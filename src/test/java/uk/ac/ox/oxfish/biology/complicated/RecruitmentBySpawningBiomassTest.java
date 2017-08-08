package uk.ac.ox.oxfish.biology.complicated;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ox.oxfish.biology.Species;
import uk.ac.ox.oxfish.biology.initializer.MultipleSpeciesAbundanceInitializer;

import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Created by carrknight on 6/26/17.
 */
public class RecruitmentBySpawningBiomassTest {

    @Test
    public void recruitment() throws Exception {

        Species species = MultipleSpeciesAbundanceInitializer.
                generateSpeciesFromFolder(Paths.get("inputs",
                                                    "california",
                                                    "biology",
                                                    "Sablefish"), "Sablefish");

        int[] male = new int[60];
        int[] female = new int[60];
        Arrays.fill(male, 0);
        Arrays.fill(female, 10000);

        System.out.println(species.getVirginRecruits());
        System.out.println(species.getSteepness());
        System.out.println(species.isAddRelativeFecundityToSpawningBiomass());

        RecruitmentBySpawningBiomass process = new RecruitmentBySpawningBiomass(
                species.getVirginRecruits(),
                species.getSteepness(),
                species.getCumulativePhi(),
                species.isAddRelativeFecundityToSpawningBiomass()
        );


        int recruits = process.recruit(species,species.getMeristics(),female,male);
        System.out.println(recruits);
        Assert.assertEquals(416140d, (double)recruits, 1d);

    }
}