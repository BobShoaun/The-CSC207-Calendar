package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import user.Calendar;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Tag extends gui.GraphicalUserInterface implements Initializable {

    ObservableList list = FXCollections.observableArrayList();

    private Calendar calendar;

    @FXML
    private TextField tagNameTextField;

    @FXML
    private ListView tagEventList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadEvents();
    }

    protected void setCalendar(Calendar c) { this.calendar = c; }

    private void loadEvents() {
        list.remove(list);
        mt.Tag tag = calendar.getTag(tagNameTextField.getText());
        List<String> events = tag.getEvents();
        for (String s: events) {
            list.add(s);
        }
        tagEventList.getItems().addAll(list);
    }

    public void showViewTagUI(ActionEvent actionEvent) {
        //gui.GraphicalUserInterface viewTags = showGUI("viewTags.fxml");
    }

}
