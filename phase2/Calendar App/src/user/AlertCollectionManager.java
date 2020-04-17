package user;

import alert.Alert;
import alert.AlertCollection;
import alert.AlertComparator;
import event.IDManager;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Manager for Calendar's AlertCollections
 */
public class AlertCollectionManager {

    private List<AlertCollection> alertCollections;
    private final DataSaver dataSaver;

    public AlertCollectionManager(DataSaver dataSaver) {
        this.dataSaver = dataSaver;
        reloadAlertCollections();
    }


    /**
     * Reload the alert collections from the disk
     */
    public void reloadAlertCollections(){
        alertCollections = dataSaver.loadAlertCollections();
    }

    public void removeAlert(String alertToString) {
        GregorianCalendar alertTime = IDManager.parseEventId(alertToString);
        for (AlertCollection ac : alertCollections) {
            ac.removeAlert(alertTime);
        }
    }

    /**
     * Get all alert collections
     *
     * @return List of all alert collections
     */
    public List<AlertCollection> getAlertCollections() {
        return alertCollections;
    }

    /**
     * Return sorted alerts which occur in [start, end]
     *
     * @param start The inclusive start time
     * @param end   The inclusive end time
     * @return All alerts in sorted order
     */
    public List<Alert> getAlerts(GregorianCalendar start, GregorianCalendar end) {
        List<Alert> alerts = new ArrayList<Alert>();

        for (AlertCollection alertCollection :
                alertCollections) {
            alerts.addAll(alertCollection.getAlerts(start, end));
        }

        alerts.sort(new AlertComparator());

        return alerts;
    }

    /**
     * Remove all old alerts
     */
    public void removeOldAlerts() {
        for (AlertCollection aC :
                alertCollections) {
            aC.removeOldAlerts();
        }
    }
}