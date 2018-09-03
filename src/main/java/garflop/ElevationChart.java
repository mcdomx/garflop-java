package garflop;

import javafx.scene.chart.*;
import javafx.util.Pair;
import javafx.util.StringConverter;

import java.text.DecimalFormat;
import java.util.List;

import static java.lang.StrictMath.round;


public class ElevationChart {
    static final int maxPoints = 2000;

//    AreaChart<Number, Number> areaChart = new AreaChart( xAxis, yAxis);
    AreaChart<Number, Number> areaChart;

    public ElevationChart(double distance) {
        //AREA CHART
        int numPoints = RoutePoints.getPoints().size() < maxPoints? RoutePoints.getPoints().size():maxPoints;
        ValueAxis xAxis = new NumberAxis(0, numPoints , 100);
        xAxis.setMinorTickCount(0);

        xAxis.setTickLabelFormatter(new StringConverter() {
            @Override
            public String toString(Object o) {
                DecimalFormat df = new DecimalFormat("#.#");
                String rv = df.format((double) o / maxPoints * distance);
                return rv;
            }

            @Override
            public Object fromString(String string) {
                return null;
            }
        });

        Axis yAxis = new NumberAxis();
        areaChart = new AreaChart( xAxis, yAxis);

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
