package gui;

import exceptions.InvalidDateException;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AddEvent extends GraphicalUserInterface implements Initializable {

    @FXML private TextField nameField;
    @FXML private DatePicker startDate;
    @FXML private DatePicker endDate;
    @FXML private TextField startTime;
    @FXML private TextField endTime;
    @FXML private TextField seriesField;
    @FXML private TextField tagsField;
    @FXML private TextField memosField;

    @FXML
    private void handleDone(Event e) throws IOException, InvalidDateException {
        // TODO add code here
        closeGUI();
    }

    @FXML
    private void handleCancel () {
        closeGUI();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

}
