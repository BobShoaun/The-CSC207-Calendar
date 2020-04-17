package gui;

import alert.Alert;
import alert.AlertCollection;
import com.sun.istack.internal.NotNull;
import event.IDManager;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * GUI controller for alerts
 */
public class AlertController extends GraphicalUserInterface {

    private AlertCollection ac;

    @FXML
    private ListView<String> manAlertList;
    @FXML
    private ListView<String> repAlertList;
    @FXML
    private Label title;

    private Alert currManAlert;
    private Alert currRepAlert;

    private ObservableList<Alert> manualAlerts;
    private ObservableList<String> manualAlertStrings;
    private ObservableList<Alert> repeatingAlerts;
    private ObservableList<String> repeatingAlertStrings;
    private user.Calendar calendar;

    /**
     * Initialize this controller
     *
     * @param ac The AlertCollection
     * @param calendar The calendar to which the alert collection belongs
     */
    public void initialize(@NotNull AlertCollection ac, user.Calendar calendar) {
        this.ac = ac;
        this.calendar = calendar;
        title.setText("Alerts for " + ac.getEventId().substring(29).replace('%', ' ')
                + " at " + IDManager.parseEventId(ac.getEventId()).getTime());
        manualAlerts = FXCollections.observableArrayList();
        manualAlerts.addListener((ListChangeListener<Alert>) c -> {
            manualAlertStrings.clear();
            for (Alert alert :
                    manualAlerts) {
                manualAlertStrings.add(alert.getTime().getTime().toString());
            }
        });
        repeatingAlerts = FXCollections.observableArrayList();
        repeatingAlerts.addListener((ListChangeListener<Alert>) c -> {
            repeatingAlertStrings.clear();
            for (Alert alert :
                    repeatingAlerts) {
                repeatingAlertStrings.add(alert.getTime().getTime().toString());
            }
        });
        manualAlertStrings = FXCollections.observableArrayList();
        repeatingAlertStrings = FXCollections.observableArrayList();
        manAlertList.setItems(manualAlertStrings);
        repAlertList.setItems(repeatingAlertStrings);
        updateRepeatingAlerts();
        updateManAlerts();
    }

    private void updateManAlerts() {
        manualAlerts.clear();
        manualAlerts.addAll(ac.getManAlerts());
    }

    private void updateRepeatingAlerts() {
        repeatingAlerts.clear();
        repeatingAlerts.addAll(ac.getGeneratedAlerts(new GregorianCalendar(1950, Calendar.JANUARY, 0),
                new GregorianCalendar(2100, Calendar.JANUARY, 0)));
    }

    @FXML
    private void manAlertListClicked() {
        if(manAlertList.getSelectionModel().getSelectedIndex() >= 0){
            currManAlert = manualAlerts.get(manAlertList.getSelectionModel().getSelectedIndex());
            update();
        }
    }

    @FXML
    private void repAlertListClicked() {
        currRepAlert = repeatingAlerts.get(repAlertList.getSelectionModel().getSelectedIndex());
        update();
    }

    @FXML
    private void deleteManualAlert() {
        if (currManAlert != null) {
            ac.removeAlert(currManAlert.getTime());
            updateManAlerts();
        }
        update();
    }

    @FXML
    private void deleteRepeatingAlert() {
        if (currRepAlert != null) {
            updateRepeatingAlerts();
        }
        update();
    }

    @FXML
    private void addManualAlert() {
        ManualAlertController controller = openGUI("manualAlert.fxml");
        controller.initialize(this, ac);
        update();
    }

    @FXML
    private void addRepeatingAlert() {
        RepeatingAlertController controller = openGUI("repeatingAlert.fxml");
        controller.initialize(this, ac);
        update();
    }

    @FXML
    private void editManualAlert() {
        if (currManAlert != null) {
            ManualAlertController controller = openGUI("manualAlert.fxml");
            controller.setDate(currManAlert.getTime());
            controller.initialize(this, ac);
        }
        update();
    }

    @FXML
    private void editRepeatingAlert() {
        if (currRepAlert != null) {
            ManualAlertController controller = openGUI("manualAlert.fxml");
            controller.initialize(this, ac);
            controller.setDate(currRepAlert.getTime());
        }
        update();
    }

    /**
     * Update the ListViews with the manual and repeating alerts.
     */
    protected void update() {
        updateManAlerts();
        updateRepeatingAlerts();
        calendar.getAlertCollectionManager().reloadAlertCollections();
    }

    @FXML
    private void clearManAlerts() {
        ac.removeAllManualAlerts();
        update();
    }

    @FXML
    private void clearRepeatingAlerts() {
        ac.removeAllGeneratedAlerts();
        update();
    }

    @FXML
    private void doneButton() {
        closeGUI();
    }

}
