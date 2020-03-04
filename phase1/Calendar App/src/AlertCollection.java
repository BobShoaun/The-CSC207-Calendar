import java.time.Duration;
import java.util.*;

/**
 * A collection/group of one alert.
 *
 * @author colin
 */
public class AlertCollection extends TextFileSerializer implements Observer {
    private List<Alert> manAlerts;
    private String eventId;
    private GregorianCalendar eventTime;
    private CalendarGenerator cg;

    /**
     * Creates a new Alert group (possibly repeating)
     *
     * @param e The Event attached to the Alert.
     */
    public AlertCollection(Event e) {
        this.eventId = e.getId();
        this.eventTime = new GregorianCalendar();
        this.eventTime.setTime(e.getStartDate());
        manAlerts = new ArrayList<>();
    }

    /**
     * Get the ID of the event associated with this AlertCollection
     *
     * @return The ID of the event
     */
    public String getEventId() {
        return eventId;
    }

    public List<Alert> getManAlerts() {
        return manAlerts;
    }

    /**
     * Add an alert manually by setting the time.
     *
     * @param time The time of the Alert to be added.
     * @return Whether or not the Alert could be added.
     */
    public boolean addAlert(GregorianCalendar time) {
        for (GregorianCalendar c : cg) {
            if (c.equals(time))
                return false;
        }
        for (Alert a : manAlerts) {
            if (a.getTime().equals(time.getTime()))
                return false;
        }
        manAlerts.add(new Alert(time));
        manAlerts.sort(new AlertComparator());
        return true;
    }

    /**
     * Add a recurring Alert until the Event occurs
     *
     * @param start  The start time
     * @param period The time between each alert
     */
    public boolean addAlert(GregorianCalendar start, Duration period) {
        if (this.cg == null)
            return false;
        else if (this.cg.getPeriods().contains(period))
            return false;
        this.cg.addPeriod(period);
        return true;
    }

    /**
     * Remove a single Alert.
     *
     * @param d The date of the Alert to be removed
     * @return Whether or not the Alert could be removed.
     */
    public boolean removeAlert(GregorianCalendar d) {
        return removeManualAlert(d) || removeGeneratedAlert(d);
    }

    /**
     * Remove an automatically generated Alert.
     *
     * @param d The time of the alert being removed.
     * @return Whether the alert could be removed
     */
    public boolean removeGeneratedAlert(GregorianCalendar d) {
        for (GregorianCalendar ignored : cg.getIgnoreList()) {
            if (d.equals(ignored))
                return false;
        }
        return cg.getIgnoreList().add(d);
    }

    /**
     * Remove a manually created Alert.
     *
     * @param d The time of the alert being removed.
     * @return Whether the alert could be removed
     */
    public boolean removeManualAlert(GregorianCalendar d) {
        return manAlerts.removeIf(a -> a.getTime().equals(d.getTime()));
    }

    /**
     * Shift the time of all Alerts in this AlertCollection.
     *
     * @param newEventTime The new time of the Event
     */
    public void shiftAlerts(GregorianCalendar newEventTime) {
        if (cg == null) {
            throw new IllegalStateException();
        }
        GregorianCalendar newStart = new GregorianCalendar();
        long diff = newEventTime.getTimeInMillis() - eventTime.getTimeInMillis();
        long newMillis = cg.getStartTime().getTimeInMillis() + diff;
        newStart.setTimeInMillis(newMillis);
        this.cg.setStartTime(newStart);
    }

    /**
     * Replace the current CalendarGenerator with a new one.
     *
     * @param cg The new CalendarGenerator
     */
    public void setCalendarGenerator(CalendarGenerator cg) {
        this.cg = cg;
    }

    /**
     * Get the current CalendarGenerator.
     *
     * @return The current CalendarGenerator
     */
    public CalendarGenerator getCalendarGenerator() {
        return cg;
    }

    /**
     * Get all Alerts for the event between a set of times.
     *
     * @param start The start time delimiter
     * @param end   The end time delimiter
     * @return The list of Alerts between start and end time.
     */
    public List<Alert> getAlerts(GregorianCalendar start, GregorianCalendar end) {
        List<Alert> alerts = getManualAlerts(start, end);
        alerts.addAll(getGeneratedAlerts(start, end));
        alerts.sort(new AlertComparator());
        return alerts;
    }

    /**
     * Get manually created Alerts for the event between a set of times.
     *
     * @param start The start time delimiter
     * @param end   The end time delimiter
     * @return The list of Alerts between start and end time.
     */
    public List<Alert> getManualAlerts(GregorianCalendar start, GregorianCalendar end) {
        List<Alert> alerts = new LinkedList<>();
        for (Alert a : manAlerts) {
            if (a.getTime().compareTo(start.getTime()) >= 0 && a.getTime().compareTo(end.getTime()) <= 0)
                alerts.add(a);
        }
        return alerts;
    }

    /**
     * Get automatically generated Alerts for the event between a set of times.
     *
     * @param start The start time delimiter
     * @param end   The end time delimiter
     * @return The list of Alerts between start and end time.
     */
    public List<Alert> getGeneratedAlerts(GregorianCalendar start, GregorianCalendar end) {
        List<Alert> alerts = new LinkedList<>();
        for (GregorianCalendar d : cg) {
            if (d.compareTo(start) >= 0 && d.compareTo(end) <= 0)
                alerts.add(new Alert(d));
        }
        return alerts;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof GregorianCalendar)
            shiftAlerts((GregorianCalendar) arg);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public String toString() {
        String result = eventId + "\n" + eventTime.getTimeInMillis() + "\n";
        for (Alert a : getManAlerts()) {
            result += a.toString() + " ";
        }
        result += "\n" + cg.toString();
        return result;
    }

    /**
     * Load the data into this AlertCollection.
     *
     * @param filePath The user's directory, without the trailing /
     * @param eventId  The ID of the event for which the Alerts are being loaded
     */
    public void load(String filePath, String eventId) {
        List<String> strings = loadStringsFromFile(filePath + "/" + eventId + ".txt");

        this.eventId = strings.get(0).trim();

        String[] manTimes = strings.get(1).trim().split("\\s+");
        for (String timeStr : manTimes) {
            manAlerts.add(new Alert(timeStr));
        }

        StringBuilder cgStr = new StringBuilder();
        for (int i = 2; i < strings.size(); i++) {
            cgStr.append(strings.get(i));
        }
        this.cg = new CalendarGenerator(cgStr.toString());

    }

    /**
     * Save this AlertCollection's data into a text file.
     *
     * @param filePath The user's directory.
     */
    public void save(String filePath) {
        filePath = filePath + "/" + eventId + ".txt";
        List<String> contents = Arrays.asList(toString().split("\\s+"));
        saveToFile(filePath, contents);
    }

}
