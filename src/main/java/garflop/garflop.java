package garflop;



import static java.lang.Math.*;
import java.io.*;
import java.util.*;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.filter.ElementFilter;
import org.jdom.input.SAXBuilder;

/**
 * Hello world!
 *
 */
public class garflop
{
    public static void main( String[] args )
    {
        for (String item : args) {
            System.out.println(item);
        }

    try {
        // Create a document from a file
        File inputFile = new File(args[0]);
        SAXBuilder saxBuilder = new SAXBuilder();
        Document document = saxBuilder.build(inputFile);
        Element rootElement = document.getRootElement();
        Iterator<Element> elements = rootElement.getDescendants(new ElementFilter("trkpt"));


        HashMap<String, Double> prev_point = new HashMap();
        HashMap<String, Double> cur_point = new HashMap();
        Double ttlDistance = 0.0;

        while (elements.hasNext()) {
            List<Attribute> point = elements.next().getAttributes();

            if (    (point.get(0).getName() == "lat" && point.get(1).getName() == "lon") ||
                    (point.get(0).getName() == "lon" && point.get(1).getName() == "lat")    )
                {
                    cur_point.put(point.get(0).getName(), Double.parseDouble(point.get(0).getValue()));
                    cur_point.put(point.get(1).getName(), Double.parseDouble(point.get(1).getValue()));

                    //If prev point has a value, measure the distance between points
                    if ( !prev_point.keySet().isEmpty() ) {
                        ttlDistance += dist(prev_point, cur_point);
                    }

                    prev_point.put("lat", cur_point.get("lat"));
                    prev_point.put("lon", cur_point.get("lon"));

                }
        } // end while()

        System.out.println(ttlDistance);


    } catch(JDOMException e) {
        e.printStackTrace();
    } catch(IOException ioe){
        ioe.printStackTrace();
    } // end try/catch()

    } // end main()

    

    static double dist (HashMap<String, Double> cur_point, HashMap<String, Double> prev_point) {
        return calc_distance(   prev_point.get("lat"), prev_point.get("lon"),
                                cur_point.get("lat"), cur_point.get("lon")  );
    }

    /*
    ===============================================================================
    get distance in metres between 2 points:
    Vincenty Formula http://www.movable-type.co.uk/scripts/latlong-vincenty.html
    SOURCE: https://github.com/janantala/GPS-distance
    */
    static double calc_distance (double lat1, double lon1, double lat2, double lon2) {

        double a = 6378137, b = 6356752.314245, f = 1 / 298.257223563;
        double L = (lon2 - lon1) * (Math.PI / 180);
        double U1 = atan( (1 - f) * tan(lat1 * (Math.PI / 180)) );
        double U2 = atan( (1 - f) * tan(lat2 * (Math.PI / 180)) );
        double sinU1 = sin(U1), cosU1 = cos(U1);
        double sinU2 = sin(U2), cosU2 = cos(U2);
        double cosSqAlpha;
        double sinSigma;
        double cos2SigmaM;
        double cosSigma;
        double sigma;

        double lambda = L, lambdaP, iterLimit = 100;
        do
        {
            double sinLambda = sin(lambda), cosLambda = cos(lambda);
            sinSigma = sqrt(	(cosU2 * sinLambda)
                    * (cosU2 * sinLambda)
                    + (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda)
                    * (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda)
            );
            if (sinSigma == 0)
            {
                return 0;
            }

            cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * cosLambda;
            sigma = atan2(sinSigma, cosSigma);
            double sinAlpha = cosU1 * cosU2 * sinLambda / sinSigma;
            cosSqAlpha = 1 - sinAlpha * sinAlpha;
            cos2SigmaM = cosSigma - 2 * sinU1 * sinU2 / cosSqAlpha;

            double C = f / 16 * cosSqAlpha * (4 + f * (4 - 3 * cosSqAlpha));
            lambdaP = lambda;
            lambda = 	L + (1 - C) * f * sinAlpha
                    * 	(sigma + C * sinSigma
                    * 	(cos2SigmaM + C * cosSigma
                    * 	(-1 + 2 * cos2SigmaM * cos2SigmaM)
            )
            );

        } while ( abs(lambda - lambdaP) > 1e-12 && --iterLimit > 0);

        if (iterLimit == 0)
        {
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

        double s = b * A * (sigma - deltaSigma);

        return s;
    }
}
