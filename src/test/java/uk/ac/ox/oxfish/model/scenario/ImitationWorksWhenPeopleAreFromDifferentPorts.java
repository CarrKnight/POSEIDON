package uk.ac.ox.oxfish.model.scenario;

import org.junit.Test;
import uk.ac.ox.oxfish.biology.initializer.factory.LinearGetterBiologyFactory;
import uk.ac.ox.oxfish.fisher.Fisher;
import uk.ac.ox.oxfish.geography.mapmakers.SimpleMapInitializerFactory;
import uk.ac.ox.oxfish.geography.ports.TwoPortsFactory;
import uk.ac.ox.oxfish.model.FishState;
import uk.ac.ox.oxfish.model.network.EquidegreeBuilder;
import uk.ac.ox.oxfish.utility.parameters.FixedDoubleParameter;

import java.util.DoubleSummaryStatistics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by carrknight on 7/3/17.
 */
public class ImitationWorksWhenPeopleAreFromDifferentPorts {


    @Test
    public void twoPopulations() throws Exception {


        //even though people have friends in other ports, they don't just up and leave the best spot which
        // is just next to their OWN port

        TwoPopulationsScenario scenario = new TwoPopulationsScenario();
        scenario.setAllowFriendshipsBetweenPorts(true);
        scenario.setSmallFishers(0);
        scenario.setLargeFishers(100);

        EquidegreeBuilder networkBuilder = new EquidegreeBuilder();
        networkBuilder.setDegree(new FixedDoubleParameter(10)); //make imitation way faster this way
        scenario.setNetworkBuilder(networkBuilder);

        ((SimpleMapInitializerFactory) scenario.getMapInitializer()).setCoastalRoughness(new FixedDoubleParameter(0));

        LinearGetterBiologyFactory biologyInitializer = new LinearGetterBiologyFactory();
        biologyInitializer.setIntercept(new FixedDoubleParameter(10000));
        scenario.setBiologyInitializer(biologyInitializer);

        TwoPortsFactory ports = new TwoPortsFactory();
        ports.setNamePort1("port1");
        ports.setNamePort2("port2");
        scenario.setPorts(ports);

        FishState state = new FishState(System.currentTimeMillis());
        state.setScenario(scenario);
        state.start();

        state.schedule.step(state);

        // make sure there are people in both ports!
        int port1= 0;
        int port2 = 0;
        for(Fisher fisher : state.getFishers()) {
            if (fisher.getHomePort().getName().equals("port1"))
                port1++;
            else
                port2++;
        }
        assertTrue(port1>0);
        assertTrue(port2>0);

        //make sure people have friends in opposite ports
        int crossFriendships = 0;
        for(Fisher fisher : state.getFishers())
            for(Fisher friend : fisher.getDirectedFriends())
                if(fisher.getHomePort() != friend.getHomePort())
                    crossFriendships++;


        assertTrue(crossFriendships>0);


        //step for 100 days
        for(int i=0; i<300; i++)
            state.schedule.step(state);


        DoubleSummaryStatistics averageXFishedPort1 = new DoubleSummaryStatistics();
        DoubleSummaryStatistics averageXFishedPort2= new DoubleSummaryStatistics();
        DoubleSummaryStatistics averageYFishedPort1= new DoubleSummaryStatistics();
        DoubleSummaryStatistics averageYFishedPort2= new DoubleSummaryStatistics();

        //gather data:
        for(Fisher fisher : state.getFishers()) {
            if (fisher.getHomePort().getName().equals("port1"))
            {

                averageXFishedPort1.accept(fisher.getLastFinishedTrip().getMostFishedTileInTrip().getGridX());
                averageYFishedPort1.accept(fisher.getLastFinishedTrip().getMostFishedTileInTrip().getGridY());
            }
            else {
                averageXFishedPort2.accept(fisher.getLastFinishedTrip().getMostFishedTileInTrip().getGridX());
                averageYFishedPort2.accept(fisher.getLastFinishedTrip().getMostFishedTileInTrip().getGridY());
            }
        }

        double x1 = averageXFishedPort1.getAverage();
        double x2 = averageXFishedPort2.getAverage();
        double y1 = averageYFishedPort1.getAverage();
        double y2 = averageYFishedPort2.getAverage();

        System.out.println(x1);
        System.out.println(x2);
        System.out.println(y1);
        System.out.println(y2);

        assertEquals(x1,39,2);
        assertEquals(x2,39,2);
        assertEquals(y1,0,2);
        assertEquals(y2,49,2);
    }
}