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

package uk.ac.ox.oxfish.fisher.purseseiner.equipment;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import sim.util.Int2D;
import uk.ac.ox.oxfish.biology.GlobalBiology;
import uk.ac.ox.oxfish.biology.LocalBiology;
import uk.ac.ox.oxfish.fisher.Fisher;
import uk.ac.ox.oxfish.fisher.equipment.Boat;
import uk.ac.ox.oxfish.fisher.equipment.Catch;
import uk.ac.ox.oxfish.fisher.equipment.gear.Gear;
import uk.ac.ox.oxfish.fisher.equipment.gear.HoldLimitingDecoratorGear;
import uk.ac.ox.oxfish.fisher.purseseiner.actions.AbstractSetAction;
import uk.ac.ox.oxfish.fisher.purseseiner.fads.FadManager;
import uk.ac.ox.oxfish.fisher.purseseiner.samplers.CatchSampler;
import uk.ac.ox.oxfish.fisher.purseseiner.samplers.DurationSampler;
import uk.ac.ox.oxfish.fisher.purseseiner.strategies.fields.AttractionField;
import uk.ac.ox.oxfish.geography.SeaTile;

import javax.measure.Quantity;
import javax.measure.quantity.Time;
import java.util.*;

public class PurseSeineGear implements Gear {

    private final FadManager fadManager;
    private final double successfulFadSetProbability;
    private final Map<Class<? extends AbstractSetAction>, DurationSampler> durationSamplers;
    private final Map<Class<? extends AbstractSetAction>, CatchSampler> catchSamplers;
    private final Set<AttractionField> attractionFields;
    private final Map<Int2D, Integer> lastVisits = new HashMap<>();

    public PurseSeineGear(
        FadManager fadManager,
        Map<Class<? extends AbstractSetAction>, DurationSampler> durationSamplers,
        Map<Class<? extends AbstractSetAction>, CatchSampler> catchSamplers,
        final Iterable<AttractionField> attractionFields,
        double successfulFadSetProbability
    ) {
        this.fadManager = fadManager;
        this.durationSamplers = durationSamplers;
        this.successfulFadSetProbability = successfulFadSetProbability;
        this.catchSamplers = catchSamplers;
        this.attractionFields = ImmutableSet.copyOf(attractionFields);

    }

    public static PurseSeineGear getPurseSeineGear(Fisher fisher) {
        return maybeGetPurseSeineGear(fisher).orElseThrow(() -> new IllegalArgumentException(
            "PurseSeineGear not available. Fisher " +
                fisher + " is using " + fisher.getGear().getClass() + "."
        ));
    }

    public static Optional<PurseSeineGear> maybeGetPurseSeineGear(Fisher fisher) {
        return Optional
            .of(fisher.getGear())
            .filter(gear -> gear instanceof PurseSeineGear)
            .map(gear -> (PurseSeineGear) gear);
    }

    public Set<AttractionField> getAttractionFields() { return attractionFields; }

    public Map<Class<? extends AbstractSetAction>, CatchSampler> getCatchSamplers() { return catchSamplers; }

    public double getSuccessfulFadSetProbability() {
        return successfulFadSetProbability;
    }

    public FadManager getFadManager() { return fadManager; }

    @Override
    public Catch fish(
        Fisher fisher,
        LocalBiology localBiology,
        SeaTile context,
        int hoursSpentFishing,
        GlobalBiology globalBiology
    ) {
        // Assume we catch *all* the biomass from the FAD
        final double[] catches = globalBiology.getSpecies().stream()
            .mapToDouble(localBiology::getBiomass).toArray();
        return HoldLimitingDecoratorGear.limitToHoldCapacity(new Catch(catches), fisher.getHold(), globalBiology);
    }

    @Override
    public double getFuelConsumptionPerHourOfFishing(Fisher fisher, Boat boat, SeaTile where) {
        // TODO: see if making a set should consume fuel
        return 0;
    }

    @Override
    public double[] expectedHourlyCatch(
        Fisher fisher, SeaTile where, int hoursSpentFishing, GlobalBiology modelBiology
    ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Gear makeCopy() {
        return new PurseSeineGear(
            fadManager,
            ImmutableMap.copyOf(durationSamplers),
            ImmutableMap.copyOf(catchSamplers),
            ImmutableSet.copyOf(attractionFields),
            successfulFadSetProbability
        );
    }

    @Override
    public boolean isSame(Gear o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final PurseSeineGear that = (PurseSeineGear) o;
        return Double.compare(that.successfulFadSetProbability, successfulFadSetProbability) == 0 &&
            Objects.equals(fadManager, that.fadManager) &&
            Objects.equals(durationSamplers, that.durationSamplers) &&
            Objects.equals(catchSamplers, that.catchSamplers) &&
            Objects.equals(attractionFields, that.attractionFields) &&
            Objects.equals(lastVisits, that.lastVisits);
    }

    public Quantity<Time> nextSetDuration(
        Class<? extends AbstractSetAction> actionClass
    ) {
        return durationSamplers.get(actionClass).nextDuration();
    }

    public void recordVisit(Int2D gridLocation, int timeStep) {
        lastVisits.put(gridLocation, timeStep);
    }

    public Optional<Integer> getLastVisit(Int2D gridLocation) {
        return Optional.ofNullable(lastVisits.get(gridLocation));
    }

}
