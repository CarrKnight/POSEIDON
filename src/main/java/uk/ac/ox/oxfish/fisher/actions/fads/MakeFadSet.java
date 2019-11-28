package uk.ac.ox.oxfish.fisher.actions.fads;

import ec.util.MersenneTwisterFast;
import uk.ac.ox.oxfish.biology.GlobalBiology;
import uk.ac.ox.oxfish.biology.LocalBiology;
import uk.ac.ox.oxfish.fisher.Fisher;
import uk.ac.ox.oxfish.fisher.actions.Action;
import uk.ac.ox.oxfish.fisher.equipment.fads.Fad;
import uk.ac.ox.oxfish.fisher.equipment.gear.fads.PurseSeineGear;
import uk.ac.ox.oxfish.geography.SeaTile;
import uk.ac.ox.oxfish.model.FishState;

import java.util.Optional;

import static uk.ac.ox.oxfish.fisher.equipment.fads.FadManagerUtils.getFadManager;

public class MakeFadSet extends SetAction {

    public static final String TOTAL_NUMBER_OF_FAD_SETS = "Total number of FAD sets";
    private Fad targetFad;

    public MakeFadSet(PurseSeineGear purseSeineGear, MersenneTwisterFast rng, Fad targetFad) {
        super(purseSeineGear, rng);
        this.targetFad = targetFad;
    }

    @Override public String actionName() { return "FAD sets"; }

    @Override
    public boolean isPossible(FishState model, Fisher fisher) {
        return super.isPossible(model, fisher) && isFadHere(fisher);
    }

    private boolean isFadHere(Fisher fisher) {
        return getActionTile(fisher)
            .filter(fadTile -> fadTile.equals(fisher.getLocation()))
            .isPresent();
    }

    @Override
    public Optional<SeaTile> getActionTile(Fisher fisher) {
        return getFadManager(fisher).getFadMap().getFadTile(targetFad);
    }

    @Override public Action actionAfterSet() { return new PickUpFad(targetFad); }

    /**
     * When making a FAD set, the target biology is the biology of the target FAD.
     * Fish has already been removed from the underlying sea tiles while the FAD
     * was drifting so we don't need to do that now.
     */
    @Override public LocalBiology targetBiology(
        PurseSeineGear purseSeineGear, GlobalBiology globalBiology, LocalBiology seaTileBiology, MersenneTwisterFast rng
    ) {
        return targetFad.getBiology();
    }

    /**
     * When a FAD set fails, the fish is returned to the underlying sea tile biology.
     */
    @Override public void reactToFailedSet(FishState model, SeaTile locationOfSet) {
        targetFad.releaseFish(model.getBiology().getSpecies(), locationOfSet.getBiology());
    }

}
