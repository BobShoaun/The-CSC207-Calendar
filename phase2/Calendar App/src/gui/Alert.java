package gui;

import com.sun.istack.internal.NotNull;
import entities.AlertCollection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import user.DataSaver;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.stream.Collectors;

import static entities.IDManager.parseEventId;

public class Alert extends GraphicalUserInterface {
    private final AlertCollection ac;
    @FXML
    ListView<String> manAlertList;
    @FXML
    ListView<String> repAlertList;
    @FXML
    Label title;
    private String currManAlert;
    private String currRepAlert;

    /**
     * Initialize this controller
     *
     * @param ac The AlertCollection
     */
    public Alert(@NotNull AlertCollection ac) {
        this.ac = ac;
        title.setText("Alerts for " + ac.getEventId());
        updateRepeatingAlerts();
        updateManAlerts();
    }

    /**
     * Update the manual alerts list.
     */
    private void updateManAlerts() {
        if (ac == null)
            System.out.println("AC has not been set!");
        assert ac != null;
        ObservableList<String> alertStrings = FXCollections.observableArrayList(
                ac.getManAlerts().stream().map(entities.Alert::toString).collect(Collectors.toList()));
        manAlertList.setItems(alertStrings);
    }

    /**
     * Update the repeating alerts list.
     */
    private void updateRepeatingAlerts() {
        if (ac == null)
            System.out.println("AC has not been set!");
        assert ac != null;
        ObservableList<String> alertStrings = FXCollections.observableArrayList(
                ac.getGeneratedAlerts(new GregorianCalendar(1950, Calendar.JANUARY, 0),
                        new GregorianCalendar(2100, Calendar.JANUARY, 0))
                        .stream().map(entities.Alert::toString).collect(Collectors.toList()));
        repAlertList.setItems(alertStrings);
    }

    @FXML
    private void manAlertListClicked() {
        currManAlert = manAlertList.getSelectionModel().getSelectedItems().get(0);
        System.out.println("Clicked on alert: " + currManAlert);
    }

    @FXML
    private void repAlertListClicked() {
        currRepAlert = repAlertList.getSelectionModel().getSelectedItems().get(0);
        System.out.println("Clicked on alert: " + currRepAlert);
    }

    @FXML
    private void deleteManualAlert() {
        System.out.println("Clicked delete manual alert");
        if (currManAlert != null) {
            DataSaver ds = new DataSaver("");
            GregorianCalendar time = parseEventId(currManAlert);

            ac.removeManualAlert(time);

            updateManAlerts();
        }
    }

    @FXML
    private void deleteRepeatingAlert() {
        System.out.println("Clicked delete repeating alert");
        if (currRepAlert != null) {
            DataSaver ds = new DataSaver("");
            GregorianCalendar time = parseEventId(currRepAlert);

            ac.removeGeneratedAlert(time);

            updateRepeatingAlerts();
        }
    }

    @FXML
    private void addManualAlert() {
        System.out.println("Clicked add manual alert");
        openGUI("manualAlert.fxml");
    }

    @FXML
    private void addRepeatingAlert() {
        System.out.println("Clicked add repeating alert");
        openGUI("repeatingAlert.fxml");
    }

    @FXML
    private void editManualAlert() {
        System.out.println("Clicked edit manual alert");
        if (currManAlert != null) {
            ManualAlert controller = openGUI("manualAlert.fxml");
            controller.setDate(parseEventId(currManAlert));
        }
    }

    @FXML
    private void editRepeatingAlert() {
        System.out.println("Clicked edit repeating alert");
        if (currRepAlert != null) {
            RepeatingAlert controller = openGUI("repeatingAlert.fxml");
            controller.setDate(parseEventId(currRepAlert));
            controller.setPeriod(null); //TODO: fix
        }
    }

}
