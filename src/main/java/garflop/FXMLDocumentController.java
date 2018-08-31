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
        statGrid.setVgap(10);
        statGrid.setHgap(10);
        statGrid.setPadding(new Insets(25,25,25,25));
        statGrid.setGridLinesVisible(true);

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
//        addStatRow(statGrid, 1,
//                "Start Time", 0,
//                () -> RoutePoints.getFormattedStartTime(), 1);

        //End Time
        addStatRow(statGrid, 2,
                "End Time", 0,
                () -> RoutePoints.getFormattedEndTime(), 1);

        //Elapsed Ride Time
        addStatRow(statGrid, 3,
                "Elapsed Ride Time", 0,
                () -> RoutePoints.getElapsedRideTime(), 1);

        //Distance
        addStatRow(statGrid, 4,
                "Distace (KM)", 0,
                () -> RoutePoints.getDistanceInKM(), 1);

        //Rate
        addStatRow(statGrid, 5,
                "Rate (KM/hr)", 0,
                () -> RoutePoints.getRate(), 1);

        //HeartRate
        addStatRow(statGrid, 6,
                "Avg HR", 0,
                () -> HeartRate.getAvgHR(), 1);
        addStatRow(statGrid, 6,
                "Max HR", 2,
                () -> HeartRate.getMaxHR(), 3);


        //Climb and Descent
        addStatRow(statGrid, 7,
                "Climb", 0,
                () -> Elevation.getClimb(), 1);
        addStatRow(statGrid, 7,
                "Descent", 2,
                () -> Elevation.getDescent(), 3);

        //Power
        addStatRow(statGrid, 8,
                "Avg Power", 0,
                () -> Power.getAvgPower(), 1);
        addStatRow(statGrid, 8,
                "Avg Non-Zero Power", 2,
                () -> Power.getAvgPowerNonZero(), 3);
        addStatRow(statGrid, 8,
                "Max Power", 4,
                () -> Power.getMaxPower(), 5);

        //Cadence
        addStatRow(statGrid, 9,
                "Avg Cadence", 0,
                () -> Cadence.getAvgCadence(), 1);
        addStatRow(statGrid, 9,
                "Max Cadence", 2,
                () -> Cadence.getMaxCadence(), 3);




        vbox.getChildren().add(statGrid);

    }

    private static void addStatRow(GridPane grid, int row, String title, int titleCol, Supplier<String> valFunc, int valueCol) {

        //Title
        Label lblTitle = new Label();
        makeHeader(lblTitle);
        lblTitle.setText(title);
        grid.add(lblTitle, titleCol, row);

        //Value
        Label lblValue = new Label();
        lblValue.getStyleClass().add("statValue");
        lblValue.setText(valFunc.get());
        grid.add(lblValue, valueCol, row);

    }

    private static Node makeStatBox(String title, Supplier<String> valFunc) {

        //statBox
        GridPane statBox = new GridPane();
        statBox.getStyleClass().add("statBox");


        //Title
        Label lblTitle = new Label();
        makeHeader(lblTitle);
        lblTitle.setText(title);
        Node lblHBox = wrapInHbox(lblTitle);
        makeHeader(lblHBox);
        statBox.add(lblHBox,0,0);

        //Value
        Label lblValue = new Label();
        lblValue.getStyleClass().add("statValue");
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
        GridPane.setMargin(node, new Insets(2,2,2,2));
        GridPane.setFillHeight(node, true);

        node.getStyleClass().add("statHeader");
//        node.setBlendMode(BlendMode.DIFFERENCE);
    }

    private static void addToGrid (GridPane grid, Node node, int col, int row) {
        grid.add(node, col, row, 6 ,1);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {


    } // end initialize()

} // end GarflopController()