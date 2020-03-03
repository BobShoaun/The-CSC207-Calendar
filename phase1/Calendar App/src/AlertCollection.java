//******************************************************
//  AlertCollection.java    Author: Colin De Vlieghere
//******************************************************

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AlertCollection {
    // List is only for manually created Alerts.
    private List<Alert> manAlerts;
    private String eventId;
    private DateGenerator dg;

    /**
     * Creates a new Alert group (possibly repeating)
     * @param eventId The ID of the Event attached to the Alert.
     */
    public AlertCollection(String eventId) {
        this.eventId = eventId;
        manAlerts = new ArrayList<>();
    }

    public String getEventId() {
        return eventId;
    }

    public void addAlert(Alert a) {
        manAlerts.add(a);
    }

    public void addAlert(Date time) {
        manAlerts.add(new Alert(time));
    }

    public void addAlert(Date start, Date period) {
        throw new NotImplementedException();
    }

    public boolean removeAlert(Alert a) {
        return manAlerts.remove(a);
    }

    public List<Alert> getAlerts(Date start, Date end) {
        throw new NotImplementedException();
    }

}
