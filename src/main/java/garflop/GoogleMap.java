package garflop;

import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.json.JSONArray;

public class GoogleMap extends HBox {

    //    https://blogs.oracle.com/java/javafx-webview-overview
    public GoogleMap(){

        try {
            WebView webView = new WebView();
            WebEngine webEngine = webView.getEngine();
            webEngine.load(getClass().getResource("/html/map.html").toString());

            //When page is loaded, connect the JavaApp class with the webEngine
            //Methods in the JavaApp class are available to javascript that is
            //loaded in the webengine.
            webEngine.getLoadWorker().stateProperty().addListener(
                    (ObservableValue<? extends Worker.State> ov, Worker.State oldState, Worker.State newState) -> {
                        if (newState == Worker.State.SUCCEEDED) {
                            JSObject win = (JSObject) webEngine.executeScript("window");
                            win.setMember("app", new JavaApp());
                        }
                    }
            );

            this.getChildren().add(webView);

        } catch (Exception e) {
            System.out.println("Error creating html file.");

        }



    } // end drawMap()

    // JavaScript interface object. All methods in this object are available
    // to javascript file.
    public class JavaApp {
        public JSONArray getLatLonPoints() {
            return RoutePoints.getLatLonPoints();
        }
    } // end JavaApp



} //enf GoogleMap Class
