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

package uk.ac.ox.oxfish.fisher.selfanalysis.profit;

import uk.ac.ox.oxfish.fisher.Fisher;
import uk.ac.ox.oxfish.fisher.log.TripRecord;
import uk.ac.ox.oxfish.model.FishState;

/**
 * A cost computation, to be used by fishers to properly estimate profits
 * Created by carrknight on 7/12/16.
 */
public interface Cost {


    /**
     * computes and return the cost
     * @param fisher agent that did the trip
     * @param model
     * @param record the trip record
     * @param revenue revenue from catches
     * @param durationInHours this is given as an argument because when the fisher is acting for real this is called
     *                        by
     *
     * @return dollars spent
     * */
    public double cost(
            Fisher fisher, FishState model, TripRecord record, double revenue, double durationInHours);

}
