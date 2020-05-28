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

package uk.ac.ox.oxfish.experiments.tuna;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import sim.engine.Steppable;
import uk.ac.ox.oxfish.fisher.equipment.gear.factory.PurseSeineGearFactory;
import uk.ac.ox.oxfish.fisher.equipment.gear.fads.PurseSeineGear;
import uk.ac.ox.oxfish.model.FishState;
import uk.ac.ox.oxfish.model.StepOrder;
import uk.ac.ox.oxfish.model.data.monitors.loggers.PurseSeineActionsLogger;
import uk.ac.ox.oxfish.model.regs.fads.ActionSpecificRegulation;
import uk.ac.ox.oxfish.model.regs.fads.ActiveFadLimits;
import uk.ac.ox.oxfish.model.regs.fads.SetLimitsFactory;
import uk.ac.ox.oxfish.model.scenario.TunaScenario;
import uk.ac.ox.oxfish.utility.AlgorithmFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static com.google.common.collect.Streams.stream;
import static java.util.stream.Stream.concat;
import static uk.ac.ox.oxfish.model.regs.fads.ActiveFadLimitsFactory.iattcLimits;
import static uk.ac.ox.oxfish.model.regs.fads.ActiveFadLimitsFactory.makeLimit;
import static uk.ac.ox.oxfish.utility.FishStateUtilities.entry;

@SuppressWarnings("UnstableApiUsage")
public class Slice1Sweeps {

    private static final Path basePath =
        Paths.get(System.getProperty("user.home"), "workspace", "tuna", "np");

    private static final Path scenarioPath =
        basePath.resolve(Paths.get("runs", "gatherers_test", "tuna_calibrated.yaml"));

    private static final Path outputPath =
        basePath.resolve(Paths.get("runs", "2020-05-26"));

    private static final int NUM_RUNS_PER_POLICY = 10;
    private static final int NUM_YEARS_TO_RUN = 6;

    public static void main(String[] args) {

        final ActionSpecificRegulation currentFadLimits = new ActiveFadLimits(iattcLimits);

        final ActionSpecificRegulation smallerFadLimits = new ActiveFadLimits(ImmutableList.of(
            makeLimit(ImmutableSet.of(6), v -> v >= 1200, 115),
            makeLimit(ImmutableSet.of(6), v -> v < 1200, 75),
            makeLimit(ImmutableSet.of(4, 5), __ -> true, 30),
            makeLimit(ImmutableSet.of(1, 2, 3), __ -> true, 20)
        ));

        final ImmutableMap<AlgorithmFactory<? extends ActionSpecificRegulation>, String> fadLimits = ImmutableMap.of(
            __ -> currentFadLimits, "Current FAD limits",
            __ -> smallerFadLimits, "Strict FAD limits"
        );

        final ImmutableMap<Optional<AlgorithmFactory<? extends ActionSpecificRegulation>>, String> setLimits =
            concat(
                Stream.of(0, 25, 50, 75).map(Optional::of),
                Stream.of(Optional.<Integer>empty())
            ).collect(toImmutableMap(
                opt -> opt.map(SetLimitsFactory::new),
                opt -> opt.map(limit -> limit + " sets limit").orElse("No set limit")
            ));

        final ImmutableList<AlgorithmFactory<? extends ActionSpecificRegulation>> businessAsUsual =
            ImmutableList.of(__ -> currentFadLimits);

        ImmutableMap.Builder<String, Consumer<TunaScenario>> policies = ImmutableMap.builder();
        fadLimits.forEach((activeFadLimits, fadLimitsName) ->
            setLimits.forEach((generalSetLimits, setLimitsName) ->
                policies.put(makePolicy(
                    businessAsUsual,
                    concat(Stream.of(activeFadLimits), stream(generalSetLimits)).collect(toImmutableList()),
                    fadLimitsName + " / " + setLimitsName
                ))
            )
        );

        new Runner<>(TunaScenario.class, scenarioPath, outputPath)
            .requestYearlyData()
            .requestFisherYearlyData()
            .registerRowProvider("action_log.csv", PurseSeineActionsLogger::new)
//            .registerRowProviders("heatmap_data.csv", fishState -> {
//                final int interval = 30;
//                ImmutableList.Builder<HeatmapGatherer> gatherers = new ImmutableList.Builder<>();
//                gatherers.add(
//                    new FadDeploymentHeatmapGatherer(interval),
//                    new FadSetHeatmapGatherer(interval),
//                    new UnassociatedSetHeatmapGatherer(interval),
//                    new FadDensityHeatmapGatherer(interval)
//                );
//                fishState.getSpecies().forEach(species -> {
//                    gatherers.add(new BiomassHeatmapGatherer(interval, species));
//                    gatherers.add(new CatchFromFadSetsHeatmapGatherer(interval, species));
//                    gatherers.add(new CatchFromUnassociatedSetsHeatmapGatherer(interval, species));
//                });
//                return gatherers.build();
//            })
            .setPolicies(policies.build())
            .run(NUM_YEARS_TO_RUN, NUM_RUNS_PER_POLICY);
    }

    private static Map.Entry<String, Consumer<TunaScenario>> makePolicy(
        List<AlgorithmFactory<? extends ActionSpecificRegulation>> businessAsUsual,
        Collection<AlgorithmFactory<? extends ActionSpecificRegulation>> policyRegulations,
        String policyName
    ) {
        Steppable setRegulations = simState -> {
            System.out.println("Changing regulations to " + policyName + " for all fishers at day " + simState.schedule.getSteps());
            final FishState fishState = (FishState) simState;
            fishState.getFishers().forEach(fisher ->
                ((PurseSeineGear) fisher.getGear()).getFadManager().setActionSpecificRegulations(
                    policyRegulations.stream().map(factory -> factory.apply(fishState))
                )
            );
        };
        return entry(policyName, scenario -> {
            PurseSeineGearFactory purseSeineGearFactory =
                (PurseSeineGearFactory) scenario.getFisherDefinition().getGear();
            purseSeineGearFactory.setActionSpecificRegulations(businessAsUsual);
            scenario.getPlugins().add(fishState ->
                __ -> fishState.scheduleOnceAtTheBeginningOfYear(setRegulations, StepOrder.AFTER_DATA, 1)
            );
        });
    }

}
