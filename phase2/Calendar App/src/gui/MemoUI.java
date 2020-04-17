package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import user.Calendar;

import java.util.List;

public class MemoUI extends GraphicalUserInterface {

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


    public MemoUI() {
        memoExistsLabel.setVisible(false);
        memoTitle = memoTitleField.getText();
        loadEvents();
    }

    public void setCalendar(Calendar c) { this.calendar = c; }


    @FXML
    private void editMemo() {
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
        showViewMemoUI();
    }

    @FXML
    private void deleteMemo() {
        String memoTitle = memoTitleField.getText();
        memotag.Memo memo = calendar.getMemo(memoTitle);
        calendar.removeMemo(memo);
        showViewMemoUI();
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

    private void showViewMemoUI() {
        ViewMemosUI controller = showGUI("viewMemos.fxml");
        controller.setCalendar(calendar);
    }


}
