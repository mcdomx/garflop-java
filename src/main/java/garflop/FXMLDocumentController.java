package garflop;

import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.AreaChart;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import org.json.JSONArray;

import java.io.File;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Supplier;

import static java.lang.System.exit;

public class FXMLDocumentController implements Initializable {

    private HBox map;
    private AreaChart elevChart;
    VBox statGrid;



    @FXML
    private VBox vbox;

    @FXML
    private void handleExitAction(ActionEvent event) {
        exit(0);
    }

    @FXML
    private void handleOpenGPXFile() {
        Stage stage = Main.getPrimaryStage();

        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
          new FileChooser.ExtensionFilter("GPX File", "*.gpx")
        );
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            Main.processFile(file);

            drawMap();

            double dist = RoutePoints.getDistance();
            drawElevationChart(dist);

            drawStatistics();
        }
    }

    //    http://fxexperience.com/2011/05/maps-in-javafx-2-0/
    static { // use system proxy settings when standalone application
        System.setProperty("java.net.useSystemProxies", "true");
    }


    //    https://blogs.oracle.com/java/javafx-webview-overview
    private void drawMap() {

        if (map != null)
            vbox.getChildren().remove(map);

        map = new GoogleMap();

        vbox.getChildren().add(map);

    } // end drawMap()

    // JavaScript interface object. All methods in this object are available
    // to javascript file.
    public class JavaApp {
        public JSONArray getLatLonPoints() {
            return RoutePoints.getLatLonPoints();
        }
    }


    private void drawElevationChart(double dist) {
        if (elevChart != null)
            vbox.getChildren().remove(elevChart);
        elevChart = new ElevationChart(dist).getAreaChart();
        vbox.getChildren().add(elevChart);
    }


    private void drawStatistics(){
        if (statGrid != null)
            vbox.getChildren().remove(statGrid);

        double maxWidth = 400;
        statGrid = new VBox(5);
        statGrid.setAlignment(Pos.BOTTOM_CENTER);
        statGrid.setMaxWidth(maxWidth);

        statGrid.setPadding(new Insets(10));

        //Heading
        Label lblHeading = new Label();
        lblHeading.setText("Ride Statistics");
        makeHeader(lblHeading);
        Node lblHbox = wrapInHbox(lblHeading);
        lblHbox.getStyleClass().add("statBox");
        statGrid.getChildren().add(lblHbox);

        // Start and End Time
        statGrid.getChildren().add(
                makeHbox(Arrays.asList(
                        makeStatBox("Start Time", () -> RoutePoints.getFormattedStartTime()),
                        makeStatBox("End Time", () -> RoutePoints.getFormattedEndTime())
                ),
                        maxWidth)
        );

        // Elapsed Time, Distance and Rate
        statGrid.getChildren().add(
                makeHbox(Arrays.asList(
                        makeStatBox("Elapsed Time", () -> RoutePoints.getElapsedRideTime()),
                        makeStatBox("Distance", () -> RoutePoints.getDistanceInKM()),
                        makeStatBox("Rate", () -> RoutePoints.getRate())
                ),
                        maxWidth)
        );

        //HeartRate
        statGrid.getChildren().add(
                makeHbox(Arrays.asList(
                        makeStatBox("Avg HR", () -> HeartRate.getAvgHR()),
                        makeStatBox("Max HR", () -> HeartRate.getMaxHR())
                ),
                        maxWidth)
        );

        //Climb and Descent
        statGrid.getChildren().add(
                makeHbox(Arrays.asList(
                        makeStatBox("Climb", () -> Elevation.getClimb()),
                        makeStatBox("Descent", () -> Elevation.getDescent())
                ),
                        maxWidth)
        );

        //Power
        statGrid.getChildren().add(
                makeHbox(Arrays.asList(
                        makeStatBox("Avg Power", () -> Power.getAvgPower()),
                        makeStatBox("Non-0 Power", () -> Power.getAvgPowerNonZero()),
                        makeStatBox("Max Power", () -> Power.getMaxPower())
                ),
                        maxWidth)
        );

        //Cadence
        statGrid.getChildren().add(
                makeHbox(Arrays.asList(
                        makeStatBox("Avg Cadence", () -> Cadence.getAvgCadence()),
                        makeStatBox("Max Cadence", () -> Cadence.getMaxCadence())
                ),
                        maxWidth)
        );

//        statGrid.setMinSize(win_width, win_height*.3);

        vbox.getChildren().add(statGrid);

    }

    private static Node makeHbox (List<Node> list, double maxWidth) {
        HBox hb = new HBox(5);
        for (Node n : list) {
            hb.getChildren().add(n);
            HBox.setHgrow(n, Priority.ALWAYS);
        }
        return  hb;
    }

    private static Node makeStatBox(String title, Supplier<String> valFunc) {

        //vbox
        VBox statBox = new VBox();
        statBox.getStyleClass().add("statBox");
        statBox.setAlignment(Pos.CENTER);

        //Title
        Label lblTitle = new Label();
        makeHeader(lblTitle);
        lblTitle.setText(title);
        Node hboxTitle = wrapInHbox(lblTitle);
        statBox.getChildren().add(hboxTitle);

        //Value
        Label lblValue = new Label();
        lblValue.setText(valFunc.get());
        Node hboxValue = wrapInHbox(lblValue);
        makeValue(hboxValue);
        statBox.getChildren().add(hboxValue);

        return statBox;

    }

    private static Node wrapInHbox (Node node) {
        HBox newHBox = new HBox();
        newHBox.getChildren().add(node);
        newHBox.setAlignment(Pos.CENTER);
        return newHBox;
    }

    private static void makeHeader (Node node) {
        GridPane.setMargin(node, new Insets(1));
        GridPane.setFillHeight(node, true);
        GridPane.setFillWidth(node, true);
        node.getStyleClass().add("statHeader");
    }

    private static void makeValue (Node node) {
        GridPane.setMargin(node, new Insets(1));
        GridPane.setFillHeight(node, true);
        GridPane.setFillWidth(node, true);
        node.getStyleClass().add("statValue");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    } // end initialize()

} // end GarflopController()