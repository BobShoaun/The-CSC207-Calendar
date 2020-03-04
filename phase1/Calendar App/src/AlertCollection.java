import exceptions.PeriodAlreadyExistsException;
import java.time.Duration;
import java.util.*;

/**
 * A collection/group of one alert. Has a list of manually created alerts and possibly
 * a CalendarGenerator for repeating Alerts.
 *
 * @author colin
 */
public class AlertCollection extends TextFileSerializer implements Observer {
    private List<Alert> manAlerts;
    private String eventId;
    private GregorianCalendar eventTime;
    private CalendarGenerator calGen;

    /**
     * Creates a new Alert group (possibly repeating)
     *
     * @param e The Event attached to the Alert.
     */
    public AlertCollection(Event e) {
        this.eventId = e.getId();
        this.eventTime = new GregorianCalendar();
        this.eventTime.setTime(e.getStartDate().getTime());
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
        for (GregorianCalendar c : calGen) {
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
     *  @param start  The start time
     * @param period The time between each alert
     */
    public void addAlert(GregorianCalendar start, Duration period) throws PeriodAlreadyExistsException {
        if (this.calGen == null) {
            ArrayList<Duration> d = new ArrayList<>();
            d.add(period);
            this.calGen = new CalendarGenerator(start, d, eventTime);
        }
        else if (this.calGen.getPeriods().contains(period)) {
            throw new PeriodAlreadyExistsException();
        }
        else
            this.calGen.addPeriod(period);
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
        for (GregorianCalendar ignored : calGen.getIgnoreList()) {
            if (d.equals(ignored))
                return false;
        }
        return calGen.getIgnoreList().add(d);
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
        if (calGen == null) {
            throw new IllegalStateException();
        }
        GregorianCalendar newStart = new GregorianCalendar();
        long diff = newEventTime.getTimeInMillis() - eventTime.getTimeInMillis();
        long newMillis = calGen.getStartTime().getTimeInMillis() + diff;
        newStart.setTimeInMillis(newMillis);
        this.calGen.setStartTime(newStart);
    }

    /**
     * Replace the current CalendarGenerator with a new one.
     *
     * @param cg The new CalendarGenerator
     */
    public void setCalendarGenerator(CalendarGenerator cg) {
        this.calGen = cg;
    }

    /**
     * Get the current CalendarGenerator.
     *
     * @return The current CalendarGenerator
     */
    public CalendarGenerator getCalendarGenerator() {
        return calGen;
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
        for (GregorianCalendar d : calGen) {
            if (d.compareTo(start) >= 0 && d.compareTo(end) <= 0)
                alerts.add(new Alert(d));
        }
        return alerts;
    }

    /**
     * Update this AC when its associated event changes date.
     *
     * @param o   The event being updated.
     * @param arg The new time for the event.
     */
    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof GregorianCalendar)
            shiftAlerts((GregorianCalendar) arg);
        else
            throw new IllegalArgumentException();
    }

    /**
     * Get a String representation of data in this AlertCollection.
     *
     * @return String representation of all the data in this AC, including the CalendarGenerator.
     */
    public String getString() {
        String result = eventId + "\n" + eventTime.getTimeInMillis() + "\n";
        for (Alert a : getManAlerts()) {
            result += a.getString() + " ";
        }
        result += "\n" + calGen.getString();
        return result;
    }

    @Override
    public String toString() {
        String result = "Alert for EventID " + eventId
                + ", which occurs at " + eventTime.getTime().toString() + ".\n";
        result += "===== MANUALLY CREATED ALERTS =====\n";
        for (Alert a : manAlerts) {
            result += a.toString() + "\n";
        }
        result += "===== REPEATING ALERTS =====\n";
        result += calGen.toString();
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
        this.calGen = new CalendarGenerator(cgStr.toString());

    }

    /**
     * Save this AlertCollection's data into a text file.
     *
     * @param filePath The user's directory.
     */
    public void save(String filePath) {
        filePath = filePath + "/" + eventId + ".txt";
        List<String> contents = Arrays.asList(getString().split("\\s+"));
        saveToFile(filePath, contents);
    }

}
