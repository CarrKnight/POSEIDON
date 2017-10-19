/*
 *     POSEIDON, an agent-based model of fisheries
 *     Copyright (C) 2017  CoHESyS Lab cohesys.lab@gmail.com
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

package uk.ac.ox.oxfish.fisher.erotetic.snalsar.factory;

import uk.ac.ox.oxfish.fisher.erotetic.snalsar.LegalityFeatureExtractor;
import uk.ac.ox.oxfish.geography.SeaTile;
import uk.ac.ox.oxfish.utility.AlgorithmFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * The legality extractors
 * Created by carrknight on 5/31/16.
 */
public class LegalityFeatureExtractors {


    /**
     * the list of all registered CONSTRUCTORS
     */
    public static final Map<String,Supplier<AlgorithmFactory<? extends LegalityFeatureExtractor<SeaTile>>>> CONSTRUCTORS =
            new LinkedHashMap<>();
    /**
     * a link to go from class back to the name of the constructor
     */
    public static final Map<Class<? extends AlgorithmFactory>,String> NAMES =
            new LinkedHashMap<>();


    static {

        CONSTRUCTORS.put("Ignore Rules",
                         EverywhereTrueExtractorFactory::new);
        NAMES.put(EverywhereTrueExtractorFactory.class,
                  "Ignore Rules");

        CONSTRUCTORS.put("Follow the Rules",
                         FollowRulesExtractorFactory::new);
        NAMES.put(FollowRulesExtractorFactory.class,
                  "Follow the Rules");

    }
}
