package gui;

import event.Event;
import exceptions.InvalidDateException;
import exceptions.InvalidTimeInputException;
import exceptions.NoSuchSeriesException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.Duration;
import java.time.ZoneId;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;
import java.util.Scanner;

public class SeriesUI extends GraphicalUserInterface implements Initializable {

    private final ObservableList<String> timeChoice = FXCollections.observableArrayList("Day", "Week", "Month", "Year");
    @FXML
    private CheckBox indefiniteEndDateChoice;
    @FXML
    private Label seriesErrorLabel;
    @FXML
    private TextField seriesField;

    @FXML
    private DatePicker endDate;
    @FXML
    private ChoiceBox timeChoiceBox;
    @FXML
    private TextField repeatNumField;
    @FXML
    private TextField nameField;

    private Event baseEvent;
    private user.Calendar calendar;
    private String name;
    private String seriesName;
    private GregorianCalendar start;
    private GregorianCalendar end;
    private Duration timeSpan;

    public void setDetails(Event baseEvent, user.Calendar calendar) {
        this.baseEvent = baseEvent;
        this.calendar = calendar;
        this.start = baseEvent.getStartDate();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        timeChoiceBox.setValue("Day");
        timeChoiceBox.setItems(timeChoice);
    }

    @FXML
    private void handleCreateSeries() {
        try {
            getUserInput();
            calendar.addEventSeries(name, start, end, timeSpan, baseEvent);
            System.out.println(calendar.getSeries(name));
        } catch (InvalidDateException e) {
            System.out.println("Invalid Time");
        } catch (InvalidTimeInputException e) {
            System.out.println("Invalid date");
        } catch (NoSuchSeriesException e) {
            System.out.println("No such Series");
        }
    }

    @FXML
    private void handleCancel() {
        closeGUI();
    }

    private void getUserInput() throws InvalidDateException, InvalidTimeInputException {
        name = nameField.getText();
        getTimeSpan();
        seriesName = seriesField.getText();
        end = GregorianCalendar.from(endDate.getValue().atStartOfDay(ZoneId.systemDefault()));
        if (indefiniteEndDateChoice.isSelected()) {
            end = null;
        } else if (endDate.getValue() == null && !indefiniteEndDateChoice.isSelected()) {
            throw new InvalidDateException();
        }
    }

    private void getTimeSpan() throws InvalidTimeInputException {
        String timeString = (String) timeChoiceBox.getValue();
        Scanner sc = new Scanner(repeatNumField.getText());
        if (sc.hasNextInt()) {
            int timeUnit = sc.nextInt();
            switch (timeString) {
                case "Day":
                    timeSpan = Duration.ofDays(timeUnit);
                    break;
                case "Week":
                    timeSpan = Duration.ofDays(timeUnit * 7);
                    break;
                case "Month":
                    timeSpan = Duration.ofDays(timeUnit * 30);
                    break;
                case "Years":
                    timeSpan = Duration.ofDays(timeUnit * 365);
                    break;
                default:
                    System.out.println("Time input incorrect");
                    break;
            }
        } else {
            throw new InvalidTimeInputException();
        }

    }

}