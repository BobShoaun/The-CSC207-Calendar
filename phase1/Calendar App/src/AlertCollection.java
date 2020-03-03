//******************************************************
//  AlertCollection.java    Author: Colin De Vlieghere
//******************************************************

import java.time.Duration;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

public class AlertCollection {
    private List<Alert> manAlerts;
    private String eventId;
    private GregorianCalendar eventTime;
    private CalendarGenerator cg;
    /**
     * Creates a new Alert group (possibly repeating)
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
     * @return The ID of the event
     */
    public String getEventId() {
        return eventId;
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
        return manAlerts.add(new Alert(time));
    }

    /**
     * Add a recurring Alert until the Event occurs
     *
     * @param start  The start time
     * @param period The time between each alert
     */
    public void addAlert(GregorianCalendar start, Duration period) throws IteratorAlreadySetException {
        if (cg != null)
            throw new IteratorAlreadySetException();
        this.cg = new CalendarGenerator(start, period, eventTime);
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

    public boolean shiftAlerts(int field, int amount) {
        if (cg == null) {
            return false;
        }
        GregorianCalendar newTime = (GregorianCalendar) cg.getStartTime().clone();
        newTime.add(field, amount);
        this.cg = new CalendarGenerator(newTime, cg.getPeriod(), cg.getEndTime());
        return true;
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

    private boolean removeGeneratedAlert(GregorianCalendar d) {
        for (GregorianCalendar ignored : cg.getIgnoreList()) {
            if (d.equals(ignored))
                return false;
        }
        return cg.getIgnoreList().add(d);
    }

    private boolean removeManualAlert(GregorianCalendar d) {
        return manAlerts.removeIf(a -> a.getTime().equals(d.getTime()));
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
        return alerts;
    }

    private List<Alert> getManualAlerts(GregorianCalendar start, GregorianCalendar end) {
        List<Alert> alerts = new LinkedList<>();
        for (Alert a : manAlerts) {
            if (a.getTime().compareTo(start.getTime()) >= 0 && a.getTime().compareTo(end.getTime()) <= 0)
                alerts.add(a);
        }
        return alerts;
    }

    private List<Alert> getGeneratedAlerts(GregorianCalendar start, GregorianCalendar end) {
        List<Alert> alerts = new LinkedList<>();
        for (GregorianCalendar d : cg) {
            if (d.compareTo(start) >= 0 && d.compareTo(end) <= 0)
                alerts.add(new Alert(d));
        }
        return alerts;
    }

}
