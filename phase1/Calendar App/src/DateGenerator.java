import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class DateGenerator implements Iterable<Date>{

    private List<Date> ignoreList = new ArrayList<>();

    // end==null means forever? up to implementation
    public DateGenerator(Date start, Duration period, Date end) {

    }

    @Override
    public Iterator<Date> iterator() {
        return null;
    }

    public List<Date> getIgnoreList() {
        return ignoreList;
    }
}
