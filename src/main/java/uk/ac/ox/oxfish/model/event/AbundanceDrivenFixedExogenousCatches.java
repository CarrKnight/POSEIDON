package uk.ac.ox.oxfish.model.event;

import uk.ac.ox.oxfish.biology.Species;
import uk.ac.ox.oxfish.fisher.equipment.Catch;
import uk.ac.ox.oxfish.fisher.equipment.gear.HeterogeneousAbundanceGear;
import uk.ac.ox.oxfish.fisher.equipment.gear.HomogeneousAbundanceGear;
import uk.ac.ox.oxfish.fisher.equipment.gear.components.FixedProportionFilter;
import uk.ac.ox.oxfish.geography.SeaTile;
import uk.ac.ox.oxfish.model.FishState;
import uk.ac.ox.oxfish.utility.FishStateUtilities;
import uk.ac.ox.oxfish.utility.Pair;

import java.util.Map;

/**
 * Basically you are given a number of fish to kill each year and you do that
 * on the "abundance" side of catches
 * Created by carrknight on 3/23/17.
 */
public class AbundanceDrivenFixedExogenousCatches extends AbstractExogenousCatches {


    public AbundanceDrivenFixedExogenousCatches(
            Map<Species, Double> exogenousYearlyCatchesInKg) {
        super(exogenousYearlyCatchesInKg, "Exogenous catches of ");
    }

    /**
     * simulate exogenous catch
     * @param model the model
     * @param target species to kill
     * @param tile where to kill it
     * @param step how much at most to kill
     * @return
     */
    protected Catch mortalityEvent(FishState model, Species target, SeaTile tile, double step) {
        //take it as a fixed proportion catchability (and never more than it is available anyway)
        assert tile.getBiomass(target) > FishStateUtilities.EPSILON;
        double proportionToCatch = Math.min(1,step/tile.getBiomass(target));
        //simulate the catches as a fixed proportion gear
        HomogeneousAbundanceGear simulatedGear = new HomogeneousAbundanceGear(0,
                                                                              new FixedProportionFilter(
                                                                                      proportionToCatch));
        //hide it in an heterogeneous abundance gear so that only one species at a time gets aught!
        HeterogeneousAbundanceGear gear = new HeterogeneousAbundanceGear(
                new Pair<>(target, simulatedGear)
        );
        //catch it
        Catch fish = gear.fish(null, tile, 1, model.getBiology());
        tile.reactToThisAmountOfBiomassBeingFished(fish,fish,model.getBiology());
        return fish;
    }

}