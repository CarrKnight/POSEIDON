package uk.ac.ox.oxfish.fisher.actions.fads;

import static org.apache.sis.measure.Units.HOUR;
import static uk.ac.ox.oxfish.fisher.equipment.fads.FadManagerUtils.getFadManager;
import static uk.ac.ox.oxfish.utility.Measures.toHours;

import java.util.Optional;

import org.apache.sis.measure.Quantities;
import org.jetbrains.annotations.NotNull;
import uk.ac.ox.oxfish.fisher.Fisher;
import uk.ac.ox.oxfish.fisher.actions.ActionResult;
import uk.ac.ox.oxfish.fisher.actions.Arriving;
import uk.ac.ox.oxfish.fisher.equipment.fads.Fad;
import uk.ac.ox.oxfish.geography.SeaTile;
import uk.ac.ox.oxfish.model.FishState;
import uk.ac.ox.oxfish.model.regs.Regulation;

import javax.measure.quantity.Time;

public class PickUpFad implements FadAction {

    private final Fad targetFad;

    PickUpFad(Fad targetFad) { this.targetFad = targetFad; }

    @Override
    public ActionResult act(
        FishState model, Fisher fisher, Regulation regulation, double hoursLeft
    ) {
        if (isPossible(model, fisher)) {
            getFadManager(fisher).pickUpFad(targetFad);
            return new ActionResult(new Arriving(), hoursLeft - toHours(getDuration()));
        } else {
            // it can happen that the FAD has drifted away, in which case the fisher has to
            // reconsider its course of action
            // TODO: if the FAD has drifted away, should the fisher keep pursuing it?
            return new ActionResult(new Arriving(), hoursLeft);
        }
    }

    @Override @NotNull
    public Optional<SeaTile> getActionTile(Fisher fisher) {
        return getFadManager(fisher).getFadMap().getFadTile(targetFad);
    }

    @Override public Time getDuration() {
        return Quantities.create(1, HOUR); // TODO: how long does it take to pick up a FAD?
    }

    @Override public boolean isPossible(FishState model, Fisher fisher) {
        return getActionTile(fisher)
            .filter(seaTile -> seaTile.equals(fisher.getLocation()))
            .isPresent();
    }

}
