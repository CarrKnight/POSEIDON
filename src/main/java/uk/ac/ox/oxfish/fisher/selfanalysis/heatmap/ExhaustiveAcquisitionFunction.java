package uk.ac.ox.oxfish.fisher.selfanalysis.heatmap;

import uk.ac.ox.oxfish.geography.NauticalMap;
import uk.ac.ox.oxfish.geography.SeaTile;
import uk.ac.ox.oxfish.model.FishState;

import java.util.List;

/**
 * Goes through all the possible seatiles and picks the highest one
 * Created by carrknight on 6/28/16.
 */
public class ExhaustiveAcquisitionFunction  implements AcquisitionFunction
{


    /**
     * Goes through all the possible seatiles and picks the highest one
     *
     * @param map        the map to pick from
     * @param regression the geographical regression
     * @param state
     * @return a choice
     */
    @Override
    public SeaTile pick(
            NauticalMap map, GeographicalRegression regression,
            FishState state) {

        List<SeaTile> seaTiles = map.getAllSeaTilesAsList();
        return seaTiles.parallelStream().max(
                (o1, o2) -> Double.compare(
                        regression.predict(o1,state.getHoursSinceStart()),
                        regression.predict(o2,state.getHoursSinceStart()))
        ).get();


    }
}