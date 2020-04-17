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
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.stream.Collectors;

import static event.IDManager.parseEventId;

/**
 * GUI controller for alerts
 */
public class AlertUI extends GraphicalUserInterface {

    private AlertCollection ac;

    @FXML private ListView<String> manAlertList;
    @FXML private ListView<String> repAlertList;
    @FXML private Label title;

    private Alert currManAlert;
    private Alert currRepAlert;

    private ObservableList<Alert> manualAlerts;
    private ObservableList<String> manualAlertStrings;
    private ObservableList<Alert> repeatingAlerts;
    private ObservableList<String> repeatingAlertStrings;

    /**
     * Initialize this controller
     *
     * @param ac The AlertCollection
     */
    public void initialize(@NotNull AlertCollection ac) {
        this.ac = ac;
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

    /**
     * Update the manual alerts list.
     */
    private void updateManAlerts() {
        if (ac == null)
            System.out.println("AC has not been set!");
        manualAlerts.clear();
        manualAlerts.addAll(ac.getManAlerts());
    }

    /**
     * Update the repeating alerts list.
     */
    private void updateRepeatingAlerts() {
        if (ac == null)
            System.out.println("AC has not been set!");
        repeatingAlerts.clear();
        repeatingAlerts.addAll(ac.getGeneratedAlerts(new GregorianCalendar(1950, Calendar.JANUARY, 0),
                new GregorianCalendar(2100, Calendar.JANUARY, 0)));
    }

    @FXML
    private void manAlertListClicked() {
        currManAlert = manualAlerts.get(manAlertList.getSelectionModel().getSelectedIndex());
        System.out.println("Clicked on alert: " + currManAlert);
        update();
    }

    @FXML
    private void repAlertListClicked() {
        currRepAlert = repeatingAlerts.get(repAlertList.getSelectionModel().getSelectedIndex());
        System.out.println("Clicked on alert: " + currRepAlert);
        update();
    }

    @FXML
    private void deleteManualAlert() {
        System.out.println("Clicked delete manual alert");
        if (currManAlert != null) {
            ac.removeAlert(currManAlert.getTime());
            updateManAlerts();
        }
        update();
    }

    @FXML
    private void deleteRepeatingAlert() {
        System.out.println("Clicked delete repeating alert");
        if (currRepAlert != null) {
            if (!ac.removeGeneratedAlert(currRepAlert.getTime()))
                System.out.println("Could not remove generated alert");
            updateRepeatingAlerts();
        }
        update();
    }

    @FXML
    private void addManualAlert() {
        System.out.println("Clicked add manual alert");
        ManualAlertUI controller = openGUI("manualAlert.fxml");
        controller.initialize(this, ac);
        update();
    }

    @FXML
    private void addRepeatingAlert() {
        System.out.println("Clicked add repeating alert");
        AddRepeatingAlertUI controller = openGUI("addRepeatingAlert.fxml");
        controller.initialize(this, ac);
        update();
    }

    @FXML
    private void editManualAlert() {
        System.out.println("Clicked edit manual alert");
        if (currManAlert != null) {
            ManualAlertUI controller = openGUI("manualAlert.fxml");
            controller.setDate(currManAlert.getTime());
            controller.initialize(this, ac);
        }
        update();
    }

    @FXML
    private void editRepeatingAlert() {
        System.out.println("Clicked edit repeating alert");
        if (currRepAlert != null) {
            ManualAlertUI controller = openGUI("manualAlert.fxml");
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
