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
        System.out.println(alerts.toString());
    }

    @Override
    public void show() {

    }
}
