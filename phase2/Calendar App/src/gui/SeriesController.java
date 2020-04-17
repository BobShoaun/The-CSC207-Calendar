package gui;

import event.Event;
import event.Series;
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

/**
 * GUI controller class for Series
 */
public class SeriesController extends GraphicalUserInterface implements Initializable {

    private final ObservableList<String> timeChoice = FXCollections.observableArrayList("Day", "Week", "Month", "Year");

    @FXML
    private Label dateErrorLabel;
    @FXML
    private CheckBox indefiniteEndDateChoice;
    @FXML
    private Label seriesErrorLabel;
    @FXML
    private TextField seriesField;
    @FXML
    private DatePicker endDate;
    @FXML
    private ChoiceBox<String> timeChoiceBox;
    @FXML
    private TextField repeatNumField;

    private Event baseEvent;
    private Series series;
    private user.Calendar calendar;
    private String seriesName;
    private GregorianCalendar start;
    private GregorianCalendar end;
    private Duration timeSpan;

    /**
     * Set the details for this Series.
     *
     * @param baseEvent Base Event
     * @param calendar  Calendar
     */
    public void setDetails(Event baseEvent, user.Calendar calendar) {
        this.baseEvent = baseEvent;
        this.calendar = calendar;
        this.start = baseEvent.getStartDate();
    }

    /**
     * Initialize the GUI to have the correct values
     *
     * @param location  URL
     * @param resources ResourceBundle
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        timeChoiceBox.setValue("Day");
        timeChoiceBox.setItems(timeChoice);
    }

    @FXML
    private void handleCreateSeries() {
        seriesErrorLabel.setVisible(false);
        dateErrorLabel.setVisible(false);
        try {
            getUserInput();
            if (seriesName.equals("")) {
                calendar.addEventSeries(baseEvent.getName(), start, end, timeSpan, baseEvent);
            } else {
                getSeries();
                series.addRepeatingEvent(baseEvent, start, end, timeSpan);
            }
            calendar.getDataSaver().saveCalendar(calendar);
            closeGUI();
        } catch (InvalidDateException e) {
            dateErrorLabel.setVisible(true);
            dateErrorLabel.setText("Invalid Date");
        } catch (InvalidTimeInputException e) {
            dateErrorLabel.setVisible(true);
            dateErrorLabel.setText("Invalid Number");
        } catch (NoSuchSeriesException e) {
            seriesErrorLabel.setVisible(true);
        }
    }

    @FXML
    private void handleCancel() {
        closeGUI();
    }


    protected void getUserInput() throws InvalidDateException, InvalidTimeInputException {
        getTimeSpan();
        seriesName = seriesField.getText();
        if (indefiniteEndDateChoice.isSelected()) {
            end = null;
        } else {
            end = GregorianCalendar.from(endDate.getValue().atStartOfDay(ZoneId.systemDefault()));
        }
    }

    private void getSeries() throws NoSuchSeriesException {
        if (!seriesName.equals("")) {
            series = calendar.getSeries(seriesName);
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