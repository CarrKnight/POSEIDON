package uk.ac.ox.oxfish.model.regs;

import com.google.common.base.Preconditions;
import uk.ac.ox.oxfish.biology.Species;
import uk.ac.ox.oxfish.fisher.Fisher;
import uk.ac.ox.oxfish.fisher.log.TripRecord;
import uk.ac.ox.oxfish.fisher.selfanalysis.profit.Cost;
import uk.ac.ox.oxfish.model.FishState;
import uk.ac.ox.oxfish.model.market.itq.ITQOrderBook;

import java.util.HashMap;
import java.util.function.Function;

/**
 * Like multiquota but with a reference to ITQs used to compute opportunity costs
 * Created by carrknight on 4/20/16.
 */
public class MultiQuotaITQRegulation extends MultiQuotaRegulation  {


    final private HashMap<Integer,ITQOrderBook> orderBooks;

    public MultiQuotaITQRegulation(
            double[] yearlyQuota, FishState state, HashMap<Integer,ITQOrderBook> orderBooks) {
        super(yearlyQuota, state);
        this.orderBooks = orderBooks;
    }


    //compute opportunity costs!

    private ITQOpportunityCost cost;

    /**
     * add yourself as an opportunity cost!
     * @param model
     * @param fisher
     */
    @Override
    public void start(FishState model, Fisher fisher) {
        assert cost == null;
        cost = new ITQOpportunityCost(new Function<Species, ITQOrderBook>() {
            @Override
            public ITQOrderBook apply(Species species) {
                return orderBooks.get(species.getIndex());
            }
        });

        assert (!fisher.getOpportunityCosts().contains(cost));
        fisher.getOpportunityCosts().add(cost);

    }

    @Override
    public void turnOff(Fisher fisher) {
        if(cost!=null)
            fisher.getOpportunityCosts().remove(cost);
    }

    @Override
    public void reactToSale(
            Species species, Fisher seller, double biomass, double revenue) {
        super.reactToSale(species, seller, biomass, revenue);

    }


}

