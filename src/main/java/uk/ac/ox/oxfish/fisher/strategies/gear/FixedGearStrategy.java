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

package uk.ac.ox.oxfish.fisher.strategies.gear;

import ec.util.MersenneTwisterFast;
import uk.ac.ox.oxfish.fisher.Fisher;
import uk.ac.ox.oxfish.fisher.actions.Action;
import uk.ac.ox.oxfish.model.FishState;

/**
 * Always returns the same gear every day
 * Created by carrknight on 6/13/16.
 */
public class FixedGearStrategy implements GearStrategy {
    /**
     * choose gear to use for this trip
     *
     * @param fisher        the agent making a choice
     * @param random        the randomizer
     * @param model         the model
     * @param currentAction the action that triggered a call to this strategy
     * @return the gear to use. Null can be returned to mean: "use current gear"
     */
    @Override
    public void updateGear(
            Fisher fisher, MersenneTwisterFast random, FishState model, Action currentAction) {
    }

    @Override
    public void start(FishState model, Fisher fisher) {

    }

    @Override
    public void turnOff(Fisher fisher) {

    }
}
