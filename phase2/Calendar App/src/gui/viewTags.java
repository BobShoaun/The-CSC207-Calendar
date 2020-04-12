package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import mt.Tag;
import user.Calendar;

import java.awt.*;
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
        selectTagLabel.setVisible(false);
        loadTag();
    }

    private void setCalendar(Calendar c) { this.calendar = c; }

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
        } else {
            selectedTag = tag;
        }

    }

    @FXML
    private void editTag(ActionEvent actionEvent) {
        if (selectedTag == null || selectedTag.isEmpty()) {
            selectTagLabel.setVisible(true);
            System.out.println("No tag selected!");
        } else {
            showTagUI(actionEvent);
        }
    }

    @FXML
    private void showTagUI(ActionEvent actionEvent) {
        //Tag tag = showGUI("tag.fxml");
    }

    @FXML
    private void showCalendarUI(ActionEvent actionEvent) {
        //gui.GraphicalUserInterface calendar = showGUI("calendar.fxml");
    }

}
