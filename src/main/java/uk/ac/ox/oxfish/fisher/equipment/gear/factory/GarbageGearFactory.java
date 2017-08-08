package uk.ac.ox.oxfish.fisher.equipment.gear.factory;

import com.google.common.base.Preconditions;
import uk.ac.ox.oxfish.biology.Species;
import uk.ac.ox.oxfish.biology.initializer.MultipleSpeciesAbundanceInitializer;
import uk.ac.ox.oxfish.fisher.equipment.gear.GarbageGearDecorator;
import uk.ac.ox.oxfish.fisher.equipment.gear.Gear;
import uk.ac.ox.oxfish.model.FishState;
import uk.ac.ox.oxfish.utility.AlgorithmFactory;
import uk.ac.ox.oxfish.utility.parameters.DoubleParameter;
import uk.ac.ox.oxfish.utility.parameters.FixedDoubleParameter;

/**
 * Creates a gear where one species is not modelled directly in the biology but this gear still catches a bunch
 * of it in a fixed proportion to the rest of the world
 * Created by carrknight on 3/22/17.
 */
public class GarbageGearFactory implements AlgorithmFactory<GarbageGearDecorator> {


    private String garbageSpeciesName = MultipleSpeciesAbundanceInitializer.FAKE_SPECIES_NAME;

    private DoubleParameter proportionSimulatedToGarbage = new FixedDoubleParameter(0.3);

    private AlgorithmFactory<? extends Gear> delegate = new FixedProportionGearFactory();


    /**
     * Applies this function to the given argument.
     *
     * @param state the function argument
     * @return the function result
     */
    @Override
    public GarbageGearDecorator apply(FishState state) {

        Species garbageSpecies = state.getBiology().getSpecie(garbageSpeciesName);
        Preconditions.checkArgument(garbageSpecies != null && garbageSpecies.isImaginary(),
                                    "The garbage species must be exist and be'imaginary'");
        Gear delegate = this.delegate.apply(state);

        return new GarbageGearDecorator(garbageSpecies,
                                        proportionSimulatedToGarbage.apply(state.getRandom()),
                                        delegate);


    }

    /**
     * Getter for property 'garbageSpeciesName'.
     *
     * @return Value for property 'garbageSpeciesName'.
     */
    public String getGarbageSpeciesName() {
        return garbageSpeciesName;
    }

    /**
     * Setter for property 'garbageSpeciesName'.
     *
     * @param garbageSpeciesName Value to set for property 'garbageSpeciesName'.
     */
    public void setGarbageSpeciesName(String garbageSpeciesName) {
        this.garbageSpeciesName = garbageSpeciesName;
    }

    /**
     * Getter for property 'proportionSimulatedToGarbage'.
     *
     * @return Value for property 'proportionSimulatedToGarbage'.
     */
    public DoubleParameter getProportionSimulatedToGarbage() {
        return proportionSimulatedToGarbage;
    }

    /**
     * Setter for property 'proportionSimulatedToGarbage'.
     *
     * @param proportionSimulatedToGarbage Value to set for property 'proportionSimulatedToGarbage'.
     */
    public void setProportionSimulatedToGarbage(DoubleParameter proportionSimulatedToGarbage) {
        this.proportionSimulatedToGarbage = proportionSimulatedToGarbage;
    }

    /**
     * Getter for property 'delegate'.
     *
     * @return Value for property 'delegate'.
     */
    public AlgorithmFactory<? extends Gear> getDelegate() {
        return delegate;
    }

    /**
     * Setter for property 'delegate'.
     *
     * @param delegate Value to set for property 'delegate'.
     */
    public void setDelegate(
            AlgorithmFactory<? extends Gear> delegate) {
        this.delegate = delegate;
    }
}