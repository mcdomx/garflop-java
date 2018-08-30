package garflop;

import org.jdom.Element;

public class Elevation implements DataField {
    @Override
    public void set(Point p, Element e) {
        p.setElevation(Double.parseDouble(e.getValue()));
    }

    public static void display(RoutePoints route) {
        System.out.println("Total Climb (Descent): " + (int) route.getClimb() + " (" + (int) route.getDescent() + ")");
    }

    public static String getClimb () {
        RoutePoints route = RoutePoints.getCurrentRoute();
        return Integer.toString((int) route.getClimb());
    }

    public static String getDescent () {
        RoutePoints route = RoutePoints.getCurrentRoute();
        return Integer.toString((int) route.getDescent());
    }
}
