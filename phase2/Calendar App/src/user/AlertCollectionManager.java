package user;

import alert.Alert;
import alert.AlertCollection;
import alert.AlertComparator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    public void reloadAlertCollections() {
        alertCollections = dataSaver.loadAlertCollections();
    }

    /**
     * Remove an alert based on its string; formatted:
     * Event Name at Time
     *
     * @param alertString String of alert
     */
    public void removeAlert(String alertString) {
        alertString = alertString.split("at ")[1];
        SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy");
        GregorianCalendar alertTime = new GregorianCalendar();
        try {
            alertTime.setTime(df.parse(alertString));
        } catch (ParseException e) {
            e.printStackTrace();
        }
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