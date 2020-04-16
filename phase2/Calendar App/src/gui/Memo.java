package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.event.Event;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import user.Calendar;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Memo extends GraphicalUserInterface {

    ObservableList list = FXCollections.observableArrayList();

    private Calendar calendar;
    private mt.Memo memo;

    @FXML
    private TextField memoTitleField;
    @FXML
    private TextField memoTextField;
    @FXML
    private Label memoExistsLabel;
    @FXML
    private ListView<String> eventsList;


    public Memo() {
        memoExistsLabel.setVisible(false);
        loadEvents();
    }

    public void setCalendar(Calendar c) { this.calendar = c; }

    public void setMemo(mt.Memo m) {this.memo = m;}


    @FXML
    private void editMemo(Event e) {
        String memoTitle = memoTitleField.getText();
        String memoText = memoTextField.getText();

        try {
            calendar.editMemoTitle(memoTitle, memoTitle);
            calendar.editMemoText(memoTitle, memoText);
        } catch (IllegalArgumentException ex) {
            memoExistsLabel.setText("Memo name already exists!");
            memoExistsLabel.setVisible(true);
        }
        showViewMemoUI(e);
    }

    @FXML
    private void deleteMemo(Event e) {
        String memoTitle = memoTitleField.getText();
        mt.Memo memo = calendar.getMemo(memoTitle);
        calendar.removeMemo(memo);
        showViewMemoUI(e);
    }

    private void loadEvents() {
        list.remove(list);
        mt.Memo memo = calendar.getMemo(memoTextField.getText());
        List<String> events = memo.getEvents();
        for (String s: events) {
            list.add(s);
        }
        eventsList.getItems().addAll(list);
    }

    private void showViewMemoUI(Event e) {
        viewMemos vm = showGUI("viewMemos.fxml");
    }


}
