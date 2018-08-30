package garflop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.AreaChart;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import static java.lang.System.exit;

public class FXMLDocumentController implements Initializable {

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
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            Main.processFile(file);
            AreaChart elevChart = new ElevationChart().getAreaChart();
            vbox.getChildren().add(elevChart);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {


//        AreaChart elevChart = new ElevationChart().getAreaChart();
//        vbox.getChildren().add(elevChart);


    } // end initialize()

} // end GarflopController()