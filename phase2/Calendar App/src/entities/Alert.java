package entities;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 * An alert.Alert, representing a notification for an alert.Event.
 *
 * @author colin
 */
public class Alert {

    private GregorianCalendar time;
    private String eventId;

    /**
     * Create an Alert
     *
     * @param time    The time at which the notification goes off
     * @param eventId The ID of the event
     */
    public Alert(String eventId, GregorianCalendar time) {
        this.eventId = eventId;
        this.time = time;
    }

    /**
     * Create an Alert from a String representation.
     *
     * @param string  the toString() output, representing time
     * @param eventId The ID of the event
     */
    public Alert(String eventId, String string) {
        this.eventId = eventId;
        this.time = new GregorianCalendar();
        this.time.setTimeInMillis(Long.parseLong(string));
    }

    /**
     * Get the alert's time.
     *
     * @return The time of the alert.Alert.
     */
    public GregorianCalendar getTime() {
        return time;
    }

    /**
     * Set the time of this alert.Alert.
     *
     * @param time The new time to be set
     */
    public void setTime(Date time) {
        this.time.setTime(time);
    }

    /**
     * Get a string of data in of this alert.Alert.
     *
     * @return The time in milliseconds for the alert.Alert
     */
    protected String getString() {
        return "" + time.getTimeInMillis();
    }

    /**
     * Get a string representation of this alert.
     *
     * @return String representing the time of the alert
     */
    @Override
    public String toString() {
        return getEventId();
        //return "" + time.getTime().toString();
    }

    public String getEventId() {
        return eventId;
    }
}
