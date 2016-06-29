package uk.ac.ox.oxfish.fisher.strategies.destination;

import com.google.common.base.Preconditions;
import ec.util.MersenneTwisterFast;
import uk.ac.ox.oxfish.fisher.Fisher;
import uk.ac.ox.oxfish.fisher.actions.Action;
import uk.ac.ox.oxfish.fisher.log.TripListener;
import uk.ac.ox.oxfish.fisher.log.TripRecord;
import uk.ac.ox.oxfish.fisher.selfanalysis.heatmap.AcquisitionFunction;
import uk.ac.ox.oxfish.fisher.selfanalysis.heatmap.GeographicalObservation;
import uk.ac.ox.oxfish.fisher.selfanalysis.heatmap.GeographicalRegression;
import uk.ac.ox.oxfish.geography.NauticalMap;
import uk.ac.ox.oxfish.geography.SeaTile;
import uk.ac.ox.oxfish.model.FishState;
import uk.ac.ox.oxfish.utility.adaptation.maximization.DefaultBeamHillClimbing;
import uk.ac.ox.oxfish.utility.adaptation.maximization.RandomStep;
import uk.ac.ox.oxfish.utility.adaptation.probability.AdaptationProbability;

import java.util.HashMap;

/**
 * A destination strategy that keeps a heatmap of profits and uses it to guide decisions
 * Created by carrknight on 6/29/16.
 */
public class HeatmapDestinationStrategy implements DestinationStrategy, TripListener
{

    /**
     * geographical regression to learn and predict where you make most money
     */
    private final GeographicalRegression profitRegression;

    /**
     * the strategy used to scan the profit regression to look for the "best"
     */
    private final AcquisitionFunction acquisition;

    /**
     * should we ignore trips that were cut short?
     */
    private final boolean ignoreFailedTrips;


    /**
     * the probability of exploring (shocking the best) and exploiting
     */
    private final AdaptationProbability  probability;


    private Fisher fisher;

    private FishState model;

    /**
     * you also listen to your friends trips, here are the listeners to turn off
     */
    private HashMap<Fisher, TripRecord> lastFriendTripRecorded;

    /**
     * what is doing the navigation
     */
    private final FavoriteDestinationStrategy delegate;

    /**
     * what t odo in the case of an exploration
     */
    private final RandomStep<SeaTile> explorationStep;


    public HeatmapDestinationStrategy(
            GeographicalRegression profitRegression,
            AcquisitionFunction acquisition, boolean ignoreFailedTrips,
            AdaptationProbability probability,
            NauticalMap map,
            MersenneTwisterFast random,
            int stepSize) {
        this.profitRegression = profitRegression;
        this.acquisition = acquisition;
        this.ignoreFailedTrips = ignoreFailedTrips;
        this.probability = probability;
        this.delegate = new FavoriteDestinationStrategy(map,random);
        this.explorationStep = DefaultBeamHillClimbing.DEFAULT_RANDOM_STEP(stepSize,5);
    }

    @Override
    public void start(FishState model, Fisher fisher) {
        this.fisher = fisher;
        this.model=model;

        lastFriendTripRecorded = new HashMap<>(fisher.getDirectedFriends().size());
        fisher.addTripListener(this);
    }

    @Override
    public void turnOff() {
        fisher.removeTripListener(this);
        lastFriendTripRecorded.clear();
        fisher=null;
        model=null;
    }

    @Override
    public void reactToFinishedTrip(TripRecord record)
    {
        SeaTile tile = record.getMostFishedTileInTrip();
        if(tile!=null)
            if(!record.isCutShort() || ignoreFailedTrips)
                profitRegression.addObservation(new GeographicalObservation(
                        tile.getGridX(),
                        tile.getGridY(),
                        model.getHoursSinceStart(),
                        record.getProfitPerHour(true)
                ));

        //go through your friends and add their observations if they are new
        // (with imitation probability)
        for(Fisher friend : fisher.getDirectedFriends())
        {
            TripRecord friendTrip = friend.getLastFinishedTrip();
            //if you have already been through this don't worry
            if(lastFriendTripRecorded.get(friend) == friendTrip)
                continue;
            lastFriendTripRecorded.put(friend,friendTrip);
            tile = friendTrip.getMostFishedTileInTrip();
            if(tile!=null)

                if(!record.isCutShort() || ignoreFailedTrips)
                if(model.getRandom().nextDouble()<=probability.getImitationProbability())
                    profitRegression.addObservation(new GeographicalObservation(
                            tile.getGridX(),
                            tile.getGridY(),
                            model.getHoursSinceStart(),
                            friendTrip.getProfitPerHour(true)
                    ));

        }

        //find the optimal
        SeaTile optimal = acquisition.pick(model.getMap(), profitRegression, model);
        Preconditions.checkState(optimal.getAltitude()<0);
        if(model.getRandom().nextDouble()<=probability.getExplorationProbability()) {
            optimal = explorationStep.randomStep(model, model.getRandom(), fisher, optimal);
            Preconditions.checkState(optimal.getAltitude()<0);
        }
        delegate.setFavoriteSpot(optimal);
    }


    public GeographicalRegression getProfitRegression() {
        return profitRegression;
    }

    public AdaptationProbability getProbability() {
        return probability;
    }

    public HashMap<Fisher, TripRecord> getLastFriendTripRecorded() {
        return lastFriendTripRecorded;
    }

    public RandomStep<SeaTile> getExplorationStep() {
        return explorationStep;
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
            Fisher fisher, MersenneTwisterFast random, FishState model, Action currentAction) {
        return delegate.chooseDestination(fisher,random,model,currentAction);
    }

    public SeaTile getFavoriteSpot() {
        return delegate.getFavoriteSpot();
    }
}