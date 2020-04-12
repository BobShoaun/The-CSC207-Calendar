package gui;

import entities.AlertCollection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import user.DataSaver;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.stream.Collectors;

public class Alert extends GraphicalUserInterface {
    private AlertCollection ac;
    @FXML
    ListView<String> manAlertList;
    @FXML
    ListView<String> repAlertList;
    private String currManAlert;
    private String currRepAlert;

    protected void setAlertCollection(AlertCollection ac) {
        this.ac = ac;
    }

    private void updateManAlerts() {
        if (ac == null)
            System.out.println("AC has not been set!");
        ObservableList<String> alertStrings = FXCollections.observableArrayList(
                ac.getManAlerts().stream().map(entities.Alert::toString).collect(Collectors.toList()));
        manAlertList.setItems(alertStrings);
    }

    private void updateRepeatingAlerts() {
        if (ac == null)
            System.out.println("AC has not been set!");
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
    private void deleteManAlert() {
        System.out.println("Clicked delete manual alert");
        if (currManAlert != null) {
            DataSaver ds = new DataSaver("");
            GregorianCalendar time = ds.parseEventId(currManAlert);

            ac.removeManualAlert(time);

            updateManAlerts();
        }
    }

    @FXML
    private void deleteRepeatingAlert() {
        System.out.println("Clicked delete repeating alert");
        if (currRepAlert != null) {
            DataSaver ds = new DataSaver("");
            GregorianCalendar time = ds.parseEventId(currRepAlert);

            ac.removeGeneratedAlert(time);

            updateRepeatingAlerts();
        }
    }

}
