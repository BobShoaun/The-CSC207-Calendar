package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import memotag.Memo;
import user.Calendar;

import java.util.List;

/**
 * GUI controller class for viewing memos.
 */
public class ViewMemosUI extends gui.GraphicalUserInterface {

    @FXML
    private ListView<String> memoList;
    @FXML
    private Label selectMemoLabel;

    private Calendar calendar;
    private String selectedMemo;
    private final ObservableList<String> list = FXCollections.observableArrayList();

    /**
     * Set the GUI to the correct config upon start
     */
    public void initialize() {
        selectMemoLabel.setVisible(false);
        memoList.setItems(list);
    }

    /**
     * Set the Calendar
     *
     * @param c Calendar
     */
    public void setCalendar(Calendar c) {
        this.calendar = c;
        loadMemos();
    }

    private void loadMemos() {
        list.clear();
        List<Memo> memos = calendar.getMemos();
        for (Memo m : memos) {
            String s = m.getTitle() + ": " + m.getText();
            list.add(s);
        }
    }

    /**
     * Set the Memo to the selected Memo.
     */
    @FXML
    private void chooseMemo() {
        String memo = memoList.getSelectionModel().getSelectedItem();
        if (memo == null || memo.isEmpty()) {
            selectedMemo = null;
            selectMemoLabel.setVisible(true);
        } else {
            selectedMemo = memo;
        }

    }

    @FXML
    private void editMemo() {
        if (selectedMemo == null || selectedMemo.isEmpty()) {
            selectMemoLabel.setVisible(true);
            System.out.println("No memo selected!");
        } else {
            showMemoUI();
        }
    }

    @FXML
    private void showMemoUI() {
        if (memoList.getSelectionModel().getSelectedIndex() >= 0) {
            MemoUI memoUI = showGUI("memo.fxml");
            memoUI.setCalendar(calendar);
            memoUI.setMemo(calendar.getMemos().get(memoList.getSelectionModel().getSelectedIndex()));
        }
    }

    @FXML
    private void close() {
        closeGUI();
    }

}