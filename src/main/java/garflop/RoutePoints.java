package garflop;

import garflop.DistanceCalculations;
import javafx.util.Pair;


import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.summarizingInt;

/*  RoutePoints class hold the array of points collected in a route.
    Methods return summary values and statistics calculated from the points.
    RoutePoints uses singleton pattern to ensure only one instance is created.
 */

public class RoutePoints {

    private static RoutePoints unique;
    private static ArrayList<Point> points = new ArrayList<>();

    //private initializer for singelton pattern
    private RoutePoints () {    }

    //singleton initializer
    public static RoutePoints createRoutePoints() {
        if (unique == null)
            unique = new RoutePoints();

        return unique;
    }

    public static RoutePoints getCurrentRoute() {
        return unique;
    }

    //clears all the points in the route
    public static void resetRoutePoints() {
        points.clear();
    }

    public static double distanceInMeters () {
        return DistanceCalculations.totalDistanceInMeters(points);
    }

    public static String getDistanceInKM () {
        Double ttlDistance = DistanceCalculations.totalDistanceInKM(getPoints());
        return (Double.valueOf(new DecimalFormat("#.#").format(ttlDistance))).toString();
    }

    public static String getRate () {
        int offset = ZonedDateTime.now().getOffset().getTotalSeconds()/60/60;
        LocalDateTime startTime = LocalDateTime.ofEpochSecond(getEpochStartTime(), 0, ZoneOffset.ofHours(offset));
        LocalDateTime endTime = LocalDateTime.ofEpochSecond(getEpochEndTime(), 0, ZoneOffset.ofHours(offset));
        Duration ttlTime = Duration.between(startTime, endTime);
        Double ttlDistance = DistanceCalculations.totalDistanceInKM(getPoints());
        Double timeFrac = ttlTime.toHours() + ttlTime.toMinutes()%60/60.0 + ttlTime.toMillis()/1000%60/60.0/60.0;
        Double rate = Math.round(ttlDistance / timeFrac * 100.0)/100.0;
        return rate.toString();
    }

    public static void addPoint(Point point) {
        points.add(point);
    }

    public static List<Pair<Double, Double>> getLatLonPoints() {
        //array with lat and lon point pair for each array element
        List<Pair<Double, Double>> latlon_pts = new ArrayList<>();

        for (Point p : points) {
            latlon_pts.add(new Pair<>(p.getLat(), p.getLon()));
        }

        return latlon_pts;
    }

    public static List<Pair<Double, Double>> getElevationPoints(int maxPoints) {

        //e_pts holds the points in a route that mark elevation points
        List<Pair<Double, Double>> e_pts = new ArrayList<>();

        //Points need to be evenly spaced so a fixed distance between
        //marked elevation points is calculated
        int numPts = points.size();
        double dist = distanceInMeters();
        double metersPerPoint = dist / maxPoints;
        double cumDist = 0;

        //Store the initial point
        e_pts.add(new Pair<>(0.00, points.get(0).getElevation() ));

        //Loop through points and store an elevation point when the
        //fixed distance between points is reached
        for ( int curPt=1 ; curPt < numPts ; curPt++ ) {
            cumDist += DistanceCalculations.ptDistInMeters(points.get(curPt), points.get(curPt-1));
            if ( cumDist >= (metersPerPoint*e_pts.size()) )
                e_pts.add(new Pair<>(cumDist, points.get(curPt).getElevation()));
        }

        System.out.println("Total Elevation Points: " + e_pts.size());

        return e_pts;
    }

    public static List<Map<String, Double>> getMapPoints() {
        List<Map<String, Double>> rpts = new ArrayList<>();

        for (Point p : points) {
            Map<String, Double> pt = new HashMap<>();
            pt.put("lat", p.getLat());
            pt.put("lon", p.getLon());
            rpts.add(pt);
        }

        return rpts;

    }

    public static ArrayList<Point> getPoints() {
        return points;
    }


    public static IntSummaryStatistics hrSummary() {
        return points.stream()
                    .filter((a) -> a.getHeartRate() > 0)
                    .collect(summarizingInt(Point::getHeartRate));
    }


    public static IntSummaryStatistics cadenceSummary() {
        return points.stream()
                    .filter((a) -> a.getCadence()>0 )
                    .collect(summarizingInt(Point::getCadence));
    }

    public static IntSummaryStatistics powerSummary () {
            return points.stream()
                    .collect(summarizingInt(Point::getPower));
    }

    public static IntSummaryStatistics powerSummary_nonZero () {
        return points.stream()
                .filter((a) -> a.getPower() > 0)
                .collect(summarizingInt(Point::getPower));
    }


    public static double getClimb () {

        return IntStream.range(1, points.size())
                .filter( (a) -> (points.get(a).getElevation()-points.get(a-1).getElevation()) > 0 )
                .mapToDouble( (a) -> points.get(a).getElevation()-points.get(a-1).getElevation() )
                .sum();
    }

    public static double getDescent () {

        return IntStream.range(1, points.size())
                .filter( (a) -> (points.get(a).getElevation()-points.get(a-1).getElevation()) < 0 )
                .mapToDouble( (a) -> points.get(a).getElevation()-points.get(a-1).getElevation() )
                .sum();
    }

    public static long getEpochStartTime () {
        return points.get(0).getEpochTimeStamp();
    }

    public static String getFormattedStartTime() {
        int offset = ZonedDateTime.now().getOffset().getTotalSeconds()/60/60;
        LocalDateTime startTime = LocalDateTime.ofEpochSecond(getEpochStartTime(), 0, ZoneOffset.ofHours(offset));
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mma");
        return dtf.format(startTime);
    }

    public static long getEpochEndTime () {
        return points.get(points.size()-1).getEpochTimeStamp();
    }

    public static String getFormattedEndTime() {
        int offset = ZonedDateTime.now().getOffset().getTotalSeconds()/60/60;
        LocalDateTime endTime = LocalDateTime.ofEpochSecond(getEpochEndTime(), 0, ZoneOffset.ofHours(offset));
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mma");
        return dtf.format(endTime);
    }

    public static String getElapsedRideTime () {
        int offset = ZonedDateTime.now().getOffset().getTotalSeconds()/60/60;
        LocalDateTime startTime = LocalDateTime.ofEpochSecond(getEpochStartTime(), 0, ZoneOffset.ofHours(offset));
        LocalDateTime endTime = LocalDateTime.ofEpochSecond(getEpochEndTime(), 0, ZoneOffset.ofHours(offset));
        Duration ttlTime = Duration.between(startTime, endTime);
        return (ttlTime.toHours() + ":" + ttlTime.toMinutes()%60 + ":" + ttlTime.toMillis()/1000%60);
    }
}
