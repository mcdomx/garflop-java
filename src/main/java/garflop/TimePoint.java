package garflop;

import org.jdom.Element;
import java.time.format.DateTimeFormatter;
import static java.time.temporal.ChronoField.INSTANT_SECONDS;

public class TimePoint implements DataField {
    @Override
    public void set(Point p, Element e) {
    DateTimeFormatter dtf = DateTimeFormatter.ISO_INSTANT;
    Long epochStamp = dtf.parse(e.getValue()).getLong(INSTANT_SECONDS);
    p.setEpochTimeStamp(epochStamp);
    }
}
