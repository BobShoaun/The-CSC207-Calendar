package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.time.Duration;
import java.time.LocalDate;
import java.util.GregorianCalendar;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RepeatingAlert extends GraphicalUserInterface {

    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField textField;
    @FXML
    private TextField durationAmount;
    @FXML
    private ChoiceBox<String> durationUnit;

    public RepeatingAlert() {
        ObservableList<String> options = FXCollections.observableArrayList(
                Stream.of("Days", "Hours", "Minutes", "Seconds").collect(Collectors.toList())
        );
        durationUnit.setItems(options);
    }

    protected void setDate(GregorianCalendar date) {
        LocalDate localDate = LocalDate.of(date.get(GregorianCalendar.YEAR), date.get(GregorianCalendar.MONTH),
                date.get(GregorianCalendar.DATE));
        datePicker.setValue(localDate);
        textField.setText("" + date.get(GregorianCalendar.HOUR) + date.get(GregorianCalendar.MINUTE));
    }

    protected void setPeriod(Duration period) {
        double time = period.getSeconds();
        String unit;
        if (time >= 86400) {
            unit = "Days";
            time /= 86400;
        } else if (time >= 3600) {
            unit = "Hours";
            time /= 3600;
        } else if (time >= 60) {
            unit = "Minutes";
            time /= 60;
        } else {
            unit = "Seconds";
        }
        durationUnit.setValue(unit);
        durationAmount.setText("" + time);
    }
}
