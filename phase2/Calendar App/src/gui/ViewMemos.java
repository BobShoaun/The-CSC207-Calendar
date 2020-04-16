package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import mt.Memo;
import user.Calendar;

import java.util.List;

public class ViewMemos extends gui.GraphicalUserInterface {

    @FXML
    private ListView<String> memoList;

    @FXML
    private Label selectMemoLabel;

    private Calendar calendar;

    private String selectedMemo;

    ObservableList<String> list = FXCollections.observableArrayList();

    public void initialize() {
        selectMemoLabel.setVisible(false);
        memoList.setItems(list);
    }

    public void setCalendar(Calendar c) {
        this.calendar = c;
        loadMemos();
    }

    private void loadMemos() {
        list.clear();
        List<Memo> memos = calendar.getMemos();
        for (Memo m: memos) {
            String s = m.getTitle() + ": " + m.getText();
            list.add(s);
        }
    }

    @FXML
    public void chooseMemo(javafx.scene.input.MouseEvent mouseEvent) {
        String memo = memoList.getSelectionModel().getSelectedItem();
        if (memo == null || memo.isEmpty()) {
            selectedMemo = null;
            selectMemoLabel.setVisible(true);
        } else {
            selectedMemo = memo;
        }

    }

    @FXML
    private void editMemo(ActionEvent actionEvent) {
        if (selectedMemo == null || selectedMemo.isEmpty()) {
            selectMemoLabel.setVisible(true);
            System.out.println("No memo selected!");
        } else {
            showMemoUI(actionEvent);
        }
    }

    @FXML
    private void showMemoUI(ActionEvent actionEvent) {
        gui.Memo memo = showGUI("memo.fxml");
        memo.setCalendar(calendar);
    }

    @FXML
    private void close(ActionEvent actionEvent) {
        closeGUI();
    }
}
