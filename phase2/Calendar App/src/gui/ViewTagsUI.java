package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import memotag.Tag;
import user.Calendar;

import java.util.List;

public class ViewTagsUI extends gui.GraphicalUserInterface{

    @FXML private ListView<String> tagList;
    @FXML private Label selectTagLabel;

    private Calendar calendar;
    private String selectedTag;
    private ObservableList<String> list = FXCollections.observableArrayList();

    public void initialize() {
        selectTagLabel.setVisible(false);
        tagList.setItems(list);
    }

    public void setCalendar(Calendar c) {
        this.calendar = c;
        loadTag();
    }

    private void loadTag() {
        List<Tag> tags = calendar.getTags();
        for (Tag t: tags) {
            String s = t.getText();
            list.add(s);
        }
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
        TagUI tagUI = showGUI("tag.fxml");
        tagUI.setCalendar(calendar);
    }

    public void close(ActionEvent actionEvent) {
        closeGUI();
    }

}