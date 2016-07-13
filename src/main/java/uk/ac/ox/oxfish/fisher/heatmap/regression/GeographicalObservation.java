package uk.ac.ox.oxfish.fisher.heatmap.regression;

import uk.ac.ox.oxfish.geography.SeaTile;

import java.util.Objects;

/**
 * An immutable object useful for regression. It is compared with respect to time
 * Created by carrknight on 6/27/16.
 */
public class GeographicalObservation implements Comparable<GeographicalObservation>
{

    private final double time;

    private final double value;

    private final SeaTile tile;

    public GeographicalObservation(SeaTile tile, double time, double value) {
        this.tile=tile;
        this.time = time;
        this.value = value;
    }


    /**
     * Getter for property 'x'.
     *
     * @return Value for property 'x'.
     */
    public double getX() {
        return tile.getGridX();
    }

    /**
     * Getter for property 'y'.
     *
     * @return Value for property 'y'.
     */
    public double getY() {
        return tile.getGridY();
    }

    /**
     * Getter for property 'time'.
     *
     * @return Value for property 'time'.
     */
    public double getTime() {
        return time;
    }

    /**
     * Getter for property 'value'.
     *
     * @return Value for property 'value'.
     */
    public double getValue() {
        return value;
    }

    /**
     * Note: this class has a natural ordering that is
     * inconsistent with equals: the ordering is over time alone
     */
    @Override
    public int compareTo(GeographicalObservation o) {
        return Double.compare(this.getTime(),o.getTime());
    }

    /**
     * Getter for property 'tile'.
     *
     * @return Value for property 'tile'.
     */
    public SeaTile getTile() {
        return tile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeographicalObservation that = (GeographicalObservation) o;
        return Double.compare(that.time, time) == 0 &&
                Double.compare(that.value, value) == 0 &&
                Objects.equals(tile, that.tile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(time, value, tile);
    }
}