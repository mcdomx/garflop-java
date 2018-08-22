package garflop;

import org.jdom.*;

public class Cadence implements DataField {
    @Override
    public void set(Point p, Element e) {
        p.setCadence(Integer.parseInt(e.getValue()));
    }

}
