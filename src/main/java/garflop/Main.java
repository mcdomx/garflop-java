package garflop;

import garflop.Point.*;

import java.io.*;
import java.util.*;
import static java.lang.Math.*;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.filter.ElementFilter;
import org.jdom.input.SAXBuilder;


public class Main {
    public static void main( String[] args ) {

        try {
            // Create a document from a file
            File inputFile = new File(args[0]);
            SAXBuilder s = new SAXBuilder();

            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(inputFile);
            Element rootElement = document.getRootElement();

            Iterator<?> trkpts = rootElement.getDescendants(new ElementFilter("trkpt"));

            while (trkpts.hasNext()) {

                //Topografix GPX elements
                Element trkpt = (Element) trkpts.next();

                Point point = new Point(trkpt.getAttributes());
                if (!point.isValid()) continue;

                //Get elevation of current point
                Element elevation = trkpt.getChild("ele", rootElement.getNamespace());
                point.setElevation(Double.parseDouble(elevation.getValue()));

                //Garmin Trackpoint Extensions
                //Check if extensions exist.  If they do, record the values.
                Element pt_extensions = trkpt.getChild("extensions", trkpt.getNamespace());
                if ( pt_extensions != null ) {
                    // put the extensions in a list
                    List<Element> extension_elements = pt_extensions.getChildren();

                    //iterate over the list and allocate element values to their cumulative variables
                    for ( Element e : extension_elements ) {
                        // check for power
                        if ( e.getName().equals("power") )
                            point.setPower(Integer.parseInt(e.getValue()));

                        //check for TrackPointExtensions
                        if ( e.getName().equals("TrackPointExtension") ) {
                            //iterate through the track point extensions and store its values
                            List<Element> tpxElements = e.getChildren();
                            for ( Element tpx : tpxElements ) {
                                String tpxName = tpx.getName();
                                if ( tpxName.equals("hr") )
                                    point.setHeartRate(Integer.parseInt(tpx.getValue()));
                                else if ( tpxName.equals("cad") )
                                    point.setCadence(Integer.parseInt(tpx.getValue()));
                                }
                            } // end if TrackPointExtension

                        } // end if extension elements

                    } // end if for garmin trackpoint extensions

            } // end while()

            System.out.println("Total Distance: " + (double) Math.round(totalDistance(Point.getPoints()) * 10d) / 10d);
            System.out.println("Avg HR (max): " + (double) Math.round(Point.getAvgHR()) * 10d / 10d + " (" + Point.getMaxHR() + ")");
            System.out.println("Avg Cadence (max): " + (double) Math.round(Point.getAvgCadence()) * 10d / 10d + " (" + Point.getMaxCadence() + ")");
            HashMap<String, Double>climbStats = Point.getClimbStats();
            System.out.println("Total Climb: " + (double) Math.round(climbStats.get("ttlClimb") * 10d) / 10d);
            System.out.println("Total Descent: " + (double) Math.round(climbStats.get("ttlDescent") * 10d) / 10d);


        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (JDOMException e) {
            e.printStackTrace();
        }  // end try/catch()
    } // end main()




    private static double totalDistance ( List<Point> points ) {

        Point prevPoint;
        Point curPoint;
        double ttlDistance = 0.0;

        ListIterator<Point> iterablePoints = points.listIterator();
        prevPoint = iterablePoints.next();

        while (iterablePoints.hasNext()) {
            curPoint = iterablePoints.next();
            ttlDistance += dist(curPoint, prevPoint);

            prevPoint = curPoint;

        }

        return ttlDistance;

    } // end totalDistance()

    // calc distance between 2 points
    private static double dist (Point curPoint, Point prevPoint){
        return calc_distance(prevPoint.getLat(), prevPoint.getLon(),
                curPoint.getLat(), curPoint.getLon());
    }

    /*
    ===============================================================================
    get distance in metres between 2 lat/lon coordinates:
    Vincenty Formula http://www.movable-type.co.uk/scripts/latlong-vincenty.html
    SOURCE: https://github.com/janantala/GPS-distance
    */
    private static double calc_distance ( double lat1, double lon1, double lat2, double lon2){

        double a = 6378137, b = 6356752.314245, f = 1 / 298.257223563;
        double L = (lon2 - lon1) * (Math.PI / 180);
        double U1 = atan((1 - f) * tan(lat1 * (Math.PI / 180)));
        double U2 = atan((1 - f) * tan(lat2 * (Math.PI / 180)));
        double sinU1 = sin(U1), cosU1 = cos(U1);
        double sinU2 = sin(U2), cosU2 = cos(U2);
        double cosSqAlpha;
        double sinSigma;
        double cos2SigmaM;
        double cosSigma;
        double sigma;

        double lambda = L, lambdaP, iterLimit = 100;
        do {
            double sinLambda = sin(lambda), cosLambda = cos(lambda);
            sinSigma = sqrt((cosU2 * sinLambda)
                    * (cosU2 * sinLambda)
                    + (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda)
                    * (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda)
            );
            if (sinSigma == 0) {
                return 0;
            }

            cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * cosLambda;
            sigma = atan2(sinSigma, cosSigma);
            double sinAlpha = cosU1 * cosU2 * sinLambda / sinSigma;
            cosSqAlpha = 1 - sinAlpha * sinAlpha;
            cos2SigmaM = cosSigma - 2 * sinU1 * sinU2 / cosSqAlpha;

            double C = f / 16 * cosSqAlpha * (4 + f * (4 - 3 * cosSqAlpha));
            lambdaP = lambda;
            lambda = L + (1 - C) * f * sinAlpha
                    * (sigma + C * sinSigma
                    * (cos2SigmaM + C * cosSigma
                    * (-1 + 2 * cos2SigmaM * cos2SigmaM)
            )
            );

        } while (abs(lambda - lambdaP) > 1e-12 && --iterLimit > 0);

        if (iterLimit == 0) {
            return 0;
        }

        double uSq = cosSqAlpha * (a * a - b * b) / (b * b);
        double A = 1 + uSq / 16384
                * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
        double B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));
        double deltaSigma =
                B * sinSigma
                        * (cos2SigmaM + B / 4
                        * (cosSigma
                        * (-1 + 2 * cos2SigmaM * cos2SigmaM) - B / 6 * cos2SigmaM
                        * (-3 + 4 * sinSigma * sinSigma)
                        * (-3 + 4 * cos2SigmaM * cos2SigmaM)));

        return b * A * (sigma - deltaSigma);
    } // end calc_distance()

} // end main()



