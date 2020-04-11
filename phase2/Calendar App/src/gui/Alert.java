package gui;

import entities.AlertCollection;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.util.GregorianCalendar;
import java.util.stream.Collectors;

public class Alert {
    private AlertCollection ac;
    @FXML
    ListView<String> manAlertList;
    @FXML
    ListView<String> repAlertList;

    protected void setAlertCollection(AlertCollection ac) {
        this.ac = ac;
    }

    private void updateManAlerts() {
        if (ac == null)
            System.out.println("AC has not been set!");
        ObservableList<String> alertStrings = FXCollections.observableArrayList(
                ac.getManAlerts().stream()
                        .map(entities.Alert::toString).collect(Collectors.toList()));
        manAlertList.setItems(alertStrings);
    }

}
