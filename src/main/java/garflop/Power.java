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
}
