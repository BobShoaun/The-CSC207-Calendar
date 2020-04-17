package consoleui;

import alert.Alert;
import alert.AlertCollection;
import exceptions.PeriodAlreadyExistsException;

/**
 * Terminal interface for AlertCollection.
 *
 * @author Colin De Vlieghere
 */
public class AlertUI extends UserInterface {

    private final AlertCollection alerts;

    public AlertUI(AlertCollection ac) {
        alerts = ac;
    }

    /**
     * Display the contents of the alert collection to the user.
     */
    @Override
    public void display() {
        System.out.println(alerts);
    }

    /**
     * Start this user interface.
     */
    @Override
    public void show() {

        alerts.removeOldAlerts();

        display();
        int option = getOptionsInput(new String[]{"Exit",
                "Add a single alert",
                "Add a repeating alert",
                "Edit an alert",
                "Remove an alert",
        });
        switch (option) {
            case 0:
                return;
            case 1:
                addAlert();
                break;
            case 2:
                addRepeatingAlert();
                break;
            case 3:
                editAlert();
                break;
            case 4:
                removeAlert();
                break;
        }
    }


    private void removeAlert() {
        boolean works = false;
        while (!works) {
            works = alerts.removeAlert(
                    getDateInput("Enter the date of the alert to be removed: ")
            );
        }
    }

    private void editAlert() {
        Alert a = alerts.getAlert(getDateInput("Enter the time of the alert to edit: "));
        a.setTime(getDateInput("Enter a new time for this alert: ").getTime());
    }

    private void addRepeatingAlert() {
        boolean badInput = true;
        while (badInput) {
            try {
                alerts.addAlert(
                        getDateInput("Enter a start time for the alert: "),
                        getDurationInput("Enter a period for the series: "));
                badInput = false;
            } catch (PeriodAlreadyExistsException e) {
                System.out.println("Period already exists. Please try again: ");
            }
        }
    }

    private void addAlert() {
        boolean result;
        result = alerts.addAlert(
                getDateInput("Enter a time for the alert: "));
        while (!result) {
            result = alerts.addAlert(
                    getDateInput("Alert already exists. Please try again: "));
        }
    }
}
