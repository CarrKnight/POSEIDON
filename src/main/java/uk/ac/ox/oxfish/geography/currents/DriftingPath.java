package uk.ac.ox.oxfish.geography.currents;

import sim.util.Double2D;
import uk.ac.ox.oxfish.geography.SeaTile;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Collections.unmodifiableMap;

public class DriftingPath {

    private final int initialTimeStep;
    private final Map<Integer, Optional<Double2D>> positions;
    private final CurrentVectors currentVectors;
    private final BiFunction<Integer, Integer, SeaTile> getSeaTile;

    public DriftingPath(
        int initialTimeStep,
        Double2D initialPosition,
        CurrentVectors currentVectors,
        BiFunction<Integer, Integer, SeaTile> getSeaTile
    ) {
        this.initialTimeStep = initialTimeStep;
        this.currentVectors = currentVectors;
        this.getSeaTile = getSeaTile;
        positions = new HashMap<>();
        positions.put(initialTimeStep, Optional.of(initialPosition));
    }

    public Optional<Double2D> position(int timeStep) {
        checkArgument(timeStep >= initialTimeStep);
        return positions.computeIfAbsent(timeStep, step ->
            position(timeStep - 1).flatMap(previousPosition -> {
                final SeaTile seaTile = getSeaTile.apply((int) previousPosition.x, (int) previousPosition.y);
                final Optional<Double2D> vector = Optional.ofNullable(currentVectors.getVector(step, seaTile));
                return vector.map(previousPosition::add);
            })
        );
    }

    public Map<Integer, Optional<Double2D>> getPositions() { return unmodifiableMap(positions); }
}