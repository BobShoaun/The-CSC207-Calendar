//************************************************
//  Alert.java      Author: Colin De Vlieghere
//************************************************

import java.util.Date;
import java.util.GregorianCalendar;

public class Alert {

    private GregorianCalendar time;

    public Alert(Date time) {
        this.time = new GregorianCalendar();
        this.time.setTime(time);
    }

    public Date getTime() {
        return time.getTime();
    }

    public void setTime(Date time) {
        this.time.setTime(time);
    }

}
