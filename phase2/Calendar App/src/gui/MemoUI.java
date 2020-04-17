package gui;

import event.Event;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import memotag.Memo;
import user.Calendar;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MemoUI extends GraphicalUserInterface {

    private ObservableList<String> list = FXCollections.observableArrayList();
    private Calendar calendar;
    private Memo memo;

    /**
     * Set the memo which is edited by this
     * @param memo
     */
    public void setMemo(Memo memo){
        this.memo = memo;
        eventsList.setItems(list);
        memoExistsLabel.setVisible(false);
        memoTitleField.setText(memo.getTitle());
        memoTextField.setText(memo.getText());
        loadEvents();
    }

    @FXML private TextField memoTitleField;
    @FXML private TextField memoTextField;
    @FXML private Label memoExistsLabel;
    @FXML private ListView<String> eventsList;

    public MemoUI() {
    }

    public void setCalendar(Calendar c) {
        this.calendar = c;
    }

    @FXML
    private void editMemo() {
        String newMemoTitle = memoTitleField.getText();
        String memoText = memoTextField.getText();
        try {
            String oldTitle = memo.getTitle();
            calendar.editMemoTitle(oldTitle, newMemoTitle);
            calendar.editMemoText(newMemoTitle, memoText);
        } catch (IllegalArgumentException ex) {
            memoExistsLabel.setText("Memo name already exists!");
            memoExistsLabel.setVisible(true);
        }
        showViewMemoUI();
    }

    @FXML
    private void deleteMemo() {
        calendar.removeMemo(memo);
        showViewMemoUI();
    }

    private void loadEvents() {
        list.clear();
        if(memo != null){
            List<String> events = memo.getEvents();
            for (String eventId :
                    events) {
                Event event = calendar.getEvent(eventId);
                list.add(event.getName() + " " + event.getStartDate().getTime().toString());
            }
        }
    }

    private void showViewMemoUI() {
        closeGUI();
    }
}
