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

package uk.ac.ox.oxfish.model.market.factory;

import uk.ac.ox.oxfish.model.FishState;
import uk.ac.ox.oxfish.model.market.Market;
import uk.ac.ox.oxfish.model.market.MarketProxy;
import uk.ac.ox.oxfish.utility.AlgorithmFactory;
import uk.ac.ox.oxfish.utility.yaml.FishYAML;

import java.util.*;

public class SpeciesMarketMappedFactory implements AlgorithmFactory<MarketProxy> {

    private LinkedHashMap<String, AlgorithmFactory<? extends Market>> markets = new LinkedHashMap<>();


    /**
     * Applies this function to the given argument.
     *
     * @param fishState the function argument
     * @return the function result
     */
    @Override
    public MarketProxy apply(FishState fishState) {
        return new MarketProxy(new LinkedHashMap<>(markets));
    }

    /**
     * Getter for property 'markets'.
     *
     * @return Value for property 'markets'.
     */
    public LinkedHashMap<String,  AlgorithmFactory<? extends Market>> getMarkets() {
        return markets;
    }

    /**
     * Setter for property 'markets'.
     *
     * @param markets Value to set for property 'markets'.
     */
    public void setMarkets(
            LinkedHashMap<String, ?> markets) {

        //useless cast, but it deals with YAML quirks
        LinkedHashMap<String,  AlgorithmFactory<? extends Market>> real = new LinkedHashMap<>();

        FishYAML  yaml = new FishYAML();

        //force it to go through YAML
        for (Map.Entry<String,  ?> entry : markets.entrySet()) {
            Object factory = entry.getValue();

            AlgorithmFactory<? extends Market> recast = (AlgorithmFactory<? extends Market>)
                    yaml.loadAs(yaml.dump(factory), AlgorithmFactory.class);
            real.put(entry.getKey(),recast);

        }



        this.markets = real;
    }


    public static void main(String[] args){
        SpeciesMarketMappedFactory factory = new SpeciesMarketMappedFactory();
        factory.getMarkets().put(
                "Lutjanus malabaricus",
                new ThreePricesMarketFactory(7,9,42900,66000,52800)
        );
        factory.getMarkets().put(
                "Pristipomoides multidens",
                new ThreePricesMarketFactory(6,7,26325,40500,42500)
        );
        factory.getMarkets().put(
                "Lutjanus erythropterus",
                new ThreePricesMarketFactory(6,9,42900,66000,52800)
        );
        factory.getMarkets().put(
                "Epinephelus areolatus",
                new ThreePricesMarketFactory(8,20,35100,54000,43200)
        );

        FishYAML yaml = new FishYAML();
        System.out.println(yaml.dump(factory));
    }





}
