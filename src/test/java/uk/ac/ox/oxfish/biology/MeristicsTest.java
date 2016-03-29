package uk.ac.ox.oxfish.biology;

import org.junit.Test;
import uk.ac.ox.oxfish.biology.complicated.Meristics;

import static org.junit.Assert.assertEquals;


public class MeristicsTest {


    @Test
    public void yelloweye() throws Exception {


        Meristics yellowEye = new Meristics(100,70, 1, 18.717, 64.594, 0.047, 0.000017, 3.03,
                                            0.045, 1, 18.717, 62.265, 0.047, 0.00000977, 3.17,
                                            0.046, 38.78, -0.437, 137900, 36500,
                                            228149,
                                            0.44056, true);

        //see if age 5 was computed correctly
        assertEquals(yellowEye.getLengthFemaleInCm().get(5),26.4837518217,.001);
        assertEquals(yellowEye.getLengthMaleInCm().get(5),26.8991271545,.001);

        assertEquals(yellowEye.getWeightFemaleInKg().get(5),0.3167667645,.001);
        assertEquals(yellowEye.getWeightMaleInKg().get(5),0.365220907,.001);

        assertEquals(yellowEye.getMaturity().get(5),0.0046166415,.0001);
        assertEquals(yellowEye.getRelativeFecundity().get(5),47344.590014727,.001);
        assertEquals(yellowEye.getPhi().get(5),173.6635925757,.001);

        assertEquals(yellowEye.getCumulativeSurvivalFemale().get(5),0.7945336025,.001);
        assertEquals(yellowEye.getCumulativePhi(),8043057.98636817,.01);

    }


    @Test
    public void shortspine() throws Exception {


        Meristics shortspine = new Meristics(100, 100, 2, 7, 75, 0.018, 4.77E-06, 3.263,
                                             0.0505, 2, 7, 75, 0.018, 4.77E-06, 3.263,
                                             0.0505, 18.2, -2.3, 1, 0, 36315502,
                                             0.6, false);

        //see if age 5 was computed correctly
        assertEquals(shortspine.getLengthFemaleInCm().get(5),11.3138255265,.001);
        assertEquals(shortspine.getLengthMaleInCm().get(5),11.3138255265,.001);

        assertEquals(shortspine.getWeightFemaleInKg().get(5),0.0130770514,.001);
        assertEquals(shortspine.getWeightMaleInKg().get(5),0.0130770514,.001);

        assertEquals(shortspine.getMaturity().get(10),0.3900004207,.0001);
        assertEquals(shortspine.getRelativeFecundity().get(5), 0.0130770514, .001);
        assertEquals(shortspine.getRelativeFecundity().get(20), 0.3052767163, .001);
       assertEquals(shortspine.getCumulativeSurvivalFemale().get(5),0.7768562128,.001);
        assertEquals(shortspine.getCumulativeSurvivalFemale().get(20),0.3642189796,.001);
        assertEquals(shortspine.getPhi().get(20),0.1111875741,.001);
        assertEquals(shortspine.getCumulativePhi(),10.9714561805,.01);

    }

    @Test
    public void longspine() throws Exception {


        Meristics longspine = new Meristics(80, 40, 3, 8.573, 27.8282, 0.108505, 4.30E-06, 3.352,
                                            0.111313, 3, 8.573, 27.8282, 0.108505, 4.30E-06, 3.352,
                                            0.111313, 17.826, -1.79, 1,
                                            0, 168434124,
                                            0.6, false);

        //see if age 5 was computed correctly
        assertEquals(longspine.getLengthFemaleInCm().get(5),12.3983090675,.001);
        assertEquals(longspine.getLengthMaleInCm().get(5),12.3983090675,.001);

        assertEquals(longspine.getWeightFemaleInKg().get(5),0.019880139,.001);
        assertEquals(longspine.getWeightMaleInKg().get(5),0.019880139,.001);

        assertEquals(longspine.getMaturity().get(5),6.03332555676691E-05,.0001);
        assertEquals(longspine.getRelativeFecundity().get(5), 0.019880139, .001);
        assertEquals(longspine.getCumulativeSurvivalMale().get(5), 0.5731745408, .001);
        assertEquals(longspine.getPhi().get(5),3.45814860523815E-05,.001);

       assertEquals(longspine.getCumulativePhi(),0.5547290727,.001);

    }

    @Test
    public void sablefish() throws Exception {


        Meristics sablefish = new Meristics(59,30 , 0.5, 25.8, 56.2, 0.419, 3.6724E-06, 3.250904,
                                            0.065, 0.5, 25.8, 64, 0.335, 3.4487E-06, 3.26681,
                                            0.08, 58, -0.13, 1, 0, 40741397,
                                            0.6, false);

        //see if age 5 was computed correctly
        assertEquals(sablefish.getLengthFemaleInCm().get(5),55.5416341677,.001);
        assertEquals(sablefish.getLengthMaleInCm().get(5),51.5868143025,.001);

        assertEquals(sablefish.getWeightFemaleInKg().get(5),1.7258103959,.001);
        assertEquals(sablefish.getWeightMaleInKg().get(5),1.3559663707,.001);

        assertEquals(sablefish.getMaturity().get(5),0.4207762664,.0001);
        assertEquals(sablefish.getRelativeFecundity().get(5), 1.7258103959, .001);
        assertEquals(sablefish.getCumulativeSurvivalFemale().get(5),0.670320046,.001);
        assertEquals(sablefish.getPhi().get(5),0.4867730478,.001);
        assertEquals(sablefish.getCumulativePhi(),14.2444066772,.001);

    }


    @Test
    public void doverSole() throws Exception {


        Meristics sole = new Meristics(69,50 , 1, 9.04, 39.91, 0.1713, 0.000002231, 3.412,
                                       0.1417, 1, 5.4, 47.81, 0.1496, 0.000002805, 3.345,
                                       0.1165, 35, -0.775, 1, 0,
                                       404138330,
                                       0.8, false);

        //see if age 5 was computed correctly
        assertEquals(sole.getLengthFemaleInCm().get(5),24.5101516433,.001);
        assertEquals(sole.getLengthMaleInCm().get(5),24.3553122333,.001);

        assertEquals(sole.getWeightFemaleInKg().get(5),0.124536091,.001);
        assertEquals(sole.getWeightMaleInKg().get(5),0.120103947,.001);

        assertEquals(sole.getMaturity().get(5),0.0002945897,.0001);

        assertEquals(sole.getRelativeFecundity().get(5), 0.124536091, .001);
        assertEquals(sole.getCumulativeSurvivalFemale().get(5),0.5585003689, .001);
        assertEquals(sole.getPhi().get(5),2.04897288861722E-05,.001);

        assertEquals(sole.getCumulativePhi(),2.3584385374,.001);

    }


    @Test
    public void canaryRockfish() throws Exception {


        Meristics canary = new Meristics(40,20 , 1, 8.04, 52.53, 0.16, 1.55E-05, 3.03,
                                         0.06, 1, 8.04, 60.36, 0.125, 1.55E-05, 3.03,
                                         0.06, 40.5, -0.25, 1, 0,
                                         38340612,
                                         0.511, true);

        //see if age 5 was computed correctly
        assertEquals(canary.getLengthFemaleInCm().get(5),30.7375135093,.001);
        assertEquals(canary.getWeightFemaleInKg().get(5),0.4988476814,.001);

        //todo ask to steve for discrepancy
        assertEquals(canary.getLengthMaleInCm().get(5),30.1273037904,.001);
        assertEquals(canary.getWeightMaleInKg().get(5),0.4694413124,.001);

        assertEquals(canary.getMaturity().get(5),0.0801270824,.0001);
        assertEquals(canary.getRelativeFecundity().get(5), 0.4988476814, .001);
        assertEquals(canary.getCumulativeSurvivalFemale().get(5), 0.7408182207, .001);

        assertEquals(canary.getPhi().get(5),0.0296114001         ,.001);

       assertEquals(canary.getCumulativePhi(),29.024080491,.001);
    }


    @Test
    public void placeholder() throws Exception {
        Meristics placeholder = Meristics.FAKE_MERISTICS;
        assertEquals(placeholder.getWeightMaleInKg().get(0),1,.001);
        assertEquals(placeholder.getWeightFemaleInKg().get(0),1,.001);
        assertEquals(placeholder.getMaxAge(), 0, .001);


    }
}