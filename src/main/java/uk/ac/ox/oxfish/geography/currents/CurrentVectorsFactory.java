package uk.ac.ox.oxfish.geography.currents;

import com.univocity.parsers.common.record.Record;
import com.vividsolutions.jts.geom.Coordinate;
import sim.field.geo.GeomGridField;
import sim.util.Double2D;
import uk.ac.ox.oxfish.geography.EquirectangularDistance;
import uk.ac.ox.oxfish.geography.NauticalMap;
import uk.ac.ox.oxfish.geography.SeaTile;
import uk.ac.ox.oxfish.model.scenario.TunaScenario;
import uk.ac.ox.oxfish.utility.csv.CsvParserUtil;

import java.nio.file.Path;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import static uk.ac.ox.oxfish.utility.MasonUtils.coordinateToXY;
import static uk.ac.ox.oxfish.utility.csv.CsvParserUtil.getLocalDate;

public class CurrentVectorsFactory {

    private static final int SECONDS_PER_DAY = 60 * 60 * 24;

    public static CurrentVectors makeCurrentVectors(NauticalMap map, int stepsPerDay) {
        final TreeMap<Integer, EnumMap<CurrentPattern, Map<SeaTile, Double2D>>> vectorMaps =
            makeVectorMaps(map, TunaScenario.currentFiles);
        return new CurrentVectors(vectorMaps, stepsPerDay);
    }

    private static TreeMap<Integer, EnumMap<CurrentPattern, Map<SeaTile, Double2D>>> makeVectorMaps(
        NauticalMap map,
        Map<CurrentPattern, Path> currentFiles
    ) {
        TreeMap<Integer, EnumMap<CurrentPattern, Map<SeaTile, Double2D>>> currentVectors = new TreeMap<>();
        currentFiles.forEach((currentPattern, path) ->
            CsvParserUtil.parseAllRecords(path).forEach(record ->
                Optional.ofNullable(map.getSeaTile(readCoordinate(record))).ifPresent(seaTile -> {
                    final int dayOfYear = getLocalDate(record, "dttm").getDayOfYear();
                    final Map<SeaTile, Double2D> seaTileDouble2DMap = currentVectors
                        .computeIfAbsent(dayOfYear, __ -> new EnumMap<>(CurrentPattern.class))
                        .computeIfAbsent(currentPattern, __ -> new HashMap<>());
                    final Double2D vector = readVector(record, seaTile, map);
                    seaTileDouble2DMap.put(seaTile, vector);
                })));
        return currentVectors;
    }

    private static Coordinate readCoordinate(Record record) {
        return new Coordinate(record.getDouble("lon"), record.getDouble("lat"));
    }

    private static Double2D readVector(Record record, SeaTile seaTile, NauticalMap map) {
        final Double2D metrePerSecondVector = new Double2D(
            record.getDouble("u"),
            record.getDouble("v")
        );
        return metrePerSecondToXyPerDaysVector(metrePerSecondVector, seaTile, map);
    }

    /**
     * Converts a metres/second vector at a location into a grid-xy offsets/day vector.
     * This is slightly convoluted because the translation of distance into grid offsets depends on the latitude,
     * so we need to use lon/lat coordinates as an intermediate and then convert back to grid coordinates.
     */
    private static Double2D metrePerSecondToXyPerDaysVector(Double2D metrePerSecondVector, SeaTile seaTile, NauticalMap nauticalMap) {
        final GeomGridField gridField = nauticalMap.getRasterBathymetry();
        final Double2D metresPerDayVector = metrePerSecondVector.multiply(SECONDS_PER_DAY);
        final Coordinate startCoord = nauticalMap.getCoordinates(seaTile);
        final Double2D startXY = coordinateToXY(gridField, startCoord);
        final Double2D lonLatVector = metresVectorToLonLatVector(startCoord, metresPerDayVector.x, metresPerDayVector.y);
        final Coordinate endCoord = new Coordinate(startCoord.x + lonLatVector.x, startCoord.y + lonLatVector.y);
        final Double2D endXY = coordinateToXY(gridField, endCoord);
        return endXY.add(startXY.negate());
    }

    /**
     * Takes a vector of offsets in metres and converts it to a vector of longitude/latitude
     * offsets, assuming that we are in the vicinity of {@code coord}. Adapted from
     * https://stackoverflow.com/a/2839560 and https://stackoverflow.com/a/7478827.
     */
    private static Double2D metresVectorToLonLatVector(Coordinate coord, Double u, Double v) {
        double r = EquirectangularDistance.EARTH_RADIUS * 1000; // Earth radius in metres
        final double dx = (180 / Math.PI) * (u / r) / Math.cos(Math.PI / 180.0 * coord.y);
        final double dy = (180 / Math.PI) * (v / r);
        return new Double2D(dx, dy);
    }

}
