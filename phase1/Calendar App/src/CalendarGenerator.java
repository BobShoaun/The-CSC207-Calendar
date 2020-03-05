
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
        this.startTime = start;
        this.periods = periods;
        this.endTime = end;
    }

    /**
     * Create a CalendarGenerator from a String.
     *
     * @param input
     */
    public CalendarGenerator(String input) {

    }

    public void addPeriod(Duration period) {
        //TODO: complete
    }

    /**
     * Outputs all data in this CG into a String.
     *
     * @return String representation of the data
     */
    public String getString() {
        StringBuilder result = new StringBuilder("" + startTime.getTimeInMillis() + "\n" + endTime.getTimeInMillis() + "\n");
        for (Duration period : periods) {
            result.append(period.getSeconds()).append(" ");
        }
        result.append("\n");
        for (GregorianCalendar ignored : ignoreList) {
            result.append(ignored.getTimeInMillis()).append(" ");
        }
        return result.toString();
    }

    @Override
    public String toString() {
        return "";
    }

    @Override
    public Iterator<GregorianCalendar> iterator() {
        return null;
        // TODO: complete
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

    public void setStartTime(GregorianCalendar startTime) {
        this.startTime = startTime;
    }
}
