package uk.ac.ox.oxfish.maximization;

import uk.ac.ox.oxfish.model.scenario.Scenario;
import uk.ac.ox.oxfish.utility.yaml.FishYAML;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TunaMaximization {

    /**
     * For now, just generate a new tuna scenario file (tuna.yaml) from the copy-pasted parameters
     * resulting from running calibration.yaml.
     */
    public static void main(String[] args) throws IOException {

        FishYAML yaml = new FishYAML();
        Path optimizationFile = Paths.get("inputs", "tuna", "calibration.yaml");
        GenericOptimization optimization = yaml.loadAs(new FileReader(optimizationFile.toFile()), GenericOptimization.class);
        Scenario scenario = optimization.buildScenario(new double[]{
            -3.900, -2.747, -4.701, -8.053, 9.668, 3.558, -7.573
        });
        Path outputFile = optimizationFile.getParent().resolve("tuna.yaml");
        yaml.dump(scenario, new FileWriter(outputFile.toFile()));

    }
}
