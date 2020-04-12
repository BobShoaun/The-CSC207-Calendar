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

    @FXML
    private TextField nameField;
    @FXML
    private DatePicker startTime;
    @FXML
    private DatePicker endTime;
    @FXML
    private TextField seriesField;
    @FXML
    private TextField tagsField;
    @FXML
    private TextField memosField;

    @FXML
    private void handleDone(Event e) throws IOException, InvalidDateException {

    }

    @FXML
    private void handleCancel () {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

}
