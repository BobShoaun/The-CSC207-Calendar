//************************************************
//  Alert.java      Author: Colin De Vlieghere
//************************************************

import java.util.Date;

public class Alert {
    private Date time;
    private String alertText;

    public Alert(Date time, String alertText) {
        this.time = time;
        this.alertText = alertText;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getAlertText() {
        return alertText;
    }

    public void setAlertText(String alertText) {
        this.alertText = alertText;
    }
}
