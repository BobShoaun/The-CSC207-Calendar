import java.util.Date;
import java.util.GregorianCalendar;

/**
 * An Alert, representing a notification for an Event.
 *
 * @author colin
 */
public class Alert {

    private GregorianCalendar time;

    /**
     * Create an Alert
     *
     * @param time The time at which the notification goes off
     */
    public Alert(GregorianCalendar time) {
        this.time = time;
    }

    /**
     * Create an Alert from a String representation.
     *
     * @param string the toString() output, representing time
     */
    public Alert(String string) {
        this.time = new GregorianCalendar();
        this.time.setTimeInMillis(Long.parseLong(string));
    }

    /**
     * Get the alert's time.
     *
     * @return The time of the Alert.
     */
    public Date getTime() {
        return time.getTime();
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
     * Get a string representation of this Alert.
     *
     * @return The time in milliseconds for the Alert
     */
    @Override
    public String toString() {
        return "" + time.getTimeInMillis();
    }

}
