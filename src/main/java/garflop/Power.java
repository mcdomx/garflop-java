package garflop;

import org.jdom.Element;

import java.util.IntSummaryStatistics;

public class Power implements DataField {
    @Override
    public void set(Point p, Element e) {
        p.setPower(Integer.parseInt(e.getValue()));
    }

    public static void display(RoutePoints route) {
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
    }

    public static String getAvgPower() {
        String rv = null;
        RoutePoints route = RoutePoints.getCurrentRoute();
        IntSummaryStatistics pwr = route.powerSummary();
        if (pwr.getAverage() != 0)
            rv = Integer.toString((int)pwr.getAverage());

        return rv;
    }

    public static String getAvgPowerNonZero() {
        String rv = null;
        RoutePoints route = RoutePoints.getCurrentRoute();
        IntSummaryStatistics pwr = route.powerSummary_nonZero();
        if (pwr.getAverage() != 0)
            rv = Integer.toString((int)pwr.getAverage());

        return rv;
    }

    public static String getMaxPower() {
        String rv = null;
        RoutePoints route = RoutePoints.getCurrentRoute();
        IntSummaryStatistics pwr = route.powerSummary();
        if (pwr.getAverage() != 0)
            rv = Integer.toString((int)pwr.getMax());

        return rv;
    }
}
