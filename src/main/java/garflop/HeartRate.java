package garflop;

import org.jdom.Element;

public class HeartRate implements DataField {
    @Override
    public void set(Point p, Element e) {
        p.setHeartRate(Integer.parseInt(e.getValue()));
    }
}
