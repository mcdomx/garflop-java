package garflop;

import org.jdom.*;

import java.util.IntSummaryStatistics;

public class Cadence implements DataField {
    @Override
    public void set(Point p, Element e) {
        p.setCadence(Integer.parseInt(e.getValue()));
    }

    public static void display(RoutePoints route) {
        IntSummaryStatistics cad = route.cadenceSummary();
        if (cad.getAverage() != 0 ) {
            System.out.println("Avg Cadence (max): " +
                    (int) cad.getAverage() +
                    " (" + cad.getMax() + ")");
        }
    }

    public static String getAvgCadence() {
        String rv = null;
        RoutePoints route = RoutePoints.getCurrentRoute();
        IntSummaryStatistics cad = route.cadenceSummary();
        if (cad.getAverage() != 0 )
            rv = Integer.toString((int) cad.getAverage());

        return rv;
    }

    public static String getMaxCadence() {
        String rv = null;
        RoutePoints route = RoutePoints.getCurrentRoute();
        IntSummaryStatistics cad = route.cadenceSummary();
        if (cad.getAverage() != 0 )
            rv = Integer.toString((int) cad.getMax());

        return rv;
    }


}
