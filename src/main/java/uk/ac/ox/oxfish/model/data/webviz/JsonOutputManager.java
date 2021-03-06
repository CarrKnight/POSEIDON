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

package uk.ac.ox.oxfish.model.data.webviz;

import com.google.common.collect.ImmutableList;
import sim.engine.SimState;
import uk.ac.ox.oxfish.model.AdditionalStartable;
import uk.ac.ox.oxfish.model.FishState;
import uk.ac.ox.oxfish.model.Startable;
import uk.ac.ox.oxfish.model.StepOrder;

import java.util.Collection;

public final class JsonOutputManager implements AdditionalStartable {

    private final int numYearsToSkip;
    private final ImmutableList<JsonOutputPlugin<?>> outputPlugins;

    JsonOutputManager(
        final int numYearsToSkip,
        final Collection<JsonOutputPlugin<?>> outputPlugins
    ) {
        this.numYearsToSkip = numYearsToSkip;
        this.outputPlugins = ImmutableList.copyOf(outputPlugins);
    }

    @Override public void start(final FishState fishState) {
        if (numYearsToSkip == 0)
            startOutputPlugins(fishState);
        else
            fishState.scheduleOnceAtTheBeginningOfYear(
                this::startOutputPlugins,
                StepOrder.DATA_RESET,
                numYearsToSkip
            );
    }

    private void startOutputPlugins(SimState simState) {
        final FishState fishState = (FishState) simState;
        outputPlugins.forEach(plugin -> plugin.start(fishState));
    }

    @Override public void turnOff() {
        outputPlugins.forEach(Startable::turnOff);
    }

}
