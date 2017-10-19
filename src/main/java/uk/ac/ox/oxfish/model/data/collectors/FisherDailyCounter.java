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

package uk.ac.ox.oxfish.model.data.collectors;

import sim.engine.SimState;
import uk.ac.ox.oxfish.biology.Species;

/**
 *
 * like a normal counter but has arrays ready to make catch data faster to read and write
 * Created by carrknight on 8/14/15.
 */
public class FisherDailyCounter extends Counter {

    private double[] landings;

    private double[] catches;

    private double[] earnings;

    public FisherDailyCounter(int numberOfSpecies) {
        super(IntervalPolicy.EVERY_DAY);
        landings = new double[numberOfSpecies];
        earnings = new double[numberOfSpecies];
        catches = new double[numberOfSpecies];
        super.addColumn(FisherYearlyTimeSeries.CASH_FLOW_COLUMN);
        super.addColumn(FisherYearlyTimeSeries.EFFORT);
    }

    @Override
    public void step(SimState simState) {
        super.step(simState);

        for(int i=0; i<landings.length; i++)
        {
            landings[i]=0;
            earnings[i]=0;
            catches[i]=0;
        }
    }

    /**
     * increment catch earnings column by this
     *
     * @param add        by how much to increment
     */
    public void countLanding(Species species, double add) {
        landings[species.getIndex()]+=add;
    }

    public void countEarnings(Species species, double add) {
        earnings[species.getIndex()]+=add;
    }

    public void countCatches(Species species, double add) {
        catches[species.getIndex()]+=add;
    }

    public double getLandingsPerSpecie(int index)
    {
        return landings[index];
    }

    public double getEarningsPerSpecie(int index)
    {
        return earnings[index];
    }

    public double getCatchesPerSpecie(int index){
        return catches[index];
    }
}
