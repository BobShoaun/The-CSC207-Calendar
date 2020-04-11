package gui;

import exceptions.InvalidDateException;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class AddEvent extends GraphicalUserInterface {

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

}
