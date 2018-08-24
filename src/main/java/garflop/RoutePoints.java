package garflop;

import com.sun.xml.internal.xsom.impl.scd.Iterators;

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

    private RoutePoints () {
    }

    public static RoutePoints createRoutePoints() {
        if (unique == null)
            unique = new RoutePoints();

        return unique;
    }

    public static void addPoint(Point point) {
        points.add(point);
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

    public static long getEpochEndTime () {
        return points.get(points.size()-1).getEpochTimeStamp();
    }


}
