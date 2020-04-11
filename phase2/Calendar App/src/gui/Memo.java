package gui;
import javafx.fxml.FXML;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.stage.Stage;
import user.Calendar;

import java.awt.*;

public class Memo {

    private Calendar calendar;

    @FXML
    private TextField memoTitleField;
    @FXML
    private TextField memoTextField;
    @FXML
    private Label memoExistsLabel;

    public void setCalendar(Calendar c) { this.calendar = c; }

    public void init() { memoExistsLabel.setVisible(false);}

    @FXML
    private void createMemo(Event e) {
        String memoTitle = memoTitleField.getText();
        String memoText = memoTextField.getText();

        try {
//            calendar.addMemo(memoTitle, memoText);
        } catch (IllegalArgumentException ex) {
            memoExistsLabel.setText("Memo name already exists!");
            memoExistsLabel.setVisible(true);
        }
        showCalendarUI(e);
    }

    @FXML
    private void deleteMemo(Event e) {
        String memoTitle = memoTitleField.getText();
        mt.Memo memo = calendar.getMemo(memoTitle);
        calendar.removeMemo(memo);
        showCalendarUI(e);
    }

    private void showCalendarUI(Event e) {
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.hide();
    }

}
