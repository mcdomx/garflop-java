package garflop;

import java.io.*;
import java.text.DecimalFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import static com.sun.xml.internal.xsom.impl.Const.schemaNamespace;
import static java.time.temporal.ChronoField.*;
import org.jdom.*;
import org.jdom.filter.ElementFilter;
import org.jdom.input.SAXBuilder;


public class Main {
    public static void main( String[] args ) {

        try {
            // Create a new SAXBuilder document from file provided as argument
            File inputFile = new File(args[0]);
            SAXBuilder s = new SAXBuilder();
            Document document = s.build(inputFile);
            Element rootElement = document.getRootElement();
            Namespace rootNameSpace = rootElement.getNamespace();

            //Create new RoutePoint object to store points in
            RoutePoints route = new RoutePoints();

            Iterator<?> trkpts = rootElement.getDescendants(new ElementFilter("trkpt"));

            while (trkpts.hasNext()) {

                //set trackpoint element
                Element trkpt = (Element) trkpts.next();

                //create new point
                Point point = new Point(trkpt.getAttributes(), route);
                if (!point.isValid()) continue;

                //find "bottom" elements and store them in route points
                iterateElements(trkpt, point);

            } // end while()

            displaySummary(route);

        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (JDOMException e) {
            e.printStackTrace();
        }  // end try/catch()
    } // end main()


    private static void iterateElements (Element element, Point point) {
        List<Element> sub_elements = element.getChildren();

        //iterate over the list recursively until a bottom element is found
        for ( Element e : sub_elements ) {
            iterateElements(e, point);
        }

        switch (element.getName()){
            case "ele":
                point.setElevation(Double.parseDouble(element.getValue()));
                break;
            case "time":
                DateTimeFormatter dtf = DateTimeFormatter.ISO_INSTANT;
                Long epochStamp = dtf.parse(element.getValue()).getLong(INSTANT_SECONDS);
                point.setEpochTimeStamp(epochStamp);
                break;
            case "power":
                point.setPower(Integer.parseInt(element.getValue()));
                break;
            case "hr":
                point.setHeartRate(Integer.parseInt(element.getValue()));
                break;
            case "cad":
                point.setCadence(Integer.parseInt(element.getValue()));
                break;
        }

    } // end iterateElements()


    private static void displaySummary (RoutePoints route) {
        displayTimeDistanceRate(route);
        displayHR(route);
        displayClimb(route);
        displayPower(route);
        displayCadence(route);
    } // end displaySummary


    private static void displayTimeDistanceRate(RoutePoints route) {

        int offset = ZonedDateTime.now().getOffset().getTotalSeconds()/60/60;

        LocalDateTime startTime = LocalDateTime.ofEpochSecond(route.getEpochStartTime(), 0, ZoneOffset.ofHours(offset));
        LocalDateTime endTime = LocalDateTime.ofEpochSecond(route.getEpochEndTime(), 0, ZoneOffset.ofHours(offset));
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mma");

        System.out.println("Start Time: " + dtf.format(startTime));
        System.out.println("End Time: " + dtf.format(endTime));
        Duration ttlTime = Duration.between(startTime, endTime);
        System.out.println("Total Time: " + ttlTime.toHours() + ":" + ttlTime.toMinutes()%60 + ":" + ttlTime.toMillis()/1000%60);

        Double ttlDistance = DistanceCalculations.totalDistanceInKM(route.getPoints());
        System.out.println("Total Distance: " + Double.valueOf(new DecimalFormat("#.#").format(ttlDistance)));
        Double timeFrac = ttlTime.toHours() + ttlTime.toMinutes()%60/60.0 + ttlTime.toMillis()/1000%60/60.0/60.0;
        Double rate = ttlDistance / timeFrac;
        System.out.println("Rate: " + Math.round(rate*100.0)/100.0);

    } // end displayTimeDistanceRate()


    private static void displayHR(RoutePoints route) {
        IntSummaryStatistics hr = route.hrSummary();
        if (hr.getAverage() != 0) {
            System.out.println("Avg HR (max): " +
                    (int) hr.getAverage() +
                    " (" + hr.getMax() + ")");
        }
    } // end displayHR()


    private static void displayClimb(RoutePoints route) {
        System.out.println("Total Climb (Descent): " + (int) route.getClimb() + " (" + (int) route.getDescent() + ")");
    } // end displayClimb()


    private static void displayPower(RoutePoints route) {
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
    }// end displayPower()


    private static void displayCadence(RoutePoints route) {
        IntSummaryStatistics cad = route.cadenceSummary();
        if (cad.getAverage() != 0 ) {
            System.out.println("Avg Cadence (max): " +
                    (int) cad.getAverage() +
                    " (" + cad.getMax() + ")");
        }
    } // end displayTimes()


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

        Set<String> schemaSet = new HashSet<>(Arrays.asList(schemas));

        System.out.println("Now for my final trick!");

        for (String i : schemaSet)
            System.out.println(i);

    } // end getSchemas()

} // end main()
