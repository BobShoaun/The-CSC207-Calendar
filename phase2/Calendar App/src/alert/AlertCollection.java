package alert;

import dates.CalendarGenerator;
import event.Event;
import event.IDManager;
import exceptions.PeriodAlreadyExistsException;
import user.DataSaver;

import java.time.Duration;
import java.util.*;

/**
 * A collection/group of alerts for one Event.
 * Has a list of manually created alerts and possibly
 * a CalendarGenerator for repeating Alerts.
 *
 * @author Colin De Vlieghere
 */
public class AlertCollection implements Observer {

    private List<Alert> manAlerts;
    private String eventId;
    private GregorianCalendar eventTime;
    private CalendarGenerator calGen;
    private final DataSaver saver;

    /**
     * Creates a new Alert collection
     *
     * @param e The Event attached to the Alert.
     */
    public AlertCollection(Event e, DataSaver saver) {
        this.eventId = e.getId();
        this.eventTime = new GregorianCalendar();
        this.eventTime.setTime(e.getStartDate().getTime());
        manAlerts = new ArrayList<>();
        this.saver = saver;
    }

    /**
     * Create a new AlertCollection
     *
     * @param eventId The ID of the event
     * @param saver   The Datasaver representing the user filepath
     */
    public AlertCollection(String eventId, DataSaver saver) {
        this.eventId = eventId;
        this.saver = saver;
        manAlerts = new ArrayList<>();
    }

    /**
     * Set the time of the event this AC is associated with.
     *
     * @param eventTime The time of the Event
     */
    public void setEventTime(GregorianCalendar eventTime) {
        this.eventTime = eventTime;
    }

    /**
     * Set the CalendarGenerator for generated Alerts.
     *
     * @param calGen CalendarGenerator to be set
     */
    public void setCalGen(CalendarGenerator calGen) {
        this.calGen = calGen;
    }

    /**
     * Get the ID of the event associated with this AlertCollection
     *
     * @return The ID of the event
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * Get the list of manual alerts
     *
     * @return All manually created alerts in this AlertCollection
     */
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
        if (calGen != null) {
            for (GregorianCalendar c : calGen) {
                if (c.equals(time))
                    return false;
            }
        }
        for (Alert a : manAlerts) {
            if (a.getTime().getTime().equals(time.getTime()))
                return false;
        }
        manAlerts.add(new Alert(eventId, time));
        manAlerts.sort(new AlertComparator());
        saver.saveAlertCollection(this);
        return true;
    }

    /**
     * Add a recurring Alert until the Event occurs
     *
     * @param start  The start time
     * @param period The time between each alert
     */
    public void addAlert(GregorianCalendar start, Duration period) throws PeriodAlreadyExistsException {
        if (this.calGen == null) {
            ArrayList<Duration> d = new ArrayList<>();
            d.add(period);
            this.calGen = new CalendarGenerator(start, d, eventTime);
        } else if (this.calGen.getPeriods().contains(period)) {
            throw new PeriodAlreadyExistsException();
        } else
            this.calGen.addPeriod(period);
        saver.saveAlertCollection(this);
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
     * Clear all manually created alerts.
     */
    public void removeAllManualAlerts() {
        manAlerts = new ArrayList<>();
        saver.saveAlertCollection(this);
    }

    /**
     * Clear all generated alerts.
     */
    public void removeAllGeneratedAlerts() {
        calGen = null;
        saver.saveAlertCollection(this);
    }

    /**
     * Remove any alerts that occur in the past.
     */
    public void removeOldAlerts() {
        GregorianCalendar start = new GregorianCalendar();
        start.setTimeInMillis(0);
        for (Alert a : getAlerts(start, new GregorianCalendar())) {
            removeAlert(a.getTime());
        }
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
        calGen.addIgnore(d);
        saver.saveAlertCollection(this);
        return true;
    }

    /**
     * Remove a manually created Alert.
     *
     * @param d The time of the alert being removed.
     * @return Whether the alert could be removed
     */
    public boolean removeManualAlert(GregorianCalendar d) {
        boolean result = manAlerts.removeIf(a -> a.getTime().getTime().equals(d.getTime()));
        saver.saveAlertCollection(this);
        return result;
    }

    /**
     * Shift the time of all Alerts in this AlertCollection.
     *
     * @param newEventTime The new time of the Event
     */
    private void shiftAlerts(GregorianCalendar newEventTime) {
        if (calGen == null) {
            throw new IllegalStateException();
        }
        GregorianCalendar newStart = new GregorianCalendar();
        long diff = newEventTime.getTimeInMillis() - eventTime.getTimeInMillis();
        long newMillis = calGen.getStartTime().getTimeInMillis() + diff;
        newStart.setTimeInMillis(newMillis);
        this.calGen.setStartTime(newStart);
        saver.saveAlertCollection(this);
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
     * Get a single alert with a given date.
     *
     * @param time Date of the alert
     * @return The alert
     */
    public Alert getAlert(GregorianCalendar time) {
        return getAlerts(time, time).get(0);
    }

    private List<Alert> getManualAlerts(GregorianCalendar start, GregorianCalendar end) {
        List<Alert> alerts = new ArrayList<>();
        for (Alert a : manAlerts) {
            if (a.getTime().getTime().compareTo(start.getTime()) >= 0
                    && a.getTime().getTime().compareTo(end.getTime()) <= 0)
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
        List<Alert> alerts = new ArrayList<>();
        if (calGen == null)
            return alerts;
        for (GregorianCalendar d : calGen) {
            if (d.compareTo(start) >= 0 && d.compareTo(end) <= 0)
                alerts.add(new Alert(eventId, d));
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
    public void update(Observable o, Object arg) { // TODO: make sure this is working
        if (arg instanceof GregorianCalendar) {
            shiftAlerts((GregorianCalendar) arg);
            GregorianCalendar eventTime = IDManager.parseEventId(eventId);
            long diff = ((GregorianCalendar) arg).getTimeInMillis();
            long newMillis = eventTime.getTimeInMillis() + diff;
            eventTime.setTimeInMillis(newMillis);
            eventId = IDManager.generateEventId(eventId.split("$")[1], eventTime);
        } else
            throw new IllegalArgumentException();
    }

    /**
     * Get a String representation of data in this AlertCollection.
     *
     * @return String representation of all the data in this AC, including the dates.CalendarGenerator.
     */
    public String getString() {
        StringBuilder result = new StringBuilder(eventId + "\n" + eventTime.getTimeInMillis() + "\n");
        for (Alert a : getManAlerts()) {
            result.append(a.getString()).append(" ");
        }
        if (calGen != null)
            result.append("\n").append(calGen.getString());
        return result.toString();
    }

    /**
     * Get a String representing this AC.
     *
     * @return A user-friendly string representation
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("Alert at " + eventTime.getTime().toString() + ".\n");
        result.append("     ===== MANUALLY CREATED ALERTS =====\n");
        if (manAlerts.size() == 0)
            result.append("None.\n");
        else {
            for (Alert a : manAlerts) {
                result.append(a.toString()).append("\n");
            }
        }
        result.append("        ===== REPEATING ALERTS =====\n");
        if (calGen != null)
            result.append(calGen.toString());
        else
            result.append("None.");
        return result.toString();
    }


}
