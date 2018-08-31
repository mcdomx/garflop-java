package garflop;

import javafx.util.Pair;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.util.List;

public class ElevationChart {
    static final int maxPoints = 2000;
    AreaChart<Number, Number> areaChart = new AreaChart(
            new NumberAxis(0,
                    RoutePoints.getPoints().size() < maxPoints? RoutePoints.getPoints().size():maxPoints ,
                    100),
            new NumberAxis());

    public ElevationChart() {
        //AREA CHART
        this.areaChart.setTitle("Elevation Profile");

        XYChart.Series elevationSeries = new XYChart.Series();
        elevationSeries.setName("elevation");

        List<Pair<Double, Double>> e_pts = RoutePoints.getElevationPoints(maxPoints);

        int numPts = e_pts.size()-1;
        for ( int i = 0 ; i <= numPts ; i++ ) {
            elevationSeries.getData()
                    .add(new XYChart.Data(i , e_pts.get(i).getValue()));
        }

        this.areaChart.getData().add(elevationSeries);

    }

    public AreaChart<Number, Number> getAreaChart() {
        return areaChart;
    }
}
