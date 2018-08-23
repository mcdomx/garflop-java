package garflop;

import org.jdom.Element;

import java.util.IntSummaryStatistics;

public class HeartRate implements DataField {
    @Override
    public void set(Point p, Element e) {
        p.setHeartRate(Integer.parseInt(e.getValue()));
    }

    public static void display (RoutePoints route) {
        IntSummaryStatistics hr = route.hrSummary();
        if (hr.getAverage() != 0) {
            System.out.println("Avg HR (max): " +
                    (int) hr.getAverage() +
                    " (" + hr.getMax() + ")");
        }
    }
}
