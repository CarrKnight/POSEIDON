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

package uk.ac.ox.oxfish.fisher.purseseiner.actions;

import uk.ac.ox.oxfish.fisher.Fisher;
import uk.ac.ox.oxfish.fisher.actions.Action;
import uk.ac.ox.oxfish.geography.SeaTile;
import uk.ac.ox.oxfish.model.data.monitors.regions.Locatable;

public abstract class PurseSeinerAction implements Action, Locatable {

    private final Fisher fisher;
    private final SeaTile location;
    private final int step;
    private final double duration;
    private final double value;
    private final boolean permitted;

    protected PurseSeinerAction(
        final Fisher fisher,
        final double duration,
        final double value,
        final boolean permitted
    ) {
        this.fisher = fisher;
        this.location = fisher.getLocation();
        this.step = fisher.grabState().getStep();
        this.duration = duration;
        this.value = value;
        this.permitted = permitted;
    }

    public int getStep() { return step; }

    public double getDuration() { return duration; }

    public Fisher getFisher() { return fisher; }

    public double getValue() { return value; }

    public boolean isPermitted() { return permitted; }

    @Override public SeaTile getLocation() { return location; }

}