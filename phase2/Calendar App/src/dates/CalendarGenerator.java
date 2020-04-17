package dates;

import java.time.Duration;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

/**
 * The CalendarGenerator class
 */
public class CalendarGenerator implements Iterable<GregorianCalendar> {

    private List<GregorianCalendar> ignoreList = new ArrayList<>();
    private GregorianCalendar startTime;
    private List<Duration> periods;
    private GregorianCalendar endTime;

    /**
     * Initialize the CalendarGenerator.
     * end==null means it goes on forever
     *
     * @param start   Start time
     * @param periods The duration of repetition
     * @param end     End time
     */
    public CalendarGenerator(GregorianCalendar start, List<Duration> periods, GregorianCalendar end) {
        this.startTime = start;
        this.periods = periods;
        this.endTime = end;
    }

    /**
     * Initialize the CalendarGenerator.
     * end==null means it goes on forever
     *
     * @param start      Start time
     * @param periods    The duration of repetition
     * @param end        End time
     * @param ignoreList List of ignored times
     */
    public CalendarGenerator(GregorianCalendar start, List<Duration> periods, GregorianCalendar end, List<GregorianCalendar> ignoreList) {
        this.startTime = start;
        this.periods = periods;
        this.endTime = end;
        this.ignoreList = ignoreList;
    }


    /**
     * Outputs all data in this CG into a String.
     *
     * @return A savable representation of the data
     */
    public String getString() {
        StringBuilder result = new StringBuilder(startTime.getTimeInMillis() + "\n"
                + endTime.getTimeInMillis() + "\n");
        for (Duration period : periods) {
            result.append(period.getSeconds()).append(" ");
        }
        result.append("\n");
        for (GregorianCalendar ignored : ignoreList) {
            result.append(ignored.getTimeInMillis()).append(" ");
        }
        return result.toString();
    }

    /**
     * Summarizes this object into a string
     *
     * @return String representation of the data
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(startTime.getTime().toString() + "\nEnd: " + endTime.getTime().toString() + "\nRepeat Durations: ");
        for (Duration period : periods) {
            result.append(period.getSeconds()).append(", ");
        }
        return result.toString();
    }

    /**
     * Adds a period to the repeat periods
     *
     * @param period the period being added
     */
    public void addPeriod(Duration period) {
        periods.add(period);
    }

    /**
     * Adds a time to the times being ignored
     *
     * @param newIgnoreTime the time to ignore
     */
    public void addIgnore(GregorianCalendar newIgnoreTime) {
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

    /**
     * creates a new iterator
     *
     * @return the iterator
     */
    @Override
    public Iterator<GregorianCalendar> iterator() {
        return new CGI();
    }

    /**
     * The CGI class
     */
    private class CGI implements Iterator<GregorianCalendar> {
        GregorianCalendar currentTime = new GregorianCalendar();

        /**
         * Create a CGI
         */
        public CGI() {
            currentTime.setTimeInMillis(startTime.getTimeInMillis());
        }

        private List<GregorianCalendar> nextSet() {
            List<GregorianCalendar> candidates = new ArrayList<>();

            for (Duration period : periods) {
                GregorianCalendar newTime = new GregorianCalendar();
                newTime.setTimeInMillis(startTime.getTimeInMillis());
                while (currentTime.getTimeInMillis() > newTime.getTimeInMillis() || ignoreList.contains(newTime)) {
                    newTime.setTimeInMillis(newTime.getTimeInMillis() + period.toMillis());
                }

                if (endTime == null || endTime.getTimeInMillis() >= newTime.getTimeInMillis()) {
                    candidates.add(newTime);
                }
            }

            return candidates;
        }

        @Override
        public boolean hasNext() {
            return nextSet().size() != 0;
        }

        @Override
        public GregorianCalendar next() {
            if (hasNext()) {
                List<GregorianCalendar> set = nextSet();
                GregorianCalendar result = set.get(0);

                for (GregorianCalendar time : set) {
                    if (result.after(time)) {
                        result.setTimeInMillis(time.getTimeInMillis());
                    }
                }

                currentTime.setTimeInMillis(result.getTimeInMillis() + 1);
                return result;
            } else {
                throw new IndexOutOfBoundsException("No next date");
            }
        }
    }
}