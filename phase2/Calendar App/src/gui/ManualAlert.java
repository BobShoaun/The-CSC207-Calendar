package gui;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.util.GregorianCalendar;

public class ManualAlert extends GraphicalUserInterface {

    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField textField;

    protected void setDate(GregorianCalendar date) {
        LocalDate localDate = LocalDate.of(date.get(GregorianCalendar.YEAR), date.get(GregorianCalendar.MONTH),
                date.get(GregorianCalendar.DATE));
        datePicker.setValue(localDate);
        textField.setText("" + date.get(GregorianCalendar.HOUR) + date.get(GregorianCalendar.MINUTE));
    }
}
