package uk.ac.ox.oxfish.fisher.equipment.gear;

import org.junit.Test;
import uk.ac.ox.oxfish.biology.EmptyLocalBiology;
import uk.ac.ox.oxfish.biology.GlobalBiology;
import uk.ac.ox.oxfish.biology.LocalBiology;
import uk.ac.ox.oxfish.biology.Species;
import uk.ac.ox.oxfish.biology.complicated.StockAssessmentCaliforniaMeristics;
import uk.ac.ox.oxfish.biology.complicated.StructuredAbundance;
import uk.ac.ox.oxfish.fisher.Fisher;
import uk.ac.ox.oxfish.fisher.equipment.Catch;
import uk.ac.ox.oxfish.geography.SeaTile;
import uk.ac.ox.oxfish.geography.habitat.TileHabitat;
import uk.ac.ox.oxfish.utility.Pair;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static uk.ac.ox.oxfish.utility.FishStateUtilities.FEMALE;
import static uk.ac.ox.oxfish.utility.FishStateUtilities.MALE;

/**
 * Created by carrknight on 3/22/17.
 */
public class GarbageGearDecoratorTest {



    @Test
    public void fishEmpty() throws Exception {
        Species first = new Species("First");
        Species second = new Species("Second");
        GlobalBiology biology = new GlobalBiology(first, second);
        LocalBiology local = new EmptyLocalBiology();
        SeaTile where = new SeaTile(0, 0, -100, new TileHabitat(0d));
        where.setBiology(local);

        Gear gear = new GarbageGearDecorator(second,1.5,new OneSpecieGear(first,.5));
        Catch fishCaught = gear.fish(mock(Fisher.class), where, 1 , biology);

        assertEquals(fishCaught.getWeightCaught(first), 0, .001);
        assertEquals(fishCaught.getWeightCaught(second), 0, .001);

    }

    @Test
    public void fishOnlyWhatIsAvailable()
    {
        Species first = new Species("First");
        Species second = new Species("Second");
        GlobalBiology biology = new GlobalBiology(first, second);
        LocalBiology local = mock(LocalBiology.class);
        when(local.getBiomass(first)).thenReturn(100.0);
        when(local.getBiomass(second)).thenReturn(0.0);

        SeaTile where = new SeaTile(0,0,-100, new TileHabitat(0d));
        where.setBiology(local);

        Gear gear = new GarbageGearDecorator(second,2,new OneSpecieGear(first,.5));
        Catch fishCaught = gear.fish(mock(Fisher.class), where,1 , biology);

        assertEquals(fishCaught.getWeightCaught(first), 50, .001);
        assertEquals(fishCaught.getWeightCaught(second), 100, .001);
        //gear itself never calls biology reacts
        verify(local,never()).reactToThisAmountOfBiomassBeingFished(any(),any(),any());

    }


    @Test
    public void expectationKillsNoFish()
    {
        Species first = new Species("First");
        Species second = new Species("Second");
        GlobalBiology biology = new GlobalBiology(first, second);
        LocalBiology local = mock(LocalBiology.class);
        when(local.getBiomass(first)).thenReturn(100.0);
        when(local.getBiomass(second)).thenReturn(0.0);

        SeaTile where = new SeaTile(0,0,-100, new TileHabitat(0d));
        where.setBiology(local);

        Gear gear = new GarbageGearDecorator(second,2,new OneSpecieGear(first,.5));
        double[] fishCaught = gear.expectedHourlyCatch(mock(Fisher.class), where,1 , biology);

        assertEquals(fishCaught[0], 50, .001);
        assertEquals(fishCaught[1], 100.0, .001);
        //gear itself never calls biology reacts
        verify(local,never()).reactToThisAmountOfBiomassBeingFished(any(),any(),any());

    }


    @Test
    public void catchesCorrectly() throws Exception {

        Species longspine = new Species("longspine",new StockAssessmentCaliforniaMeristics(80, 40, 3, 8.573, 27.8282, 0.108505, 4.30E-06, 3.352,
                                                                                           0.111313, 3, 8.573, 27.8282, 0.108505, 4.30E-06, 3.352,
                                                                                           0.111313, 17.826, -1.79, 1,
                                                                                           0, 168434124,
                                                                                           0.6, false));
        longspine.resetIndexTo(0);
        Species imaginary = new Species("imaginary", StockAssessmentCaliforniaMeristics.FAKE_MERISTICS, true);
        imaginary.resetIndexTo(1);



        HomogeneousAbundanceGear gear1 = mock(HomogeneousAbundanceGear.class,RETURNS_DEEP_STUBS);
        int[][] catches = new int[2][81];
        catches[0][5]=1000; //total catch weight = 19.880139
        when(gear1.catchesAsAbundanceForThisSpecies(any(),anyInt(), any())).
                thenReturn(
                        new StructuredAbundance(catches[MALE], catches[FEMALE])
                );


        Gear gear = new GarbageGearDecorator(imaginary,.5,
                                             new HeterogeneousAbundanceGear(
                                                     new Pair<>(longspine, gear1)
                                             ));

        GlobalBiology biology = new GlobalBiology(longspine,imaginary);

        SeaTile mock = mock(SeaTile.class,RETURNS_DEEP_STUBS);
        when(mock.getBiology().getBiomass(any())).thenReturn(1d);
        Catch caught = gear.fish(mock(Fisher.class), mock, 1, biology);
        assertEquals(caught.getWeightCaught(0), 19.880139, .001);
        assertEquals(caught.getWeightCaught(1), 9, .001); //meristics round!
        assertEquals(caught.getTotalWeight(), 9+19.880139, .001);


    }
}