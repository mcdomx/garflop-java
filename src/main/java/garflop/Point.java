package garflop;

import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIterNodeList;
import org.jdom.Attribute;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

// Point instances are created from the list returned by calling getAttributes on an element.
// a List of all the points created is stored in the static variable Points
public class Point {
    private final double lat;
    private final double lon;
    private final Boolean valid;
    private double elevation;
    private int power;
    private int heartRate;
    private int cadence;
    private static ArrayList<Point> points = new ArrayList<>();

    // Point initializer
    public Point (List<Attribute> attr){

        Boolean hasLat = false;
        Boolean hasLon = false;
        double lat = 0, lon = 0;

        //Assign point lat/lon coordinates
        for ( int i=0 ; i<attr.size() ; i++ ) {
            String attrName = attr.get(i).getName();
            if (attrName.equals("lat")) { hasLat=true; lat=Double.parseDouble(attr.get(i).getValue()); }
            if (attrName.equals("lon")) { hasLon=true; lon=Double.parseDouble(attr.get(i).getValue()); }
        } // end for loop

        if (hasLat && hasLon) {
            this.valid = true;
        } else {
            this.valid = false;
        }

        this.lat = lat;
        this.lon = lon;
        points.add(this);

    } // end initializer


    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public double getElevation() {
        return elevation;
    }

    public void setElevation(double elevation) {
        this.elevation = elevation;
    }

    public boolean isValid() {
        return this.valid;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public int getCadence() {
        return cadence;
    }

    public void setCadence(int cadence) {
        this.cadence = cadence;
    }

    public static List<Point> getPoints () {
        return Point.points;
    }

    public static int getMaxHR () {

        return points.stream()
                    .mapToInt(Point::getHeartRate)
                    .max()
                    .getAsInt();

    }


    public static double getAvgHR () {

        OptionalDouble avgHR = points.stream()
                            .mapToInt(Point::getHeartRate)
                            .average();

        return avgHR.getAsDouble();

    }


    public static int getMaxCadence () {

        return points.stream()
                .mapToInt(Point::getCadence)
                .max()
                .getAsInt();

    }

    public static double getAvgCadence () {

        OptionalDouble avgCadence = points.stream()
                .mapToInt(Point::getCadence)
                .filter(i -> i>0)
                .average();

        return avgCadence.getAsDouble();

    }



    public static HashMap<String, Double> getClimbStats () {
        HashMap<String, Double> elevStatistics = new HashMap<>();
        double ttlClimb = 0.0;
        double ttlDescent = 0.0;
        Point prevPoint;
        Point curPoint;
        double diff;

        Iterator<Point> iterPoints = points.iterator();
        prevPoint = iterPoints.next();

        while (iterPoints.hasNext()) {
            curPoint = iterPoints.next();
            diff = curPoint.getElevation() - prevPoint.getElevation();
            if ( diff > 0 ) {
                ttlClimb += diff;
            } else {
                ttlDescent += diff;
            }
        }

        elevStatistics.put( "ttlClimb", ttlClimb );
        elevStatistics.put( "ttlDescent", ttlDescent );

        return elevStatistics;
    }

}
