package gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class EditEvent extends GraphicalUserInterface implements Initializable {

    @FXML private TextField nameField;
    @FXML private DatePicker startDate;
    @FXML private TextField startTime;
    @FXML private DatePicker endDate;
    @FXML private TextField endTime;
    @FXML private TextField tagsField;
    @FXML private TextField memosField;
    @FXML private TextArea memoTextArea;
    @FXML private TextField seriesField;
    @FXML private Label seriesErrorLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO add code
    }

    @FXML
    private void handleDone() {
        // TODO add code for when done is clicked
        closeGUI();
    }

    @FXML
    private void handleAddAlerts() {
        // TODO add code for when add alerts is clicked
    }

}
