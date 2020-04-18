package gui;

import alert.AlertCollection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.awt.*;
import java.time.Duration;
import java.time.LocalDate;
import java.util.GregorianCalendar;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * GUI controller for adding repeating alerts
 */
public class RepeatingAlertController extends GraphicalUserInterface {

    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField textField;
    @FXML
    private TextField durationAmount;
    @FXML
    private ChoiceBox<String> durationUnit;
    @FXML
    private Checkbox indefiniteEndDateChoice;

    private AlertCollection ac;
    private AlertController controller;

    /**
     * Initialize the controller to the correct config
     *
     * @param c  The controller for Alert which called this controller
     * @param ac The AlertCollection to be managed
     */
    public void initialize(AlertController c, AlertCollection ac) {
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
                e.printStackTrace();
            }
        }
    }

}