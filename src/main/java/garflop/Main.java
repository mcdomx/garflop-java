package garflop;


import java.io.*;
import java.text.DecimalFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

import static garflop.RoutePoints.getMapPoints;
import static garflop.RoutePoints.getPoints;
import static garflop.RoutePoints.resetRoutePoints;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.jdom.*;
import org.jdom.filter.ElementFilter;
import org.jdom.input.SAXBuilder;

import org.json.JSONWriter;
import java.io.FileWriter;


public class Main extends Application {

    static Stage primaryStage;


    public static void main( String[] args ) {
        launch(args);
    } // end main()


    @Override
    public void start(Stage stage) throws Exception {

        primaryStage = stage;

        //Root
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/garflop.fxml"));

        //Scene
        stage.setTitle("Route Analysis");
        Scene scene = new Scene(root, 800, 800);
        //add stylesheet
        scene.getStylesheets().
                add(Main.class.getResource("/fxml/garflop.css").toExternalForm());

        //Stage
        stage.setScene(scene);
        stage.show();

    } // end start()

    public static Stage getPrimaryStage() {
        return primaryStage;
    }


    public static void processFile(File inputFile) {

        try {
            // Create a new SAXBuilder document from file provided as argument
//            File inputFile = new File(fileName);
            SAXBuilder s = new SAXBuilder();
            Document document = s.build(inputFile);
            Element rootElement = document.getRootElement();
            Namespace rootNameSpace = rootElement.getNamespace();

            //Create new RoutePoint object to store points in
            resetRoutePoints();
            RoutePoints route = RoutePoints.createRoutePoints();

            Iterator<?> trkpts = rootElement.getDescendants(new ElementFilter("trkpt"));

            while (trkpts.hasNext()) {

                //set trackpoint element
                Element trkpt = (Element) trkpts.next();

                //create new point
                Point point = new Point(trkpt.getAttributes(), route);
                if (!point.isValid()) continue;

                //find data elements and store them in route points
                iterateElements(trkpt, point);

            } // end while()

            displaySummary(route);
            List<Map<String, Double>> rpts = getMapPoints();


        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (JDOMException e) {
            e.printStackTrace();
        }  // end try/catch()

    }



    // Drops a file of route points for the mapping function to read
    // The file is a JSON file that is an Array of dictionary elements where
    //  each dictionary element is { "lat": lat-value, "lon":lon-value }
    private static void dropPointsFile() {

        try {
            Writer writer = new FileWriter("routePoints.JSON");
            JSONWriter jsonWriter = new JSONWriter(writer);
            ArrayList<Point> points = getPoints();

            jsonWriter.array();

                for ( Point p : points ) {
                    jsonWriter
                            .object()
                            .key("lat")
                            .value(p.getLat());
                    jsonWriter
                            .key("lon")
                            .value(p.getLon());
                    jsonWriter.endObject();
                }

            jsonWriter.endArray();

            writer.close();

        } catch (IOException e) {
            System.out.println("can't create file or writer.");
            return;
        }

    }

    private static void iterateElements (Element element, Point point) {
        List<Element> sub_elements = element.getChildren();

        //iterate over the list recursively until a bottom element is found
        for ( Element e : sub_elements ) {
            iterateElements(e, point);
        }

        switch (element.getName()){
            case "ele":
                new Elevation().set(point, element);
                break;
            case "time":
                new TimePoint().set(point, element);
                break;
            case "power":
                new Power().set(point, element);
                break;
            case "hr":
                new HeartRate().set(point, element);
                break;
            case "cad":
                new Cadence().set(point, element);
                break;
        }

    } // end iterateElements()


    private static void displaySummary (RoutePoints route) {
        displayTimeDistanceRate(route);

        HeartRate.display(route);
        Elevation.display(route);
        Power.display(route);
        Cadence.display(route);
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

        Double ttlDistance = DistanceCalculations.totalDistanceInKM(getPoints());
        System.out.println("Total Distance: " + Double.valueOf(new DecimalFormat("#.#").format(ttlDistance)));
        Double timeFrac = ttlTime.toHours() + ttlTime.toMinutes()%60/60.0 + ttlTime.toMillis()/1000%60/60.0/60.0;
        Double rate = ttlDistance / timeFrac;
        System.out.println("Rate: " + Math.round(rate*100.0)/100.0);

    } // end displayTimeDistanceRate()




//    private static void getSchemas(Element root) {
//
//        Namespace xsiNamespace = root.getNamespace();
//
//        System.out.println("Root Namespace: " + root.getNamespace());
//        System.out.println("SchemaNamespace: " + schemaNamespace);
//
//        List<Attribute> rootAttrs = root.getAttributes();
//        for (int i=0; i<rootAttrs.size(); i++) {
//            if (rootAttrs.get(i).getNamespacePrefix().equals("xsi"))
//                xsiNamespace = rootAttrs.get(i).getNamespace();
//            System.out.println(rootAttrs.get(i).getNamespace() + "  " + rootAttrs.get(i).getName());
//        }
//
//        String[] schemas = root.getAttributeValue("schemaLocation", xsiNamespace).split(" ");
//
//        Set<String> schemaSet = new HashSet<>(Arrays.asList(schemas));
//
//        System.out.println("Now for my final trick!");
//
//        for (String i : schemaSet)
//            System.out.println(i);
//
//    } // end getSchemas()

} // end main()
