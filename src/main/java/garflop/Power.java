package garflop;

import org.jdom.Element;

public class Power implements DataField {
    @Override
    public void set(Point p, Element e) {
        p.setPower(Integer.parseInt(e.getValue()));
    }
}
