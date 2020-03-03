//******************************************************
//  AlertCollection.java    Author: Colin De Vlieghere
//******************************************************

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

public class AlertCollection {
    private List<Alert> manAlerts;
    private String eventId;
    private GregorianCalendar eventTime;
    private DateGenerator dg;
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
     * @param time The time of the Alert to be added.
     * @return Whether or not the Alert could be added.
     */
    public boolean addAlert(Date time) {
        for (Date d : dg) {
            if (d.equals(time))
                return false;
        }
        for (Alert a : manAlerts) {
            if (a.getTime().equals(time))
                return false;
        }
        return manAlerts.add(new Alert(time));
    }

    /**
     * Add a recurring Alert until the Event occurs
     * @param start The start time
     * @param period The time between each alert
     */
    public void addAlert(Date start, Duration period) throws IteratorAlreadySetException {
        if (dg != null)
            throw new IteratorAlreadySetException();
        this.dg = new DateGenerator(start, period, eventTime.getTime());
    }

    /**
     * Remove a single Alert.
     *
     * @param d The date of the Alert to be removed
     * @return Whether or not the Alert could be removed.
     */
    public boolean removeAlert(Date d) {
        return removeManualAlert(d) || removeGeneratedAlert(d);
    }

    public boolean shiftAlerts(int field, int amount) {
        if (dg == null) {
            return false;
        }
        GregorianCalendar newTime = new GregorianCalendar();
        newTime.setTime(dg.getStartTime());
        newTime.add(field, amount);
        this.dg = new DateGenerator(newTime.getTime(), dg.getPeriod(), dg.getEndTime());
        return true;
    }

    /**
     * Replace the current DateGenerator with a new one.
     *
     * @param dg The new DateGenerator
     */
    public void setDateGenerator(DateGenerator dg) {
        this.dg = dg;
    }

    /**
     * Get the current DateGenerator.
     *
     * @return The current DateGenerator
     */
    public DateGenerator getDateGenerator() {
        return dg;
    }

    private boolean removeGeneratedAlert(Date d) {
        for (Date ignored : dg.getIgnoreList()) {
            if (d.equals(ignored))
                return false;
        }
        return dg.getIgnoreList().add(d);
    }

    private boolean removeManualAlert(Date d) {
        return manAlerts.removeIf(a -> a.getTime().equals(d));
    }

    /**
     * Get all Alerts for the event between a set of times.
     * @param start The start time delimiter
     * @param end   The end time delimiter
     * @return      The list of Alerts between start and end time.
     */
    public List<Alert> getAlerts(Date start, Date end) {
        List<Alert> alerts = getManualAlerts(start, end);
        alerts.addAll(getGeneratedAlerts(start, end));
        return alerts;
    }

    private List<Alert> getManualAlerts(Date start, Date end) {
        List<Alert> alerts = new LinkedList<>();
        for (Alert a : manAlerts) {
            if (a.getTime().compareTo(start) >= 0 && a.getTime().compareTo(end) <= 0)
                alerts.add(a);
        }
        return alerts;
    }

    private List<Alert> getGeneratedAlerts(Date start, Date end) {
        List<Alert> alerts = new LinkedList<>();
        for (Date d : dg) {
            if (d.compareTo(start) >= 0 && d.compareTo(end) <= 0)
                alerts.add(new Alert(d));
        }
        return alerts;
    }

}
