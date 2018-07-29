package uk.ac.ox.oxfish;


import org.yaml.snakeyaml.Yaml;
import uk.ac.ox.oxfish.maximization.CaliforniaDerisoOptimization;
import uk.ac.ox.oxfish.maximization.OneGearExampleMaximization;
import uk.ac.ox.oxfish.maximization.SamplePoseidonOptimization;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

public class MainOptimizer {


    static {

        Yaml yaml = new Yaml();
        try {
            yaml.dump(new SamplePoseidonOptimization(),
                    new FileWriter(Paths.get("eva","sample.yaml").toFile()));
            yaml.dump(new CaliforniaDerisoOptimization(),
                    new FileWriter(Paths.get("eva","deriso.yaml").toFile()));
            yaml.dump(new OneGearExampleMaximization(),
                    new FileWriter(Paths.get("eva","example.yaml").toFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {



        eva2.gui.Main.main(args);

    }
}
