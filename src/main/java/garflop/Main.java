package garflop;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

import static com.sun.xml.internal.xsom.impl.Const.schemaNamespace;

import org.jdom.*;
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

            //Create new RoutePoint object to store points in
            RoutePoints route = new RoutePoints();

            Iterator<?> trkpts = rootElement.getDescendants(new ElementFilter("trkpt"));

            while (trkpts.hasNext()) {

                //Topografix GPX elements
                Element trkpt = (Element) trkpts.next();

                Point point = new Point(trkpt.getAttributes(), route);
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

            System.out.println("Total Distance: " + Double.valueOf(new DecimalFormat("#.#").format(DistanceCalculations.totalDistanceInKM(route.getPoints()))));

            IntSummaryStatistics hr = route.hrSummary();
            if (hr.getAverage() != 0) {
                System.out.println("Avg HR (max): " +
                        (int) hr.getAverage() +
                        " (" + hr.getMax() + ")");
            }

            System.out.println("Total Climb (Descent): " + (int)route.getClimb() + " (" + (int)route.getDescent() + ")");


            IntSummaryStatistics pwr = route.powerSummary();
            if (pwr.getAverage() != 0) {
                System.out.println("Avg Power (max): " +
                        (int) pwr.getAverage() +
                        " (" + (int) pwr.getMax() + ")");
                IntSummaryStatistics pwrNonZero = route.powerSummary_nonZero();
                System.out.println("Avg Power - Non Zero (max): " +
                        (int) pwrNonZero.getAverage() +
                        " (" + (int) pwrNonZero.getMax() + ")");
            }

            IntSummaryStatistics cad = route.cadenceSummary();
            if (cad.getAverage() != 0 ) {
                System.out.println("Avg Cadence (max): " +
                        (int) cad.getAverage() +
                        " (" + cad.getMax() + ")");
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (JDOMException e) {
            e.printStackTrace();
        }  // end try/catch()
    } // end main()





    private static void getSchemas(Element root) {

        Namespace xsiNamespace = root.getNamespace();

        System.out.println("Root Namespace: " + root.getNamespace());
        System.out.println("SchemaNamespace: " + schemaNamespace);

        List<Attribute> rootAttrs = root.getAttributes();
        for (int i=0; i<rootAttrs.size(); i++) {
            if (rootAttrs.get(i).getNamespacePrefix().equals("xsi"))
                xsiNamespace = rootAttrs.get(i).getNamespace();
            System.out.println(rootAttrs.get(i).getNamespace() + "  " + rootAttrs.get(i).getName());
        }

        String[] schemas = root.getAttributeValue("schemaLocation", xsiNamespace).split(" ");

        Set<String> schemaSet = new HashSet<String>(Arrays.asList(schemas));

        System.out.println("Now for my final trick!");

        for (String i : schemaSet)
            System.out.println(i);



    } // end getSchemas()

} // end main()





