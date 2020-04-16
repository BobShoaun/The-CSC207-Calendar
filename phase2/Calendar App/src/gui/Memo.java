package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import user.Calendar;

import java.util.List;

public class Memo extends GraphicalUserInterface {

    ObservableList list = FXCollections.observableArrayList();

    private Calendar calendar;
    private String memoTitle;

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
        memoTitle = memoTitleField.getText();
        loadEvents();
    }

    public void setCalendar(Calendar c) { this.calendar = c; }


    @FXML
    private void editMemo(Event e) {
        String newMemoTitle = memoTitleField.getText();
        String memoText = memoTextField.getText();
        boolean edited = !memoTitle.equals(newMemoTitle);

        try {
            calendar.editMemoTitle(memoTitle, newMemoTitle);
            calendar.editMemoText(memoTitle, memoText);
        } catch (IllegalArgumentException ex) {
            memoExistsLabel.setText("Memo name already exists!");
            memoExistsLabel.setVisible(true);
        }

        if (edited) { memoTitle = newMemoTitle; }
        showViewMemoUI(e);
    }

    @FXML
    private void deleteMemo(Event e) {
        String memoTitle = memoTitleField.getText();
        memotag.Memo memo = calendar.getMemo(memoTitle);
        calendar.removeMemo(memo);
        showViewMemoUI(e);
    }

    private void loadEvents() {
        list.remove(list);
        memotag.Memo memo = calendar.getMemo(memoTextField.getText());
        List<String> events = memo.getEvents();
        for (String s: events) {
            list.add(s);
        }
        eventsList.getItems().addAll(list);
    }

    private void showViewMemoUI(Event e) {
        ViewMemos controller = showGUI("viewMemos.fxml");
        controller.setCalendar(calendar);
    }


}
