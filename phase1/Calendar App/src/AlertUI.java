import exceptions.PeriodAlreadyExistsException;

import java.io.IOException;

/**
 * Terminal interface for AlertCollection.
 */
public class AlertUI extends UserInterface {

    private AlertCollection alerts;

    public AlertUI(AlertCollection ac) {
        alerts = ac;
    }

    @Override
    public void display() {
        System.out.println(alerts);
    }

    @Override
    public void show() {
        display();
        int option = getOptionsInput(new String[]{"Exit",
                "Add a single alert",
                "Add a repeating alert",
                "Edit an alert",
                "Remove an alert",
        });
        switch (option) {
            case 0:
                break;
            case 1:
                boolean result = alerts.addAlert(
                        getDateInput("Enter a time for the alert: "));
                while (!result) {
                    result = alerts.addAlert(
                            getDateInput("Alert already exists. Please try again: "));
                }
                try {
                    alerts.save();
                } catch (IOException e) {
                    System.out.println("Save error occurred.");
                    ;
                }
                break;
            case 2:
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
                try {
                    alerts.save(); // TODO: move save to AC
                } catch (IOException e) {
                    System.out.println("Save error occurred.");
                }
                break;
            case 3:
                Alert a = alerts.getAlert(getDateInput("Enter the time of the alert to edit: "));
                a.setTime(getDateInput("Enter a new time for this alert: ").getTime());
                try {
                    alerts.save();
                } catch (IOException e) {
                    System.out.println("Save error occurred.");
                    ;
                }
                break;
            case 4:
                boolean works = false;
                while (!works) {
                    works = alerts.removeAlert(
                            getDateInput("Enter the date of the alert to be removed: ")
                    );
                }
                try {
                    alerts.save();
                } catch (IOException e) {
                    System.out.println("Save error occurred.");
                    ;
                }
                break;
        }
    }
}
