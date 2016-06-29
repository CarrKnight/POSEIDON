package uk.ac.ox.oxfish.fisher.strategies.destination;

import ec.util.MersenneTwisterFast;
import uk.ac.ox.oxfish.fisher.Fisher;
import uk.ac.ox.oxfish.fisher.actions.Action;
import uk.ac.ox.oxfish.fisher.erotetic.EroteticAnswer;
import uk.ac.ox.oxfish.fisher.erotetic.EroteticChooser;
import uk.ac.ox.oxfish.fisher.log.TripListener;
import uk.ac.ox.oxfish.fisher.log.TripRecord;
import uk.ac.ox.oxfish.geography.SeaTile;
import uk.ac.ox.oxfish.model.FishState;

import java.util.List;

/**
 * Simple erotetic strategy: works first by given answer strategy and otherwise at random random
 * Created by carrknight on 4/11/16.
 */
public class SimpleEroteticDestinationStrategy implements DestinationStrategy,
        TripListener{

    private final EroteticChooser<SeaTile> chooser = new EroteticChooser<>();

    /**
     * this strategy works by modifying the "favorite" destination of its delegate
     */
    private final FavoriteDestinationStrategy delegate;

    /**
     * grabbed at start(.)
     */
    private Fisher fisher;

    /**
     * grabbed at start(.)
     */
    private FishState model;

    /**
     * the work is done almost exclusively by the argument passed, which contains all the important parameters
     * @param thresholder
     */
    public SimpleEroteticDestinationStrategy(
            EroteticAnswer<SeaTile> thresholder,
            FavoriteDestinationStrategy delegate
    ) {
        chooser.add(thresholder);
        this.delegate = delegate;
    }

    /**
     * decides where to go.
     *
     * @param fisher
     * @param random        the randomizer. It probably comes from the fisher but I make explicit it might be needed
     * @param model         the model link
     * @param currentAction what action is the fisher currently taking that prompted to check for destination   @return the destination
     */
    @Override
    public SeaTile chooseDestination(
            Fisher fisher, MersenneTwisterFast random,
            FishState model, Action currentAction)
    {
      return delegate.chooseDestination(fisher, random, model, currentAction);
    }

    @Override
    public void reactToFinishedTrip(TripRecord record) {
        //all choices
        List<SeaTile> options = model.getMap().getAllSeaTilesExcludingLandAsList();
        delegate.setFavoriteSpot(chooser.answer(options,
                                                fisher.getTileRepresentation(),
                                                model, fisher
                                 )
        );
    }

    @Override
    public void start(FishState model, Fisher fisher)
    {

        this.fisher = fisher;
        this.model = model;
        fisher.addTripListener(this);
        for(EroteticAnswer<SeaTile> filter : chooser)
            filter.start(model);
    }


    @Override
    public void turnOff() {
        fisher.removeTripListener(this);
        for(EroteticAnswer<SeaTile> filter : chooser)
            filter.turnOff();
        fisher=null;
    }
}