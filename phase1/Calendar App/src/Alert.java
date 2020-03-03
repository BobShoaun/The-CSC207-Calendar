import java.util.Date;
import java.util.GregorianCalendar;

/**
 * An Alert, representing a notification for an Event.
 *
 * @author colin
 */
public class Alert {

    private GregorianCalendar time;

    public Alert(GregorianCalendar time) {
        this.time = time;
    }

    public Date getTime() {
        return time.getTime();
    }

    public void setTime(Date time) {
        this.time.setTime(time);
    }

}
