package alert;

import event.IDManager;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 * An Alert, representing a notification for an Event.
 *
 * @author Colin De Vlieghere
 */
public class Alert {

    private final GregorianCalendar time;
    private final String eventId;

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
     * @return The time of the Alert.
     */
    public GregorianCalendar getTime() {
        return time;
    }

    /**
     * Set the time of this Alert.
     *
     * @param time The new time to be set
     */
    public void setTime(Date time) {
        this.time.setTime(time);
    }

    /**
     * Get a string of data in of this alert.Alert.
     *
     * @return The time in milliseconds for the Alert
     */
    protected String getString() {
        return "" + time.getTimeInMillis();
    }

    /**
     * Get a string representation of this alert, formatted like an Event ID.
     *
     * @return String representing the time of the alert
     */
    @Override
    public String toString() {
        String s = getEventId();
        String eventName = s.substring(29).replace('%', ' ');
        return IDManager.generateEventId(eventName, time);
    }

    /**
     * Get the event ID
     *
     * @return The ID of the event associated with this Alert.
     */
    public String getEventId() {
        return eventId;
    }
}
