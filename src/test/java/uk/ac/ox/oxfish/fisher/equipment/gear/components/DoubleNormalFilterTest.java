package uk.ac.ox.oxfish.fisher.equipment.gear.components;

import org.junit.Test;
import uk.ac.ox.oxfish.biology.Species;
import uk.ac.ox.oxfish.biology.complicated.Meristics;
import uk.ac.ox.oxfish.utility.FishStateUtilities;

import static org.junit.Assert.assertEquals;

/**
 * Created by carrknight on 3/21/16.
 */
public class DoubleNormalFilterTest {


    @Test
    public void shortspine() throws Exception {

        Meristics shortspine = new Meristics(100, 100, 2, 7, 75, 0.018, 4.77E-06, 3.263,
                                             0.0505, 2, 7, 75, 0.018, 4.77E-06, 3.263,
                                             0.0505, 18.2, -2.3, 1, 0, 36315502,
                                             0.6, false);
        Species species = new Species("shortspine",shortspine);

        DoubleNormalFilter filter = new DoubleNormalFilter(
                true,23.53,-7,3.77,6.78,0,75,1
        );


        double[][] selectivity = filter.computeSelectivity(species);

        assertEquals(0.0026758814,selectivity[0][2],.00001);

//
    }

    @Test
    public void doverSole(){


        Meristics sole = new Meristics(69,50 , 1, 9.04, 39.91, 0.1713, 0.000002231, 3.412,
                                       0.1417, 1, 5.4, 47.81, 0.1496, 0.000002805, 3.345,
                                       0.1165, 35, -0.775, 1, 0,
                                       404138330,
                                       0.8, false);
        Species species = new Species("sole",sole);

        DoubleNormalFilter filter = new DoubleNormalFilter(
                true,38.953,-1.483,3.967,-0.764,Double.NaN,-2.259,0,50,1
        );


        double[][] selectivity = filter.computeSelectivity(species);

        assertEquals(0.798, selectivity[FishStateUtilities.FEMALE][9], .01);
    }


    @Test
    public void canaryRockfish() throws Exception {


        Meristics canary = new Meristics(40,20 , 1, 8.04, 52.53, 0.16, 1.55E-05, 3.03,
                                         0.06, 1, 8.04, 60.36, 0.125, 1.55E-05, 3.03,
                                         0.06, 40.5, -0.25, 1, 0,
                                         38340612,
                                         0.511, true);

        Species species = new Species("canary",canary);

        DoubleNormalFilter filter = new DoubleNormalFilter(
                true,39.8622,-4,3.93569,
                1.98913,
                -9,
                -0.266314,
                0,65,1
        );


        double[][] selectivity = filter.computeSelectivity(species);

        assertEquals(0.434, selectivity[FishStateUtilities.MALE][13], .001);
    }
}