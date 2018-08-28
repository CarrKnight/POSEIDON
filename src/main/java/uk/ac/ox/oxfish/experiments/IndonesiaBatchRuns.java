/*
 *     POSEIDON, an agent-based model of fisheries
 *     Copyright (C) 2018  CoHESyS Lab cohesys.lab@gmail.com
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

package uk.ac.ox.oxfish.experiments;

import com.google.common.collect.Lists;
import uk.ac.ox.oxfish.model.BatchRunner;
import uk.ac.ox.oxfish.model.data.collectors.FisherYearlyTimeSeries;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

public class IndonesiaBatchRuns {


    public static final String FILENAME = "712_pessimistic_perfect_calibrated_2014";
    public static final String DIRECTORY = "docs/indonesia_hub/runs/712/slice0/calibration/";

    public static void main(String[] args) throws IOException {


        BatchRunner runner = new BatchRunner(
                Paths.get(DIRECTORY,
                          FILENAME + ".yaml"),
                15,
                Lists.newArrayList(
                        "Snapper Landings",
                        "Snapper Landings of population0",
                        "Snapper Landings of population1",
                        "Snapper Landings of population2",
                        "Average Cash-Flow",
                        "Average Cash-Flow of population0",
                        "Average Cash-Flow of population1",
                        "Average Cash-Flow of population2",
                        "Average Number of Trips of population0",
                        "Average Number of Trips of population1",
                        "Average Number of Trips of population2",
                        "Average Distance From Port of population0",
                        "Average Distance From Port of population1",
                        "Average Distance From Port of population2",
                        "Average Trip Duration of population0",
                        "Average Trip Duration of population1",
                        "Average Trip Duration of population2",
                        //"Total Variable Costs of small",
                        //"Total Earnings of small",


                   //     "Total Variable Costs of big",
                        "Biomass Snapper"

                ),
                Paths.get(DIRECTORY,
                          FILENAME),
                null,
                System.currentTimeMillis(),
                -1
        );


        FileWriter fileWriter = new FileWriter(Paths.get(DIRECTORY, FILENAME + ".csv").toFile());
        fileWriter.write("run,year,variable,value\n");
        fileWriter.flush();

        while(runner.getRunsDone()<100) {

            StringBuffer tidy = new StringBuffer();
            runner.run(tidy);
            fileWriter.write(tidy.toString());
            fileWriter.flush();
        }
        fileWriter.close();
    }
}
