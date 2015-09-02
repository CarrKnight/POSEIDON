package uk.ac.ox.oxfish.model.market.factory;

import uk.ac.ox.oxfish.fisher.strategies.destination.DestinationStrategy;
import uk.ac.ox.oxfish.fisher.strategies.destination.factory.RandomFavoriteDestinationFactory;
import uk.ac.ox.oxfish.model.market.Market;
import uk.ac.ox.oxfish.utility.AlgorithmFactory;

import java.util.LinkedHashMap;
import java.util.function.Supplier;

/**
 * Collections holding all the possible factories for markets
 * Created by carrknight on 8/11/15.
 */
public class Markets {

    /**
     * the list of all registered CONSTRUCTORS
     */
    public static final LinkedHashMap<String,Supplier<AlgorithmFactory<? extends Market>>> CONSTRUCTORS =
            new LinkedHashMap<>();

    public static final LinkedHashMap<Class<? extends AlgorithmFactory>,String> NAMES = new LinkedHashMap<>();


    static
    {

        CONSTRUCTORS.put("Fixed Price Market",
                         FixedPriceMarketFactory::new
        );
        NAMES.put(FixedPriceMarketFactory.class,"Fixed Price Market");

        CONSTRUCTORS.put("Congested Market",
                         CongestedMarketFactory::new
        );
        NAMES.put(CongestedMarketFactory.class,"Congested Market");



    }

}