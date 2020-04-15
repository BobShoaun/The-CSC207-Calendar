package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import mt.Memo;
import user.Calendar;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class viewMemos extends gui.GraphicalUserInterface implements Initializable {

    @FXML
    private ListView<String> memoList;

    @FXML
    private Label selectMemoLabel;

    private Calendar calendar;

    private String selectedMemo;

    ObservableList list = FXCollections.observableArrayList();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        selectMemoLabel.setVisible(false);
        loadMemo();
    }

    private void setCalendar(Calendar c) { this.calendar = c; }

    private void loadMemo() {
        list.remove(list);
        List<Memo> memos = calendar.getMemos();
        for (Memo m: memos) {
            String s = m.getTitle() + ": " + m.getText();
            list.add(s);
        }
        memoList.getItems().addAll(list);
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
        Memo memo = showGUI("memo.fxml");
        memo.setCalendar(calendar);
    }

    @FXML
    private void showCalendarUI(ActionEvent actionEvent) {
        Calendar calendarController = showGUI("calendar.fxml");
        calendarController.setUser(userManager.getCurrentUser());
        calendarController.setTheme();
    }
}
