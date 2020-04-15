package gui;

import entities.AlertCollection;
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

public class AddRepeatingAlert extends GraphicalUserInterface {

    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField textField;
    @FXML
    private TextField durationAmount;
    @FXML
    private ChoiceBox<String> durationUnit;
    private AlertCollection ac;
    private Alert controller;

//    protected void setDate(GregorianCalendar date) {
//        LocalDate localDate = LocalDate.of(date.get(GregorianCalendar.YEAR), date.get(GregorianCalendar.MONTH),
//                date.get(GregorianCalendar.DATE));
//        datePicker.setValue(localDate);
//        textField.setText("" + date.get(GregorianCalendar.HOUR) + date.get(GregorianCalendar.MINUTE));
//    }
//
//    protected void setPeriod(Duration period) {
//        double time = period.getSeconds();
//        String unit;
//        if (time >= 86400) {
//            unit = "Days";
//            time /= 86400;
//        } else if (time >= 3600) {
//            unit = "Hours";
//            time /= 3600;
//        } else if (time >= 60) {
//            unit = "Minutes";
//            time /= 60;
//        } else {
//            unit = "Seconds";
//        }
//        durationUnit.setValue(unit);
//        durationAmount.setText("" + time);
//    }

    public void initialize(Alert c, AlertCollection ac) {
        this.ac = ac;
        this.controller = c;
        ObservableList<String> options = FXCollections.observableArrayList(
                Stream.of("Days", "Hours", "Minutes", "Seconds").collect(Collectors.toList())
        );
        durationUnit.setItems(options);
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
            GregorianCalendar startTime = new GregorianCalendar(localDate.getYear(),
                    localDate.getMonthValue() - 1,
                    localDate.getDayOfMonth(),
                    Integer.parseInt(text.substring(0, 2)),
                    Integer.parseInt(text.substring(3)));
            try {
                int amount = Integer.parseInt(durationAmount.getText());
                String unit = durationUnit.getValue();
                Duration d;
                switch (unit) {
                    case "Days":
                        d = Duration.ofDays(amount);
                        break;
                    case "Hours":
                        d = Duration.ofHours(amount);
                        break;
                    case "Minutes":
                        d = Duration.ofMinutes(amount);
                        break;
                    default:
                        d = Duration.ofSeconds(amount);
                        break;
                }

                ac.addAlert(startTime, d);
                closeGUI();
                controller.update();

            } catch (Exception e) {
                System.out.println("Error in parsing integer");
            }
        }
    }
}
