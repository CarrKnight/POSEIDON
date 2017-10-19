/*
 *     POSEIDON, an agent-based model of fisheries
 *     Copyright (C) 2017  CoHESyS Lab cohesys.lab@gmail.com
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package uk.ac.ox.oxfish.model.market;

import org.junit.Test;
import uk.ac.ox.oxfish.biology.Species;
import uk.ac.ox.oxfish.fisher.Fisher;
import uk.ac.ox.oxfish.model.FishState;
import uk.ac.ox.oxfish.model.regs.Anarchy;
import uk.ac.ox.oxfish.model.regs.Regulation;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;


public class FixedPriceMarketTest {


    @Test
    public void transaction() throws Exception {

        FixedPriceMarket market = new FixedPriceMarket(2.0);

        Fisher seller = mock(Fisher.class);

        market.sellFishImplementation(100.0,seller,new Anarchy(),mock(FishState.class),mock(Species.class));
        verify(seller).earn(200.0);

    }

    @Test
    public void regTransaction() throws Exception {

        FixedPriceMarket market = new FixedPriceMarket(2.0);

        Fisher seller = mock(Fisher.class);
        Regulation regulation = mock(Regulation.class);
        when(regulation.maximumBiomassSellable(any(),any(),any())).thenReturn(50.0); //can only sell 50!


        market.sellFishImplementation(100.0,seller, regulation,mock(FishState.class),mock(Species.class));
        verify(seller).earn(100.0);

    }
}