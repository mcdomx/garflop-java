package garflop;

import org.jdom.Element;

public class Elevation implements DataField {
    @Override
    public void set(Point p, Element e) {
        p.setElevation(Double.parseDouble(e.getValue()));
    }
}
