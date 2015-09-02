package uk.ac.ox.oxfish.experiments;

import uk.ac.ox.oxfish.biology.initializer.factory.FromLeftToRightFactory;
import uk.ac.ox.oxfish.fisher.Fisher;
import uk.ac.ox.oxfish.fisher.selfanalysis.MovingAveragePredictor;
import uk.ac.ox.oxfish.model.FishState;
import uk.ac.ox.oxfish.model.Startable;
import uk.ac.ox.oxfish.model.scenario.PrototypeScenario;
import uk.ac.ox.oxfish.utility.FishStateUtilities;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * A simple experiment on "expected quota prices" from crappy predictions
 * Created by carrknight on 8/19/15.
 */
public class LambdaEstimation {


    public static void main(String[] args) throws IOException {


        PrototypeScenario scenario = new PrototypeScenario();
        FishState state = new FishState(0);
        state.setScenario(scenario);
        scenario.setBiologyInitializer(new FromLeftToRightFactory());

        state.registerStartable(new Startable() {
            @Override
            public void start(FishState model) {
                for (Fisher fisher : model.getFishers()) {

                    final MovingAveragePredictor dailyCatchesPredictor =
                            MovingAveragePredictor.dailyMAPredictor("Predicted Daily Catches",
                                                                    fisher1 -> fisher1.getDailyCounter().getLandingsPerSpecie(
                                                                            0),
                                                                    90);

                    dailyCatchesPredictor.start(model, fisher);
                    final MovingAveragePredictor profitPerUnitPredictor =
                            MovingAveragePredictor.perTripMAPredictor("Predicted Unit Profit",

                                                                      fisher1 -> {
                                                                          if(fisher1.getID()==1)
                                                                              System.out.println(fisher1.getLastFinishedTrip().getUnitProfitPerSpecie(
                                                                                      0));
                                                                          return fisher1.getLastFinishedTrip().getUnitProfitPerSpecie(
                                                                                  0);
                                                                      },
                                                                      30);

                    profitPerUnitPredictor.start(model, fisher);


                    fisher.getDailyData().registerGatherer("Reservation Lambda Owning 1000 quotas",
                                                           fisher1 -> {
                                                               if (state.getDayOfTheYear() == 365)
                                                                   return Double.NaN;
                                                               double probability = 1 - dailyCatchesPredictor.probabilityBelowThis(
                                                                       1000 / (365 - state.getDayOfTheYear()));
                                                               return (probability * profitPerUnitPredictor.predict());
                                                           }, Double.NaN);


                }

            }

            @Override
            public void turnOff() {

            }
        });

        state.start();

        while(state.getYear()<10)
            state.schedule.step(state);


        Paths.get("runs","lambda").toFile().mkdirs();
        FishStateUtilities.printCSVColumnToFile(
                //fisher 0 has some years where profits are really low, making the graph a lot less clear
                state.getFishers().get(0).getDailyData().getColumn("Reservation Lambda Owning 1000 quotas"),
                Paths.get("runs", "lambda", "overTime.csv").toFile());


        while(state.getDayOfTheYear() != 100)
            state.schedule.step(state);


        //write first histogram
        while(state.getDayOfTheYear() != 100)
            state.schedule.step(state);



        FishStateUtilities.pollHistogramToFile(
                fisher -> fisher.getDailyData().getLatestObservation("Reservation Lambda Owning 1000 quotas"),
                state.getFishers(),
                Paths.get("runs", "lambda", "hist100.csv").toFile());


        //write second histogram



        //write second histogram
        while(state.getDayOfTheYear() != 360)
            state.schedule.step(state);

        FishStateUtilities.pollHistogramToFile(
                fisher -> fisher.getDailyData().getLatestObservation("Reservation Lambda Owning 1000 quotas"),
                state.getFishers(),
                Paths.get("runs", "lambda", "hist360.csv").toFile());



        System.out.println("didn't crash!");



    }

}