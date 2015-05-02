package uk.ac.ox.oxfish.model.regs;

import ec.util.MersenneTwisterFast;
import org.junit.Test;
import uk.ac.ox.oxfish.fisher.Fisher;
import uk.ac.ox.oxfish.fisher.Port;
import uk.ac.ox.oxfish.fisher.actions.Action;
import uk.ac.ox.oxfish.fisher.actions.ActionResult;
import uk.ac.ox.oxfish.fisher.actions.AtPort;
import uk.ac.ox.oxfish.fisher.equipment.Boat;
import uk.ac.ox.oxfish.fisher.equipment.Gear;
import uk.ac.ox.oxfish.fisher.equipment.Hold;
import uk.ac.ox.oxfish.fisher.strategies.DepartingStrategy;
import uk.ac.ox.oxfish.fisher.strategies.DestinationStrategy;
import uk.ac.ox.oxfish.fisher.strategies.FishingStrategy;
import uk.ac.ox.oxfish.geography.SeaTile;
import uk.ac.ox.oxfish.model.FishState;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;


public class RegulationsTest {


    //if the regulations never allow to go out, you don't go out


    @Test
    public void neverGoOut() throws Exception {

        Fisher fisher = mock(Fisher.class);
        Regulations regulations = mock(Regulations.class);
        when(regulations.allowedAtSea(any(),any() )).thenReturn(false); //you can't go out!

        AtPort port = new AtPort();
        when(fisher.shouldFisherLeavePort(any())).thenReturn(true); //the fisher wants to go out!
        ActionResult act = port.act(mock(FishState.class), fisher,regulations );

        //must be still at port!
        assertTrue(act.getNextState() instanceof AtPort);
        assertFalse(act.isActAgainThisTurn());
        //in fact it should have never even checked if I wanted to leave port
        verify(fisher,never()).shouldFisherLeavePort(any());

    }

    //update direction goes back to port if you are not allowed to leave

    @Test
    public void goBackToPort() throws Exception {
        Port port = mock(Port.class);
        SeaTile portTile = mock(SeaTile.class);
        when(port.getLocation()).thenReturn(portTile);
        DestinationStrategy destination = mock(DestinationStrategy.class);
        SeaTile destinationTile = mock(SeaTile.class);
        when(destination.chooseDestination(any(),any(),any(),any())).thenReturn(destinationTile);
        FishState model = mock(FishState.class);

        Regulations regs = mock(Regulations.class);

        Fisher fisher = new Fisher(port,new MersenneTwisterFast(),regs,
                                   mock(DepartingStrategy.class),
                                   destination,
                                   mock(FishingStrategy.class),
                                   mock(Boat.class),
                                   mock(Hold.class),
                                   mock(Gear.class));

        when(regs.allowedAtSea(fisher, model)).thenReturn(true);
        fisher.updateDestination(model,mock(Action.class));
        assertEquals(fisher.getDestination(),destinationTile);

        //if i don't allow it though it will return to home
        when(regs.allowedAtSea(fisher, model)).thenReturn(false);
        fisher.updateDestination(model, mock(Action.class));
        assertEquals(fisher.getDestination(),portTile);



    }
}