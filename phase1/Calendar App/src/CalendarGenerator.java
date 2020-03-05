import java.time.Duration;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Date;
import java.util.Arrays;
import java.util.stream.Collectors;

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
     * @param input
     */
    public CalendarGenerator(String input) {
        String[] information = input.split("\n");
        startTime = new GregorianCalendar();
        startTime.setTime(new Date(Integer.parseInt(information[0])));

        endTime = (new GregorianCalendar());
        endTime.setTime(new Date(Integer.parseInt(information[1])));

        periods = Arrays.stream(information[2].split(" ")).map(s -> Duration.ofSeconds(Long.parseLong(s))).collect(Collectors.toList());
        periods = Arrays.stream(information[3].split(" ")).map(s -> Duration.ofSeconds(Long.parseLong(s))).collect(Collectors.toList());
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
        StringBuilder result = new StringBuilder(startTime.getTimeInMillis() + "\n" + endTime.getTimeInMillis() + "\n");
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
        return "Start: " + startTime.getTime().toString() + "\nEnd: " + endTime.getTime().toString() + "\nRepeat Durations: ";
    }

    @Override
    public Iterator<GregorianCalendar> iterator() {
        return null;
        // TODO: complete
    }

    public void addIgnore(GregorianCalendar newIgnoreTime){
        ignoreList.add(newIgnoreTime);
    }

    public List<GregorianCalendar> getIgnoreList() {
        return ignoreList;
    }

    public List<Duration> getPeriods() {
        return periods;
    }

    public GregorianCalendar getStartTime() {
        return startTime;
    }

    public GregorianCalendar getEndTime() {
        return endTime;
    }

    public void setEndTime(GregorianCalendar endTime) {
        this.endTime = endTime;
    }

    public void setStartTime(GregorianCalendar startTime) {
        this.startTime = startTime;
    }
}
