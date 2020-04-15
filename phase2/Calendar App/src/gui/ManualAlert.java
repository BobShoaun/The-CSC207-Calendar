package gui;

import entities.AlertCollection;
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
    private AlertCollection ac;
    private Alert controller;

    protected void initialize(Alert c, AlertCollection ac) {
        this.ac = ac;
        this.controller = c;
    }

    protected void setDate(GregorianCalendar date) {
        LocalDate localDate = LocalDate.of(date.get(GregorianCalendar.YEAR),
                date.get(GregorianCalendar.MONTH) + 1,
                date.get(GregorianCalendar.DATE));
        datePicker.setValue(localDate);

        String hour = "" + date.get(GregorianCalendar.HOUR);
        String minute = "" + date.get(GregorianCalendar.MINUTE);

        if (hour.length() == 1)
            hour = 0 + hour;
        if (minute.length() == 1)
            minute = 0 + minute;

        textField.setText(hour + ":" + minute);
    }

    @FXML
    private void doneButton() {
        LocalDate localDate = datePicker.getValue();
        String text = textField.getText();
        if (localDate != null && text != null) {
            if (!text.matches("([01]?[0-9]|2[0-3]):[0-5][0-9]")) {
                System.out.println("Invalid time");
                return;
            }
            GregorianCalendar time = new GregorianCalendar(localDate.getYear(),
                    localDate.getMonthValue() - 1,
                    localDate.getDayOfMonth(),
                    Integer.parseInt(text.substring(0, 2)),
                    Integer.parseInt(text.substring(3)));
            ac.removeManualAlert(time);
            ac.addAlert(time);
            System.out.println("Added alert " + time.getTime());
            closeGUI();
            controller.update();
        }
    }

}
