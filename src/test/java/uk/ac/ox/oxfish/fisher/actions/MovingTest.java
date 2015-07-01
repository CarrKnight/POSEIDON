package uk.ac.ox.oxfish.fisher.actions;

import ec.util.MersenneTwisterFast;
import org.junit.Test;
import sim.field.geo.GeomGridField;
import sim.field.geo.GeomVectorField;
import sim.field.grid.ObjectGrid2D;
import uk.ac.ox.oxfish.fisher.Fisher;
import uk.ac.ox.oxfish.fisher.Port;
import uk.ac.ox.oxfish.fisher.equipment.Boat;
import uk.ac.ox.oxfish.fisher.equipment.Gear;
import uk.ac.ox.oxfish.fisher.equipment.Hold;
import uk.ac.ox.oxfish.fisher.strategies.RandomThenBackToPortDestinationStrategyTest;
import uk.ac.ox.oxfish.fisher.strategies.departing.DepartingStrategy;
import uk.ac.ox.oxfish.fisher.strategies.departing.FixedProbabilityDepartingStrategy;
import uk.ac.ox.oxfish.fisher.strategies.destination.DestinationStrategy;
import uk.ac.ox.oxfish.fisher.strategies.destination.FavoriteDestinationStrategy;
import uk.ac.ox.oxfish.fisher.strategies.fishing.FishingStrategy;
import uk.ac.ox.oxfish.geography.CartesianDistance;
import uk.ac.ox.oxfish.geography.EquirectangularDistance;
import uk.ac.ox.oxfish.geography.NauticalMap;
import uk.ac.ox.oxfish.geography.SeaTile;
import uk.ac.ox.oxfish.model.FishState;
import uk.ac.ox.oxfish.model.market.Markets;
import uk.ac.ox.oxfish.model.regs.Anarchy;
import uk.ac.ox.oxfish.model.regs.factory.AnarchyFactory;

import java.util.Queue;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


public class MovingTest
{
    @Test
    public void movingOverMultipleSteps() throws Exception {


        //2 by 2 map:
        FishState fishState = RandomThenBackToPortDestinationStrategyTest.generateSimple2x2Map();
        fishState.getMap().setDistance(new CartesianDistance(3)); //3 km per map
        //1 hour step
        when(fishState.getHoursPerStep()).thenReturn(1d);
        //fake port at 1,1
        Port port = new Port(fishState.getMap().getSeaTile(1,1),mock(Markets.class)  );

        //create fisher, it wants to go to 0,1 from 1,1
        //but it only goes at 1km per hour
        //so it should take 3 steps
        Gear gear = mock(Gear.class);
        Fisher fisher = new Fisher(0, port,
                                     new MersenneTwisterFast(),
                                     new AnarchyFactory().apply(fishState),
                                     new FixedProbabilityDepartingStrategy(1.0),
                                     new FavoriteDestinationStrategy(fishState.getMap().getSeaTile(0, 1)),
                                     new FishingStrategy() {
                                         @Override
                                         public boolean shouldFish(Fisher fisher, MersenneTwisterFast random,
                                                                   FishState model) {
                                             return true;
                                         }

                                         @Override
                                         public void start(FishState model) {

                                         }

                                         @Override
                                         public void turnOff() {

                                         }
                                     },
                                     new Boat(1,1,1),
                                     new Hold(100.0, 1), gear);
        //starts at port!
        assertEquals(fishState.getMap().getSeaTile(1, 1), fisher.getLocation());

        fisher.step(fishState);
        //still at port
        assertEquals(fishState.getMap().getSeaTile(1,1),fisher.getLocation());

        //one more step, still at port!
        fisher.step(fishState);
        assertEquals(fishState.getMap().getSeaTile(1,1),fisher.getLocation());

        //final step, gooone!
        fisher.step(fishState);
        assertEquals(fishState.getMap().getSeaTile(0,1),fisher.getLocation());


    }




//path from A to A is empty


    @Test
    public void pathToItselfIsEmpty() throws Exception {

        FishState simple = generateSimple4x4Map();
        Moving move = new Moving();

        NauticalMap map = simple.getMap();
        Queue<SeaTile> route = move.getRoute(map, map.getSeaTile(2, 2), map.getSeaTile(2, 2));
        assertTrue(route.isEmpty());


    }

    @Test
    public void moveInPlace() throws Exception {

        FishState simple = generateSimple4x4Map();
        Moving move = new Moving();
        NauticalMap map = simple.getMap();

        Fisher fisher = mock(Fisher.class);
        when(fisher.getDestination()).thenReturn(map.getSeaTile(0,0));
        when(fisher.getLocation()).thenReturn(map.getSeaTile(0, 0));

        ActionResult result = move.act(simple, fisher, new Anarchy(),24 );
        verify(fisher,never()).move(any(),any(),any()); //never moved
        assertTrue(result.isActAgainThisTurn()); //think he has arrived
        assertTrue(result.getNextState() instanceof Arriving);


    }

    @Test
    public void moveAllTheWay() throws Exception {
        FishState simple = generateSimple4x4Map();
        Moving move = new Moving();
        NauticalMap map = simple.getMap();
        map.setDistance(new CartesianDistance(1.0));

        //lots of crap to initialize.
        Port port = mock(Port.class); when(port.getLocation()).thenReturn(map.getSeaTile(0, 0));
        DestinationStrategy strategy = mock(DestinationStrategy.class);
        when(strategy.chooseDestination(any(),any(),any(),any())).thenReturn(map.getSeaTile(2, 0));

        Fisher fisher = new Fisher(0, port,
                                     new MersenneTwisterFast(), new Anarchy(),
                                     mock(DepartingStrategy.class),
                                     strategy, mock(FishingStrategy.class), new Boat(0.1,1,1), mock(Hold.class),
                                     mock(Gear.class) );

        //should move and spend 20 hours doing so
        move.act(simple, fisher, new Anarchy(),24);
        assertEquals(fisher.getHoursTravelledToday(), 20, .001);
        assertEquals(fisher.getLocation(), map.getSeaTile(2, 0));

    }


    @Test
    public void movePartially() throws Exception {
        FishState simple = generateSimple4x4Map();
        Moving move = new Moving();
        NauticalMap map = simple.getMap();
        map.setDistance(new CartesianDistance(2.0));

        //lots of crap to initialize.
        Port port = mock(Port.class); when(port.getLocation()).thenReturn(map.getSeaTile(0, 0));
        DestinationStrategy strategy = mock(DestinationStrategy.class);
        when(strategy.chooseDestination(any(), any(), any(), any())).thenReturn(map.getSeaTile(2, 0));
        Fisher fisher = new Fisher(0, port, new MersenneTwisterFast(), new Anarchy(), null, strategy,
                                     mock(FishingStrategy.class),
                                     new Boat(0.1,1,1), mock(Hold.class), mock(Gear.class) );


        //should move and spend 20 hours doing so
        move.act(simple,fisher,new Anarchy() ,24);
        assertEquals(fisher.getHoursTravelledToday(), 20, .001);
        assertEquals(fisher.getLocation(),map.getSeaTile(1, 0));

    }

    @Test
    public void simpleHorizontalPath() throws Exception {

        FishState simple = generateSimple4x4Map();
        Moving move = new Moving();

        NauticalMap map = simple.getMap();
        Queue<SeaTile> route = move.getRoute(map, map.getSeaTile(0, 2), map.getSeaTile(2, 2));
        assertEquals(route.size(),2);
        assertEquals(route.poll(),map.getSeaTile(1,2));
        assertEquals(route.poll(), map.getSeaTile(2, 2));


    }


    @Test
    public void simpleVerticalPath() throws Exception {

        FishState simple = generateSimple4x4Map();
        Moving move = new Moving();

        NauticalMap map = simple.getMap();
        Queue<SeaTile> route = move.getRoute(map, map.getSeaTile(2, 0), map.getSeaTile(2, 2));
        assertEquals(route.size(),2);
        assertEquals(route.poll(),map.getSeaTile(2, 1));
        assertEquals(route.poll(),map.getSeaTile(2, 2));


    }


    @Test
    public void simpleDiagonalPath() throws Exception {

        FishState simple = generateSimple4x4Map();
        Moving move = new Moving();

        NauticalMap map = simple.getMap();
        Queue<SeaTile> route = move.getRoute(map, map.getSeaTile(0, 0), map.getSeaTile(2, 2));
        assertEquals(route.size(),2);
        assertEquals(route.poll(),map.getSeaTile(1, 1));
        assertEquals(route.poll(),map.getSeaTile(2, 2));


    }

    @Test
    public void diagonalFirstPath(){
        FishState simple = generateSimple4x4Map();
        Moving move = new Moving();

        NauticalMap map = simple.getMap();
        Queue<SeaTile> route = move.getRoute(map, map.getSeaTile(0, 0), map.getSeaTile(2, 3));
        assertEquals(route.size(),3);
        assertEquals(route.poll(),map.getSeaTile(1, 1));
        assertEquals(route.poll(),map.getSeaTile(2, 2));
        assertEquals(route.poll(),map.getSeaTile(2, 3));
    }

    //all sea tiles!
    public static FishState generateSimple4x4Map() {
        ObjectGrid2D grid2D = new ObjectGrid2D(4,4);
        //2x2, first column sea, second  column land
        for(int i=0;i<4;i++)
            for(int j=0;j<4;j++)
                grid2D.field[i][j] = new SeaTile(i,j,-100);

        //great
        NauticalMap map = new NauticalMap(new GeomGridField(grid2D),new GeomVectorField(),
                                          new EquirectangularDistance(0.0,1));
        FishState model = mock(FishState.class);
        when(model.getMap()).thenReturn(map);
        when(model.getStepsPerDay()).thenReturn(1);
        when(model.getHoursPerStep()).thenReturn(24d);
        return model;
    }



}