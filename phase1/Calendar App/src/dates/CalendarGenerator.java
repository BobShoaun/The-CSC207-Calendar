package dates;

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
        startTime.setTime(new Date(Integer.parseInt(information[0])));

        endTime = (new GregorianCalendar());
        endTime.setTime(new Date(Integer.parseInt(information[1])));

        periods = Arrays.stream(information[2].split(" ")).map(s -> Duration.ofSeconds(Long.parseLong(s))).collect(Collectors.toList());
        periods = Arrays.stream(information[3].split(" ")).map(s -> Duration.ofSeconds(Long.parseLong(s))).collect(Collectors.toList());
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
        StringBuilder result = new StringBuilder(startTime.getTime().toString() + "\nEnd: " + endTime.getTime().toString() + "\nRepeat Durations: ");
        for (Duration period : periods) {
            result.append(period.getSeconds()).append(", ");
        }
        return result.toString();
    }

    public void addPeriod(Duration period) {
        periods.add(period);
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

    @Override
    public Iterator<GregorianCalendar> iterator() {
        return new CGI();
    }

    private class CGI implements Iterator<GregorianCalendar>{
        GregorianCalendar currentTime = new GregorianCalendar();

        public CGI() {
            currentTime.setTime(new Date(startTime.getTimeInMillis()));
        }

        public GregorianCalendar add(GregorianCalendar cal, Duration dur){
            GregorianCalendar result = new GregorianCalendar();
            result.setTime(new Date(cal.getTimeInMillis() + dur.getSeconds()));
            return result;
        }

        public List<GregorianCalendar> nextSet(){
            ArrayList<GregorianCalendar> candidates = new ArrayList<>();

            for(Duration period : periods){
                GregorianCalendar newTime = new GregorianCalendar();
                newTime.setTime(new Date(startTime.getTimeInMillis()));
                while (currentTime.after(newTime)){
                    newTime = add(newTime, period);
                }

                candidates.add(newTime);
            }

            candidates.removeIf(candidate -> ignoreList.contains(candidate) || (endTime != null && candidate.after(endTime)));

            return candidates;
        }

        @Override
        public boolean hasNext() {
            return this.nextSet().size() != 0;
        }

        @Override
        public GregorianCalendar next() {
            if(hasNext()){
                List<GregorianCalendar> set = nextSet();
                GregorianCalendar result = set.get(0);

                for(GregorianCalendar time : set){
                    if (result.after(time)){
                        result.setTime(new Date(time.getTimeInMillis()));
                    }
                }

                currentTime.setTime(new Date(result.getTimeInMillis()));
                return result;
            } else {
                throw new IndexOutOfBoundsException("No next date");
            }
        }
    }
}