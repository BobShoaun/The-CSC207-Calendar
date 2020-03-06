import java.util.Date;
import java.util.GregorianCalendar;

/**
 * An Alert, representing a notification for an Event.
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
        this.time = time;
    }

    /**
     * Create an Alert from a String representation.
     *
     * @param string  the toString() output, representing time
     * @param eventId The ID of the event
     */
    public Alert(String eventId, String string) {
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
     * Get a string of data in of this Alert.
     *
     * @return The time in milliseconds for the Alert
     */
    public String getString() {
        return "" + time.getTimeInMillis();
    }

    @Override
    public String toString() {
        return "" + time.getTime().toString();
    }

    public String getEventId() {
        return eventId;
    }
}
