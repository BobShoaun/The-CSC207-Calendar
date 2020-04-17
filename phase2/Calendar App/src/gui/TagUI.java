package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import user.Calendar;

import java.util.List;

public class TagUI extends gui.GraphicalUserInterface {

    ObservableList list = FXCollections.observableArrayList();

    private Calendar calendar;
    private String tagName;

    @FXML
    private TextField tagNameTextField;

    @FXML
    private ListView tagEventList;

    public TagUI() {
        tagName = tagNameTextField.getText();
        loadEvents();
    }

    protected void setCalendar(Calendar c) { this.calendar = c; }

    private void loadEvents() {
        list.remove(list);
        memotag.Tag tag = calendar.getTag(tagNameTextField.getText());
        List<String> events = tag.getEvents();
        for (String s: events) {
            list.add(s);
        }
        tagEventList.getItems().addAll(list);
    }

    private void editTag() {
        String newTagName = tagNameTextField.getText();
        if ( !tagName.equals(newTagName) ) {
            calendar.getTag(tagName).setText(newTagName);
        }
    }

    public void showViewTagUI() {
        editTag();
        ViewTagsUI controller = showGUI("viewTags.fxml");
        controller.setCalendar(calendar);
    }

}
