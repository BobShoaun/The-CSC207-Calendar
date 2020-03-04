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

    public Alert(String string) {
        this.time = new GregorianCalendar();
        this.time.setTimeInMillis(Long.parseLong(string));
    }

    public Date getTime() {
        return time.getTime();
    }

    public void setTime(Date time) {
        this.time.setTime(time);
    }

    @Override
    public String toString() {
        return "" + time.getTimeInMillis();
    }

}
