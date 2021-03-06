package uk.ac.ox.oxfish.biology.initializer.factory;

import ec.util.MersenneTwisterFast;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;
import uk.ac.ox.oxfish.biology.GlobalBiology;
import uk.ac.ox.oxfish.biology.initializer.SingleSpeciesAbundanceInitializer;
import uk.ac.ox.oxfish.model.FishState;
import uk.ac.ox.oxfish.utility.parameters.FixedDoubleParameter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SingleSpeciesBoxcarFromListFactoryTest {




    @Test
    public void putsInThePopulationWeInputted() {


        //puts in the population we put out
        double[] populationArray = {1000.6980456204348, 6.121015839632853, 25.398188565908676, 66.43767691477537, 123.49416680518418, 175.51312136883783, 203.44393523581397, 207.9838081314201, 206.16942630531207, 212.77311507927917, 229.68682224793372, 251.6788443323218, 276.15140969414256, 305.3418840261694, 343.385307462176, 395.53623960448533, 471.56585072519, 594.8225897076568, 837.8882026922994, 1622.2151371353784, 2951.4561774929157, 0.0, 0.0, 0.0, 0.0};
        final SingleSpeciesAbundanceInitializer equalSpaced = buildPopulation(populationArray);
        System.out.println(Arrays.deepToString(equalSpaced.
                getInitialAbundance().getInitialAbundance()));


        assertArrayEquals(equalSpaced.
                getInitialAbundance().getInitialAbundance()[0],
                populationArray,
                .1
                );
    }

    @Test
    public void tooFewBins() {


        //puts in the population we put out
        double[] populationArray = {1000.6980456204348, 6.121015839632853};
        assertThrows(IllegalArgumentException.class,
                new ThrowingRunnable() {
                    @Override
                    public void run() throws Throwable {
                        buildPopulation(populationArray);
                    }
                }
        );

    }

    @NotNull
    private SingleSpeciesAbundanceInitializer buildPopulation(double[] populationArray) {
        final List<Double> population = new ArrayList<>();
        final FishState mock = mock(FishState.class);
        when(mock.getRandom()).thenReturn(new MersenneTwisterFast());
        for (double pop : populationArray) {
            population.add(pop);
        }
        SingleSpeciesBoxcarFromListFactory control2 = new SingleSpeciesBoxcarFromListFactory();
        control2.setCmPerBin(5);
        control2.setLInfinity(new FixedDoubleParameter(100));
        control2.setVirginRecruits(new FixedDoubleParameter(1000));
        control2.setInitialNumbersInEachBin(population);
        final SingleSpeciesAbundanceInitializer equalSpaced = control2.apply(mock);
        final GlobalBiology globalBiology = equalSpaced.generateGlobal(new MersenneTwisterFast(), mock);
        equalSpaced.getInitialAbundance().initialize(globalBiology.getSpecie(0));
        return equalSpaced;
    }
}