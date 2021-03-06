/*
 *  POSEIDON, an agent-based model of fisheries
 *  Copyright (C) 2020  CoHESyS Lab cohesys.lab@gmail.com
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package uk.ac.ox.oxfish.biology.growers;

import com.google.common.collect.ImmutableList;
import ec.util.MersenneTwisterFast;
import org.jetbrains.annotations.NotNull;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;
import uk.ac.ox.oxfish.biology.BiomassLocalBiology;
import uk.ac.ox.oxfish.biology.Species;
import uk.ac.ox.oxfish.fisher.purseseiner.fads.Fad;
import uk.ac.ox.oxfish.geography.fads.FadMap;
import uk.ac.ox.oxfish.model.FishState;
import uk.ac.ox.oxfish.model.Startable;
import uk.ac.ox.oxfish.model.StepOrder;
import uk.ac.ox.oxfish.model.data.monitors.Monitor;
import uk.ac.ox.oxfish.model.data.monitors.accumulators.Accumulator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Streams.concat;
import static com.google.common.collect.Streams.stream;
import static uk.ac.ox.oxfish.biology.growers.CommonLogisticGrower.allocateBiomassProportionally;
import static uk.ac.ox.oxfish.biology.growers.DerisoSchnuteCommonGrower.allocateBiomassAtRandom;
import static uk.ac.ox.oxfish.biology.growers.IndependentLogisticBiomassGrower.logisticRecruitment;
import static uk.ac.ox.oxfish.fisher.purseseiner.fads.FadManager.maybeGetFadManager;
import static uk.ac.ox.oxfish.model.StepOrder.BIOLOGY_PHASE;
import static uk.ac.ox.oxfish.model.StepOrder.DATA_RESET;
import static uk.ac.ox.oxfish.utility.FishStateUtilities.EPSILON;

/**
 * The FadAwareLogisticGrower is like a CommonLogisticGrower, but calculates growth by using the
 * memorized biomass from the previous year instead of using the current biomass.
 * This grower takes FAD biomass into account as part of the total memorized biomass, but only
 * redistributes new biomass in ocean cells. When the growth function is called, it also adds back
 * biomass that was lost by FADs drifting out of habitable ocean cells. If no FADs are present, it
 * works just like a CommonLogisticGrower (except of course for the memorized biomass bit).
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class FadAwareLogisticGrower implements Startable, Steppable {

    private final Species species;
    private final double malthusianParameter;
    private final double distributionalWeight;
    private final List<BiomassLocalBiology> seaTileBiologies;
    private final Optional<Memorizer> memorizer;
    private Optional<Accumulator<Double>> biomassLostAccumulator;

    private Stoppable receipt;

    FadAwareLogisticGrower(
        final Species species,
        final double malthusianParameter,
        final double distributionalWeight,
        final boolean useLastYearBiomass, Iterable<BiomassLocalBiology> seaTileBiologies
    ) {
        this.malthusianParameter = malthusianParameter;
        this.species = species;
        this.distributionalWeight = distributionalWeight;
        this.seaTileBiologies = ImmutableList.copyOf(seaTileBiologies);
        this.memorizer = useLastYearBiomass ? Optional.of(new Memorizer()) : Optional.empty();
    }

    @SuppressWarnings("UnstableApiUsage") @Override
    public void start(FishState fishState) {
        // Schedule the grower to every year during the biology phase
        checkState(receipt == null, "Already started!");
        receipt = fishState.scheduleEveryYear(this, BIOLOGY_PHASE);

        // There is a single biomass lost monitor that every FAD manager tells
        // about biomass lost, but we don't have a global reference to it,
        // so we need to grab it from a FadManager, but it doesn't matter
        // which one (hence the use of Stream::findAny).
        biomassLostAccumulator = fishState.getFishers()
            .stream()
            .flatMap(fisher ->
                stream(maybeGetFadManager(fisher))
                    .flatMap(fadManager -> stream(fadManager.getBiomassLostMonitor()))
                    .flatMap(monitor -> stream(monitor.getSubMonitor(species)))
            )
            .findAny()
            .map(Monitor::getAccumulator);

        memorizer.ifPresent(memorizer -> memorizer.start(fishState));
    }

    @Override public void step(SimState simState) {

        final FishState fishState = (FishState) simState;

        System.out.printf("Growing %s biomass at day %d\n", species.getName(), fishState.getDay());

        // the total carrying capacity (K) is the sum of the carrying capacities of all sea tiles
        final double totalCapacity =
            seaTileBiologies.stream().mapToDouble(biology -> biology.getCarryingCapacity(species)).sum();

        // the current biomass is the sum of biomass in all local habitats, including sea tiles and FADs
        final double currentBiomass =
            allBiologies(fishState).mapToDouble(biology -> biology.getBiomass(species)).sum();

        // if we are in "Schaefer mode", the biomass we'll feed to the logistic growth
        // function is the biomass that was memorized at the beginning of the year,
        // otherwise it's just the current biomass.
        final double biomassToUse = memorizer.map(m -> m.memorizedBiomass).orElse(currentBiomass);

        // we call the logistic function (r  * biomassToUse * (1 - biomassToUse / K))
        // to get the new biomass resulting from growth and recruitment
        final double newBiomass = logisticRecruitment(biomassToUse, totalCapacity, malthusianParameter);

        // we calculate how much space we have left in the ocean to put new biomass
        final double availableCapacity = totalCapacity - currentBiomass;

        // the biomass to allocate is the sum of the new biomass and the biomass lost by FADs drifting out,
        // while making sure we won't be exceeding the total carrying capacity of the ocean tiles
        final double biomassLostByFads = biomassLostAccumulator.map(Accumulator::get).orElse(0.0);
        final double biomassToAllocate = Math.min(newBiomass + biomassLostByFads, availableCapacity);

        // the biomass to allocate should not be negative, barring tiny floating point errors
        assert biomassToAllocate >= -EPSILON : "biomassToAllocate: " + biomassToAllocate;

        if (biomassToAllocate > EPSILON) {
            // if we have some new biomass to allocate, we distribute it among the sea tiles (not the FADs)
            allocateBiomass(biomassToAllocate, fishState.getRandom());
            final String columnName = fishState.getSpecies().get(species.getIndex()) + " Recruitment";
            fishState.getYearlyCounter().count(columnName, biomassToAllocate);
        }
    }

    private Stream<BiomassLocalBiology> allBiologies(final FishState fishState) {
        return concat(fadBiologies(fishState), seaTileBiologies.stream());
    }

    private void allocateBiomass(final double biomassToAllocate, MersenneTwisterFast rng) {
        if (distributionalWeight > 0)
            allocateBiomassProportionally(
                seaTileBiologies,
                biomassToAllocate,
                species.getIndex(),
                distributionalWeight
            );
        else
            allocateBiomassAtRandom(
                seaTileBiologies,
                biomassToAllocate,
                rng,
                species.getIndex()
            );
    }

    @NotNull @SuppressWarnings("UnstableApiUsage")
    private Stream<BiomassLocalBiology> fadBiologies(final FishState fishState) {
        return stream(Optional.ofNullable(fishState.getFadMap()))
            .flatMap(FadMap::allFads)
            .map(Fad::getBiology);
    }

    private class Memorizer implements Steppable, Startable {

        private double memorizedBiomass;

        @Override public void start(final FishState fishState) {
            final StepOrder stepOrder = DATA_RESET;
            fishState.scheduleOnce(
                __ -> {
                    step(fishState);
                    fishState.scheduleEveryYear(this, stepOrder);
                },
                stepOrder
            );

        }

        @Override public void step(final SimState simState) {
            final FishState fishState = (FishState) simState;
            memorizedBiomass =
                allBiologies(fishState)
                    .mapToDouble(biology -> biology.getBiomass(species))
                    .sum();
            System.out.printf(
                "Memorized %s biomass at day %d: %,.0f t\n",
                species.getName(),
                fishState.getDay(),
                memorizedBiomass / 1000
            );
        }

    }

}
