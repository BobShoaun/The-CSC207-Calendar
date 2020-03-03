//************************************************
//  Alert.java      Author: Colin De Vlieghere
//************************************************

import java.util.Date;

public class Alert {

    private Date time;

    public Alert(Date time) {
        this.time = time;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

}
