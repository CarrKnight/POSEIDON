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

package uk.ac.ox.oxfish.fisher.equipment.gear;

import uk.ac.ox.oxfish.biology.GlobalBiology;
import uk.ac.ox.oxfish.biology.Species;
import uk.ac.ox.oxfish.fisher.Fisher;
import uk.ac.ox.oxfish.fisher.equipment.Boat;
import uk.ac.ox.oxfish.fisher.equipment.Catch;
import uk.ac.ox.oxfish.geography.SeaTile;
import uk.ac.ox.oxfish.utility.FishStateUtilities;

import java.util.Arrays;
import java.util.List;

/**
 * A gear that is given a mean and standard deviation catchability for each specie.
 * catchability is defined as q in:
 * Catches = biomass * q * hours
 * Created by carrknight on 7/29/15.
 */
public class RandomCatchabilityTrawl implements Gear
{

    private final double[]  catchabilityMeanPerSpecie;

    private final double[] catchabilityDeviationPerSpecie;


    /**
     * speed (used for fuel consumption) of thrawling
     */
    private  final double gasPerHourFished;


    public RandomCatchabilityTrawl(
            double[] catchabilityMeanPerSpecie,
            double[] catchabilityDeviationPerSpecie,
            double gasPerHourFished) {
        this.catchabilityMeanPerSpecie = catchabilityMeanPerSpecie;
        this.catchabilityDeviationPerSpecie = catchabilityDeviationPerSpecie;
        this.gasPerHourFished = gasPerHourFished;
    }

    @Override
    public Catch fish(
            Fisher fisher, SeaTile where, int hoursSpentFishing, GlobalBiology modelBiology)
    {
        return new Catch(catchesAsArray(fisher, where, hoursSpentFishing, modelBiology));
    }

    private double[] catchesAsArray(
            Fisher fisher, SeaTile where, int hoursSpentFishing, GlobalBiology modelBiology) {
        List<Species> species = modelBiology.getSpecies();
        double[] totalCatch = new double[modelBiology.getSize()];
        for(Species specie : species)
        {
            double q = fisher.grabRandomizer().nextGaussian()*catchabilityDeviationPerSpecie[specie.getIndex()]
                    + catchabilityMeanPerSpecie[specie.getIndex()];
            totalCatch[specie.getIndex()] =
                    FishStateUtilities.catchSpecieGivenCatchability(where, hoursSpentFishing, specie, q);
        }
        return totalCatch;
    }


    @Override
    public double[] expectedHourlyCatch(
            Fisher fisher, SeaTile where, int hoursSpentFishing, GlobalBiology modelBiology) {
        return catchesAsArray(fisher, where, hoursSpentFishing, modelBiology);
    }

    /**
     * get how much gas is consumed by fishing a spot with this gear
     *
     * @param fisher the dude fishing
     * @param boat
     * @param where  the location being fished  @return liters of gas consumed for every hour spent fishing
     */
    @Override
    public double getFuelConsumptionPerHourOfFishing(
            Fisher fisher, Boat boat, SeaTile where) {
        return gasPerHourFished;
    }

    public double[] getCatchabilityMeanPerSpecie() {
        return catchabilityMeanPerSpecie;
    }

    public double[] getCatchabilityDeviationPerSpecie() {
        return catchabilityDeviationPerSpecie;
    }

    @Override
    public Gear makeCopy() {
        return new RandomCatchabilityTrawl(Arrays.copyOf(catchabilityMeanPerSpecie,catchabilityMeanPerSpecie.length),
                                            Arrays.copyOf(catchabilityDeviationPerSpecie,catchabilityMeanPerSpecie.length),
                                           gasPerHourFished);
    }

    public double getGasPerHourFished() {
        return gasPerHourFished;
    }


    @Override
    public String toString() {
        return "RandomCatchabilityTrawl{" + "catchabilityMeanPerSpecie=" + Arrays.toString(
                catchabilityMeanPerSpecie) + ", catchabilityDeviationPerSpecie=" + Arrays.toString(
                catchabilityDeviationPerSpecie) + ", gasPerHourFished=" + gasPerHourFished + '}';
    }

    @Override
    public boolean isSame(Gear o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RandomCatchabilityTrawl that = (RandomCatchabilityTrawl) o;
        return Double.compare(that.getGasPerHourFished(), getGasPerHourFished()) == 0 &&
                Arrays.equals(getCatchabilityMeanPerSpecie(), that.getCatchabilityMeanPerSpecie()) &&
                Arrays.equals(getCatchabilityDeviationPerSpecie(), that.getCatchabilityDeviationPerSpecie());
    }


}
