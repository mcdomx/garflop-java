package garflop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.chart.AreaChart;
import javafx.scene.control.Label;
import javafx.scene.effect.BlendMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Supplier;

import static java.lang.System.exit;

public class FXMLDocumentController implements Initializable {

    private AreaChart elevChart;
    GridPane statGrid;

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
            drawElevationChart();
            drawStatistics();
        }
    }

    private void drawElevationChart() {
        if (elevChart != null)
            vbox.getChildren().remove(elevChart);
        if (statGrid != null)
            vbox.getChildren().remove(statGrid);
        elevChart = new ElevationChart().getAreaChart();
        vbox.getChildren().add(elevChart);
    }

    private void drawStatistics(){
        statGrid = new GridPane();
        statGrid.setId("statGrid");
        statGrid.setAlignment(Pos.BOTTOM_CENTER);
        statGrid.setVgap(5);
        statGrid.setHgap(5);
        statGrid.setPadding(new Insets(10,10,10,10));
//        statGrid.setGridLinesVisible(true);

        //Heading
        Label lblHeading = new Label();
        lblHeading.setText("Ride Statistics");
        makeHeader(lblHeading);
        Node lblHbox = wrapInHbox(lblHeading);
        makeHeader(lblHbox);
        addToGrid(statGrid,lblHbox,0,0);

        //Start Time
        Node startTime = makeStatBox("Start Time", () -> RoutePoints.getFormattedStartTime());
        statGrid.add(startTime,0,1);

        //End Time
        Node endTime = makeStatBox("End Time", () -> RoutePoints.getFormattedEndTime());
        statGrid.add(endTime,1,1);

        //Elapsed Ride Time
        Node rideTime = makeStatBox("Elapsed Time", () -> RoutePoints.getElapsedRideTime());
        statGrid.add(rideTime,0,2);


        //Distance
        Node rideDist = makeStatBox("Distance", () -> RoutePoints.getDistanceInKM());
        statGrid.add(rideDist,1,2);

        //Rate
        Node rideRate = makeStatBox("Rate", () -> RoutePoints.getRate());
        statGrid.add(rideRate,2,2);

        //HeartRate
        Node avgHR = makeStatBox("Avg HR", () -> HeartRate.getAvgHR());
        statGrid.add(avgHR,0,3);

        Node maxHR = makeStatBox("Max HR", () -> HeartRate.getMaxHR());
        statGrid.add(maxHR,1,3);

        //Climb and Descent
        Node climb = makeStatBox("Climb", () -> Elevation.getClimb());
        statGrid.add(climb,0,4);

        Node descent = makeStatBox("Descent", () -> Elevation.getDescent());
        statGrid.add(descent,1,4);

        //Power
        Node avgPwr = makeStatBox("Avg Power", () -> Power.getAvgPower());
        statGrid.add(avgPwr,0,5);
//        addStatRow(statGrid, 8,
//                "Avg Power", 0,
//                () -> Power.getAvgPower(), 1);
        Node avgNZeroPwr = makeStatBox("Avg Non-Zero Power", () -> Power.getAvgPowerNonZero());
        statGrid.add(avgNZeroPwr,1,5);
//        addStatRow(statGrid, 8,
//                "Avg Non-Zero Power", 2,
//                () -> Power.getAvgPowerNonZero(), 3);
        Node maxPwr = makeStatBox("Max Power", () -> Power.getMaxPower());
        statGrid.add(maxPwr,2,5);
//        addStatRow(statGrid, 8,
//                "Max Power", 4,
//                () -> Power.getMaxPower(), 5);

        //Cadence
        Node avgCad = makeStatBox("Avg Cadence", () -> Cadence.getAvgCadence());
        statGrid.add(avgCad,0,6);
//        addStatRow(statGrid, 9,
//                "Avg Cadence", 0,
//                () -> Cadence.getAvgCadence(), 1);
        Node maxCad = makeStatBox("Max Cadence", () -> Cadence.getMaxCadence());
        statGrid.add(maxCad,1,6);
//        addStatRow(statGrid, 9,
//                "Max Cadence", 2,
//                () -> Cadence.getMaxCadence(), 3);


        vbox.getChildren().add(statGrid);

    }


    private static Node makeStatBox(String title, Supplier<String> valFunc) {

        //statBox
        GridPane statBox = new GridPane();
        statBox.getStyleClass().add("statBox");
        statBox.setAlignment(Pos.CENTER);


        //Title
        Label lblTitle = new Label();
        makeHeader(lblTitle);
        lblTitle.setText(title);
        Node lblHBox = wrapInHbox(lblTitle);
        makeHeader(lblHBox);
        statBox.add(lblHBox,0,0);

        //Value
        Label lblValue = new Label();
        makeValue(lblValue);
        lblValue.setText(valFunc.get());
        statBox.add(lblValue,0,1);

        return statBox;

    }

    private static Node wrapInHbox (Node node) {
        HBox newHBox = new HBox();
        newHBox.getChildren().add(node);
        newHBox.setAlignment(Pos.CENTER);
        return newHBox;
    }



    private static void makeHeader (Node node) {
        GridPane.setMargin(node, new Insets(1,1,1,1));
        GridPane.setFillHeight(node, true);
        GridPane.setFillWidth(node, true);
        node.getStyleClass().add("statHeader");
    }

    private static void makeValue (Node node) {
        GridPane.setMargin(node, new Insets(1,1,1,1));
        GridPane.setFillHeight(node, true);
        GridPane.setFillWidth(node, true);
        node.getStyleClass().add("statValue");
    }

    private static void addToGrid (GridPane grid, Node node, int col, int row) {
        grid.add(node, col, row, 6 ,1);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {


    } // end initialize()

} // end GarflopController()