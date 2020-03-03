//******************************************************
//  AlertCollection.java    Author: Colin De Vlieghere
//******************************************************

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class AlertCollection {
    // List is only for manually created Alerts.
    private List<Alert> manAlerts;
    private String eventId;
    private Date eventTime;
    private DateGenerator dg;

    /**
     * Creates a new Alert group (possibly repeating)
     * @param e The Event attached to the Alert.
     */
    public AlertCollection(Event e) {
        this.eventId = e.getId();
        this.eventTime = e.getStartDate();
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
     */
    public void addAlert(Date time) {
        manAlerts.add(new Alert(time));
    }

    /**
     * Add a recurring Alert until the Event occurs
     * @param start The start time
     * @param period The time between each alert
     */
    public void addAlert(Date start, Duration period) throws IteratorAlreadySetException {
        if (dg != null)
            throw new IteratorAlreadySetException();
        this.dg = new DateGenerator(start, period, eventTime);
    }

    /**
     * Remove a manually created Alert.
     * @param d The date of the Alert to be removed
     * @return Whether or not the Alert could be removed.
     */
    public boolean removeAlert(Date d) {
        return manAlerts.removeIf(a -> a.getTime().equals(d));
    }

    /**
     * Get all Alerts for the event between a set of times.
     * @param start The start time delimiter
     * @param end   The end time delimiter
     * @return      The list of Alerts between start and end time.
     */
    public List<Alert> getAlerts(Date start, Date end) {
//        throw new NotImplementedException();
        // TODO: complete
        List<Alert> alerts = new LinkedList<>();

        // Add manually created alerts
        for (Alert a : manAlerts) {
            if (a.getTime().compareTo(start) >= 0 && a.getTime().compareTo(end) <= 0)
                alerts.add(a);
        }
        return alerts;
    }

}
