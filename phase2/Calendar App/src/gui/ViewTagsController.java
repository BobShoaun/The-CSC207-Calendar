package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import memotag.Tag;
import user.Calendar;

import java.util.List;

/**
 * GUI controller for viewing Tags
 */
public class ViewTagsController extends gui.GraphicalUserInterface {

    @FXML
    private ListView<String> tagList;
    @FXML
    private Label selectTagLabel;

    private Calendar calendar;
    private String selectedTag;
    private ObservableList<String> list = FXCollections.observableArrayList();

    /**
     * Set the GUI to the correct config upon start
     */
    public void initialize() {
        selectTagLabel.setVisible(false);
        tagList.setItems(list);
    }

    /**
     * Set the calendar
     *
     * @param c Calendar to be set
     */
    public void setCalendar(Calendar c) {
        this.calendar = c;
        loadTag();
    }

    private void loadTag() {
        List<Tag> tags = calendar.getTags();
        for (Tag t : tags) {
            String s = t.getText();
            list.add(s);
        }
    }

    /**
     * Set the Tag to the one selected by the user
     */
    @FXML
    public void chooseTag() {
        String tag = tagList.getSelectionModel().getSelectedItem();
        if (tag == null || tag.isEmpty()) {
            selectedTag = null;
            selectTagLabel.setVisible(true);
        } else {
            selectedTag = tag;
        }

    }

    @FXML
    private void editTag(Event e) {
        if (selectedTag == null || selectedTag.isEmpty()) {
            selectTagLabel.setVisible(true);
        } else {
            showTagUI(e);
        }
    }

    @FXML
    private void showTagUI(Event e) {
        TagController tagController = showGUI("tag.fxml");
        tagController.setCalendar(calendar);
        tagController.setTag(calendar.getTag(tagList.getSelectionModel().getSelectedItem()));
    }

    /**
     * Exit window
     */
    public void close() {
        closeGUI();
    }

}