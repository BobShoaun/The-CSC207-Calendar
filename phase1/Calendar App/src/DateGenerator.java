import java.time.Duration;
import java.util.Date;
import java.util.Iterator;

public class DateGenerator implements Iterable<Date>{

    // end==null means forever? up to implementation
    public DateGenerator(Date start, Duration period, Date end) {

    }

    @Override
    public Iterator<Date> iterator() {
        return null;
    }
}
