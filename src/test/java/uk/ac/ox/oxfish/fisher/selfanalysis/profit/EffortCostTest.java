package uk.ac.ox.oxfish.fisher.selfanalysis.profit;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ox.oxfish.fisher.Fisher;
import uk.ac.ox.oxfish.fisher.log.TripRecord;
import uk.ac.ox.oxfish.model.FishState;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EffortCostTest {


    @Test
    public void costCorrect() {

        final TripRecord record = mock(TripRecord.class);
        when(record.getEffort()).thenReturn(5);
        EffortCost cost = new EffortCost(12);
        final double effortCost = cost.cost(mock(Fisher.class),
                mock(FishState.class),
                record,
                0d,
                100);

        Assert.assertEquals(effortCost,60,.001);

    }
}