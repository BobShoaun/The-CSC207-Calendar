package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.Label;
import mt.Tag;
import user.Calendar;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class viewTags extends gui.GraphicalUserInterface implements Initializable {

    @FXML
    private ListView<String> tagList;

    @FXML
    private Label selectTagLabel;

    private Calendar calendar;

    private String selectedTag;

    ObservableList list = FXCollections.observableArrayList();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        selectTagLabel.setVisible(false);
//        loadTag();
    }

    protected void setCalendar(Calendar c) { this.calendar = c; }

    private void loadTag() {
        list.remove(list);
        List<Tag> tags = calendar.getTags();
        for (Tag t: tags) {
            String s = t.getText();
            list.add(s);
        }
        tagList.getItems().addAll(list);
    }

    @FXML
    public void chooseTag(javafx.scene.input.MouseEvent mouseEvent) {
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
            System.out.println("No tag selected!");
        } else {
            showTagUI(e);
        }
    }

    @FXML
    private void showTagUI(Event e) {
        gui.Tag tag = showGUI("tag.fxml");
        tag.setCalendar(calendar);
    }

    @FXML
    private void showCalendarUI(Event e) {
        gui.Calendar calendar = showGUI("calendar.fxml");
    }

}
