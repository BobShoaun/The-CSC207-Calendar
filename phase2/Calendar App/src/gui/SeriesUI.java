package gui;

import event.Event;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class SeriesUI extends GraphicalUserInterface implements Initializable {

    private final ObservableList<String> timeChoice = FXCollections.observableArrayList("Day","Week","Month","Year");

    @FXML private DatePicker startDate;
    @FXML private DatePicker endDate;
    @FXML private ChoiceBox timeChoiceBox;
    @FXML private TextField repeatNumField;
    @FXML private TextField nameField;

    private Event baseEvent;

    public void setBaseEvent(Event baseEvent) {
        this.baseEvent = baseEvent;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        timeChoiceBox.setValue("Day");
        timeChoiceBox.setItems(timeChoice);
    }

    @FXML
    private void handleCreateSeries(){

    }

}