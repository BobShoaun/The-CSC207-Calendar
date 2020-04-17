package gui;

import event.Event;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import memotag.Tag;
import user.Calendar;

import java.util.List;

/**
 * GUI controller class for editing Event Tags.
 */
public class TagController extends gui.GraphicalUserInterface {

    private final ObservableList<String> list = FXCollections.observableArrayList();
    private Calendar calendar;
    private String tagName;
    private Tag tag;

    @FXML
    private TextField tagNameTextField;
    @FXML
    private ListView<String> tagEventList;

    /**
     * Set the tag to edit
     *
     * @param tag Tag to edit
     */
    public void setTag(Tag tag) {
        this.tag = tag;
        tagNameTextField.setText(tag.getText());
        tagName = tag.getText();
        loadEvents();
    }

    /**
     * Set the Calendar.
     *
     * @param c Calendar to be set
     */
    protected void setCalendar(Calendar c) {
        this.calendar = c;
        tagEventList.setItems(list);
    }

    private void loadEvents() {
        list.clear();
        memotag.Tag tag = calendar.getTag(tagNameTextField.getText());
        if (tag != null) {
            List<String> events = tag.getEvents();
            for (String s : events) {
                Event event = calendar.getEvent(s);
                list.add(event.getName() + " at " + event.getStartDate().getTime());
            }
        }
    }

    private void editTag() {
        String newTagName = tagNameTextField.getText();
        if (!tagName.equals(newTagName)) {
            if (!newTagName.equals("")) {
                calendar.getTag(tagName).setText(newTagName);
            }
        }
    }

    /**
     * Switch to the view tag UI page and close this window.
     */
    public void showViewTagUI() {
        editTag();
        closeGUI();
    }

}