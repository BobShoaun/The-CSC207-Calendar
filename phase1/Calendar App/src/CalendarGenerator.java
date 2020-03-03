import java.time.Duration;
import java.util.*;

public class CalendarGenerator implements Iterable<GregorianCalendar> {

    private List<GregorianCalendar> ignoreList = new ArrayList<>();

    private GregorianCalendar startTime;
    private Duration period;
    private GregorianCalendar endTime;

    // end==null means forever? up to implementation
    public CalendarGenerator(GregorianCalendar start, Duration period, GregorianCalendar end) {

    }

    @Override
    public Iterator<GregorianCalendar> iterator() {
        return null;
    }

    public List<GregorianCalendar> getIgnoreList() {
        return ignoreList;
    }

    public GregorianCalendar getStartTime() {
        return startTime;
    }

    public Duration getPeriod() {
        return period;
    }

    public GregorianCalendar getEndTime() {
        return endTime;
    }

}
