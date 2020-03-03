import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.time.Duration;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

public class CalendarGenerator implements Iterable<GregorianCalendar> {

    private List<GregorianCalendar> ignoreList = new ArrayList<>();

    private GregorianCalendar startTime;
    private List<Duration> periods;
    private GregorianCalendar endTime;

    // end==null means forever? up to implementation
    public CalendarGenerator(GregorianCalendar start, List<Duration> periods, GregorianCalendar end) {
        throw new NotImplementedException();
    }

    public void addPeriod(Duration period) {
        throw new NotImplementedException();
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

    public List<Duration> getPeriods() {
        return periods;
    }

    public GregorianCalendar getEndTime() {
        return endTime;
    }

}
