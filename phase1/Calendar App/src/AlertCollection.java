//******************************************************
//  AlertCollection.java    Author: Colin De Vlieghere
//******************************************************

import java.util.List;

public class AlertCollection {
    // List is only for manually created Alerts.
    private List<Alert> manAlerts;
    private int eventId;
    private String name;
    private DateGenerator dg;

    public AlertCollection(int eventId, String name, DateGenerator dg) {
        this.eventId = eventId;
        this.name = name;
        this.dg = dg;
    }

    public void addAlert(Alert a) {
        manAlerts.add(a);
    }

    public boolean removeAlert(Alert a) {
        return manAlerts.remove(a);
    }

}
