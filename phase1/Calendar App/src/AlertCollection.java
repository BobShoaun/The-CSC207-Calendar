//******************************************************
//  AlertCollection.java    Author: Colin De Vlieghere
//******************************************************

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Date;
import java.util.List;

public class AlertCollection {
    // List is only for manually created Alerts.
    private List<Alert> manAlerts;
    private String eventId;
    private String name;
    private DateGenerator dg;

    public AlertCollection(String eventId, String name, DateGenerator dg) {
        this.eventId = eventId;
        this.name = name;
        this.dg = dg;
    }

    public String getEventId(){return eventId;}

    public void addAlert(Alert a) {
        manAlerts.add(a);
    }

    public void addAlert(String name, Date time){throw new NotImplementedException();}

    public boolean removeAlert(Alert a) {
        return manAlerts.remove(a);
    }

    public List<Alert> getAlerts(Date start, Date end){ throw new NotImplementedException(); }

    public void addAlert(String name, Date start, Date period) { throw new NotImplementedException(); }
}
