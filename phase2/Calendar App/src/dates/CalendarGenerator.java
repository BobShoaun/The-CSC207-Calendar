package dates;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The CalendarGenerator class
 */
public class CalendarGenerator implements Iterable<GregorianCalendar> {

    private List<GregorianCalendar> ignoreList = new ArrayList<>();
    private GregorianCalendar startTime;
    private List<Duration> periods;
    private GregorianCalendar endTime;

    // end==null means forever
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
        startTime.setTimeInMillis(Long.parseLong(information[0].trim()));

        endTime = (new GregorianCalendar());
        endTime.setTimeInMillis(Long.parseLong(information[1].trim()));

        periods = Arrays.stream(information[2].split(" ")).map(s -> Duration.ofSeconds(Long.parseLong(s))).collect(Collectors.toList());
        periods = Arrays.stream(information[3].split(" ")).map(s -> Duration.ofSeconds(Long.parseLong(s))).collect(Collectors.toList());
    }

    /**
     * Outputs all data in this CG into a String.
     * @return A savable representation of the data
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

    /**
     * Summarizes this object into a string
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
     * @param period the period being added
     */
    public void addPeriod(Duration period) {
        periods.add(period);
    }

    /**
     * Adds a time to the times being ignored
     * @param newIgnoreTime the time to ignore
     */
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

    /**
     * creates a new iterator
     * @return the iterator
     */
    @Override
    public Iterator<GregorianCalendar> iterator() {
        return new CGI();
    }

    /**
     * The CGI class
     */
    private class CGI implements Iterator<GregorianCalendar>{
        GregorianCalendar currentTime = new GregorianCalendar();

        /**
         * Create a CGI
         */
        public CGI() {
            currentTime.setTimeInMillis(startTime.getTimeInMillis());
        }

        private List<GregorianCalendar> nextSet(){
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
            if(hasNext()){
                List<GregorianCalendar> set = nextSet();
                GregorianCalendar result = set.get(0);

                for(GregorianCalendar time : set){
                    if (result.after(time)){
                        result.setTimeInMillis(time.getTimeInMillis());
                    }
                }

                currentTime.setTimeInMillis(result.getTimeInMillis()+1);
                return result;
            } else {
                throw new IndexOutOfBoundsException("No next date");
            }
        }
    }
}