package uk.ac.ox.oxfish.geography.mapmakers;

import com.google.common.collect.Table;
import com.google.common.io.Files;
import com.vividsolutions.jts.geom.Envelope;
import ec.util.MersenneTwisterFast;
import sim.field.geo.GeomGridField;
import sim.field.geo.GeomVectorField;
import sim.field.grid.ObjectGrid2D;
import uk.ac.ox.oxfish.biology.GlobalBiology;
import uk.ac.ox.oxfish.geography.*;
import uk.ac.ox.oxfish.geography.habitat.TileHabitat;
import uk.ac.ox.oxfish.geography.pathfinding.AStarPathfinder;
import uk.ac.ox.oxfish.geography.sampling.GeographicalSample;
import uk.ac.ox.oxfish.geography.sampling.SampledMap;
import uk.ac.ox.oxfish.model.FishState;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.OptionalDouble;

/**
 *
 * Creates a map from file. If it is a csv it expects it being a depth map. If it is a data file it expects it
 * being a previous map that has been saved
 * Created by carrknight on 6/30/17.
 */
public class FromFileMapInitializer implements MapInitializer {




    final private Path filePath;

    /**
     * how to grid it
     */
    final private int gridWidthInCells;


    final private boolean header;

    /**
     * true if coordinates are in latlong, otherwise assume UTM
     */
    final private boolean latLong;


    public FromFileMapInitializer(Path filePath, int gridWidthInCells, boolean header, boolean latLong) {
        this.filePath = filePath;
        this.gridWidthInCells = gridWidthInCells;
        this.header = header;
        this.latLong = latLong;
    }

    @Override
    public NauticalMap makeMap(
            MersenneTwisterFast random, GlobalBiology biology, FishState model) {

        //get the file extension
        String fileExtension = Files.getFileExtension(filePath.getFileName().toString());



        try {

            //prep the sampled map
            SampledMap sampledMap;

            switch (fileExtension.trim().toLowerCase())
            {
                //assuming here you saved already the nautical map
                case "data":
                    ObjectInputStream stream = new ObjectInputStream(
                            new FileInputStream(filePath.toFile()));

                    sampledMap = (SampledMap) stream.readObject();
                    return sampledAltitudeToNauticalMap(sampledMap.getAltitudeGrid(),
                                                        sampledMap.getMbr(),
                                                        sampledMap.getGridHeight(),
                                                        sampledMap.getGridWith(), latLong);

                default:
                case "csv":
                    //otherwise read from data
                    GeographicalSample altitudeSample = new GeographicalSample(filePath,
                                                                               header);
                    //create the mbr from max-min stuff
                    Envelope mbr = new Envelope(altitudeSample.getMinEasting(), altitudeSample.getMaxEasting(),
                                                altitudeSample.getMinNorthing(), altitudeSample.getMaxNorthing());
                    //find ratio height to width
                    double heightToWidth = mbr.getHeight()/mbr.getWidth();
                    int gridHeightInCells = (int) Math.round(gridWidthInCells * heightToWidth);

                    //create backing grid
                    ObjectGrid2D backingObjectGrid = new ObjectGrid2D(gridWidthInCells, gridHeightInCells);
                    GeomGridField geomGrid = new GeomGridField(backingObjectGrid);
                    geomGrid.setMBR(mbr);

                    //get the altitude grid
                    Table<Integer, Integer, LinkedList<Double>> sampledAltitudeGrid = SampledMap.fileToGrid(
                            geomGrid,
                            altitudeSample,
                            gridWidthInCells,
                            gridHeightInCells
                    );

                    return sampledAltitudeToNauticalMap(sampledAltitudeGrid, mbr, gridHeightInCells, gridWidthInCells,
                                                        latLong);

            }

            //now turn the sampled map into

        } catch (IOException | ClassNotFoundException e)
        {
            throw new RuntimeException("Failure!");
        }
    }



    public static NauticalMap sampledAltitudeToNauticalMap(
            Table<Integer, Integer,
                    LinkedList<Double>> sampledAltitudeGrid,
            Envelope mbr, int gridHeightInCells,
            int gridWidthInCells,
            final boolean latLong) {
        //turn it into a proper map
        ObjectGrid2D altitudeGrid = new ObjectGrid2D(gridWidthInCells, gridHeightInCells);


        //so for altitude we just average them out
        for(int x=0;x<gridWidthInCells;x++)
            for(int y=0;y<gridHeightInCells;y++)
            {
                OptionalDouble average = sampledAltitudeGrid.get(x, y).
                        stream().mapToDouble(
                        value -> value).filter(
                        aDouble -> aDouble > -9999).average();
                altitudeGrid.set(x, y,
                                 new SeaTile(x, y, average.orElseGet(() -> 1000d), new TileHabitat(0)));
            }

        GeomGridField unitedMap = new GeomGridField(altitudeGrid);
        unitedMap.setMBR(mbr);

        //create the map
        Distance distance = latLong ? new EquirectangularDistanceByCoordinate() : new CartesianUTMDistance() ;
        NauticalMap nauticalMap = new NauticalMap(unitedMap, new GeomVectorField(),
                                                  distance,
                                                  new AStarPathfinder(distance));

        //cell distance:
        System.out.println("coordinates for 0,0 are: " + nauticalMap.getCoordinates(0,0) );
        System.out.println("coordinates for 1,1 are: " + nauticalMap.getCoordinates(1,1) );
        System.out.println("the distance between 0,0 and 1,1 is: " +
                                   distance.distance(nauticalMap.getSeaTile(0,0),
                                                     nauticalMap.getSeaTile(1,1),
                                                     nauticalMap) );

        return nauticalMap;
    }
}
