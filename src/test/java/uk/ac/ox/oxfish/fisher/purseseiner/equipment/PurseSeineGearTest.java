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

package uk.ac.ox.oxfish.fisher.purseseiner.equipment;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import ec.util.MersenneTwisterFast;
import org.junit.Before;
import org.junit.Test;
import sim.util.Int2D;
import uk.ac.ox.oxfish.biology.BiomassLocalBiology;
import uk.ac.ox.oxfish.biology.GlobalBiology;
import uk.ac.ox.oxfish.biology.LocalBiology;
import uk.ac.ox.oxfish.fisher.Fisher;
import uk.ac.ox.oxfish.fisher.equipment.Boat;
import uk.ac.ox.oxfish.fisher.equipment.Catch;
import uk.ac.ox.oxfish.fisher.equipment.Hold;
import uk.ac.ox.oxfish.fisher.equipment.gear.FixedProportionGear;
import uk.ac.ox.oxfish.fisher.equipment.gear.Gear;
import uk.ac.ox.oxfish.fisher.purseseiner.actions.AbstractFadSetAction;
import uk.ac.ox.oxfish.fisher.purseseiner.actions.DolphinSetAction;
import uk.ac.ox.oxfish.fisher.purseseiner.actions.NonAssociatedSetAction;
import uk.ac.ox.oxfish.fisher.purseseiner.fads.FadManager;
import uk.ac.ox.oxfish.geography.SeaTile;
import uk.ac.ox.oxfish.model.FishState;

import javax.measure.Quantity;
import javax.measure.quantity.Time;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static tech.units.indriya.quantity.Quantities.getQuantity;
import static tech.units.indriya.unit.Units.HOUR;
import static uk.ac.ox.oxfish.fisher.purseseiner.equipment.PurseSeineGear.getPurseSeineGear;
import static uk.ac.ox.oxfish.fisher.purseseiner.equipment.PurseSeineGear.maybeGetPurseSeineGear;
import static uk.ac.ox.oxfish.utility.FishStateUtilities.EPSILON;

public class PurseSeineGearTest {

    private PurseSeineGear purseSeineGear;

    @Before
    public void setUp() {
        final FishState fishState = mock(FishState.class);
        when(fishState.getBiology()).thenReturn(new GlobalBiology());
        purseSeineGear = new PurseSeineGear(
            mock(FadManager.class),
            1,
            1,
            0,
            1,
            1,
            0,
            1,
            ImmutableMap.of(),
            ImmutableList.of()
        );
    }

    @Test
    public void testGetPurseSeineGear() {
        final Fisher fisher = mock(Fisher.class);
        when(fisher.getGear()).thenReturn(mock(Gear.class));
        assertEquals(Optional.empty(), maybeGetPurseSeineGear(fisher));
        assertThrows(IllegalArgumentException.class, () -> getPurseSeineGear(fisher));
        when(fisher.getGear()).thenReturn(purseSeineGear);
        assertEquals(Optional.of(purseSeineGear), maybeGetPurseSeineGear(fisher));
        assertEquals(purseSeineGear, getPurseSeineGear(fisher));
    }

    @Test
    public void testGetFuelConsumptionPerHourOfFishing() {
        assertEquals(
            0.0,
            purseSeineGear.getFuelConsumptionPerHourOfFishing(
                mock(Fisher.class),
                mock(Boat.class),
                mock(SeaTile.class)
            ),
            EPSILON
        );
    }

    @Test
    public void testFishing() {
        final GlobalBiology globalBiology =
            GlobalBiology.genericListOfSpecies(2);
        final LocalBiology localBiology =
            new BiomassLocalBiology(new double[]{1, 1}, new double[]{1, 1});
        final SeaTile seaTile = mock(SeaTile.class);
        final Fisher fisher = mock(Fisher.class);
        final Hold hold = mock(Hold.class);
        when(fisher.getHold()).thenReturn(hold);
        when(hold.getMaximumLoad()).thenReturn(2.0);
        when(hold.getTotalWeightOfCatchInHold()).thenReturn(0.0);

        final Catch fishCaught1 = purseSeineGear.fish(
            fisher,
            localBiology,
            seaTile,
            0,
            globalBiology
        );
        assertEquals(1.0, fishCaught1.getWeightCaught(0), EPSILON);
        assertEquals(1.0, fishCaught1.getWeightCaught(1), EPSILON);

        when(hold.getMaximumLoad()).thenReturn(1.0);
        final Catch fishCaught2 = purseSeineGear.fish(
            fisher,
            localBiology,
            seaTile,
            0,
            globalBiology
        );
        assertEquals(0.5, fishCaught2.getWeightCaught(0), EPSILON);
        assertEquals(0.5, fishCaught2.getWeightCaught(1), EPSILON);

    }

    @Test
    public void expectedHourlyCatchNotSupported() {
        assertThrows(UnsupportedOperationException.class, () ->
            purseSeineGear.expectedHourlyCatch(null, null, 0, null)
        );
    }

    @Test
    public void isSame() {
        assertTrue(purseSeineGear.isSame(purseSeineGear));
        assertFalse(purseSeineGear.isSame(null));
        assertFalse(purseSeineGear.isSame(new FixedProportionGear(0)));
        assertTrue(purseSeineGear.isSame(purseSeineGear.makeCopy()));
        assertTrue(purseSeineGear.isSame(new PurseSeineGear(
            purseSeineGear.getFadManager(),
            1,
            1,
            0.1,
            1,
            1,
            0.1,
            purseSeineGear.getSuccessfulFadSetProbability(),
            purseSeineGear.getCatchSamplers(),
            purseSeineGear.getAttractionFields()
        )));
        assertFalse(purseSeineGear.isSame(new PurseSeineGear(
            mock(FadManager.class),
            1,
            1,
            0.1,
            1,
            1,
            0.1,
            purseSeineGear.getSuccessfulFadSetProbability(),
            purseSeineGear.getCatchSamplers(),
            purseSeineGear.getAttractionFields()
        )));
        assertFalse(purseSeineGear.isSame(new PurseSeineGear(
            purseSeineGear.getFadManager(),
            2,
            1,
            0.1,
            1,
            1,
            0.1,
            purseSeineGear.getSuccessfulFadSetProbability(),
            purseSeineGear.getCatchSamplers(),
            purseSeineGear.getAttractionFields()
        )));
        assertFalse(purseSeineGear.isSame(new PurseSeineGear(
            purseSeineGear.getFadManager(),
            1,
            2,
            0.1,
            1,
            1,
            0.1,
            purseSeineGear.getSuccessfulFadSetProbability(),
            purseSeineGear.getCatchSamplers(),
            purseSeineGear.getAttractionFields()
        )));
        assertFalse(purseSeineGear.isSame(new PurseSeineGear(
            purseSeineGear.getFadManager(),
            1,
            1,
            0.2,
            1,
            1,
            0.1,
            purseSeineGear.getSuccessfulFadSetProbability(),
            purseSeineGear.getCatchSamplers(),
            purseSeineGear.getAttractionFields()
        )));
        assertFalse(purseSeineGear.isSame(new PurseSeineGear(
            purseSeineGear.getFadManager(),
            1,
            1,
            2,
            1,
            1,
            0.1,
            purseSeineGear.getSuccessfulFadSetProbability(),
            purseSeineGear.getCatchSamplers(),
            purseSeineGear.getAttractionFields()
        )));
        assertFalse(purseSeineGear.isSame(new PurseSeineGear(
            purseSeineGear.getFadManager(),
            1,
            1,
            0.1,
            1,
            1,
            0.1,
            0,
            purseSeineGear.getCatchSamplers(),
            purseSeineGear.getAttractionFields()
        )));
    }

    @Test
    public void testNextSetDuration() {
        final MersenneTwisterFast rng = new MersenneTwisterFast();
        final Quantity<Time> h = getQuantity(1.0, HOUR);
        assertEquals(h, purseSeineGear.nextSetDuration(NonAssociatedSetAction.class, rng));
        assertEquals(h, purseSeineGear.nextSetDuration(DolphinSetAction.class, rng));
        assertEquals(h, purseSeineGear.nextSetDuration(AbstractFadSetAction.class, rng));
    }

    @Test
    public void testVisits() {
        assertEquals(Optional.empty(), purseSeineGear.getLastVisit(new Int2D(0, 0)));
        purseSeineGear.recordVisit(new Int2D(0, 0), 0);
        assertEquals(Optional.of(0), purseSeineGear.getLastVisit(new Int2D(0, 0)));
        assertEquals(Optional.empty(), purseSeineGear.getLastVisit(new Int2D(1, 1)));
        purseSeineGear.recordVisit(new Int2D(0, 0), 1);
        assertEquals(Optional.of(1), purseSeineGear.getLastVisit(new Int2D(0, 0)));
    }

}