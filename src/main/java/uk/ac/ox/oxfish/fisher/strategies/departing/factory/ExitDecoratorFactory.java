package uk.ac.ox.oxfish.fisher.strategies.departing.factory;

import uk.ac.ox.oxfish.fisher.strategies.departing.DepartingStrategy;
import uk.ac.ox.oxfish.fisher.strategies.departing.ExitDepartingDecorator;
import uk.ac.ox.oxfish.model.FishState;
import uk.ac.ox.oxfish.utility.AlgorithmFactory;
import uk.ac.ox.oxfish.utility.parameters.DoubleParameter;
import uk.ac.ox.oxfish.utility.parameters.FixedDoubleParameter;

public class ExitDecoratorFactory implements AlgorithmFactory<ExitDepartingDecorator>
{


    AlgorithmFactory<? extends DepartingStrategy> decorated = new FixedRestTimeDepartingFactory();

    private DoubleParameter consecutiveLossYearsBeforeQuitting = new FixedDoubleParameter(2);


    /**
     * Applies this function to the given argument.
     *
     * @param fishState the function argument
     * @return the function result
     */
    @Override
    public ExitDepartingDecorator apply(FishState fishState) {

        return new ExitDepartingDecorator(
                decorated.apply(fishState),
                consecutiveLossYearsBeforeQuitting.apply(fishState.getRandom()).intValue()
        );

    }

    /**
     * Getter for property 'decorated'.
     *
     * @return Value for property 'decorated'.
     */
    public AlgorithmFactory<? extends DepartingStrategy> getDecorated() {
        return decorated;
    }

    /**
     * Setter for property 'decorated'.
     *
     * @param decorated Value to set for property 'decorated'.
     */
    public void setDecorated(
            AlgorithmFactory<? extends DepartingStrategy> decorated) {
        this.decorated = decorated;
    }

    /**
     * Getter for property 'consecutiveLossYearsBeforeQuitting'.
     *
     * @return Value for property 'consecutiveLossYearsBeforeQuitting'.
     */
    public DoubleParameter getConsecutiveLossYearsBeforeQuitting() {
        return consecutiveLossYearsBeforeQuitting;
    }

    /**
     * Setter for property 'consecutiveLossYearsBeforeQuitting'.
     *
     * @param consecutiveLossYearsBeforeQuitting Value to set for property 'consecutiveLossYearsBeforeQuitting'.
     */
    public void setConsecutiveLossYearsBeforeQuitting(
            DoubleParameter consecutiveLossYearsBeforeQuitting) {
        this.consecutiveLossYearsBeforeQuitting = consecutiveLossYearsBeforeQuitting;
    }
}
