package gui;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import user.Calendar;

import java.io.IOException;
import java.time.LocalDate;
import java.util.GregorianCalendar;

/**
 * GUI controller class for time-travel magic
 */
public class TimeTravellerController extends gui.GraphicalUserInterface{

    @FXML
    private DatePicker timeView;

    private Calendar calendar;
    private CalendarController calendarController;

    /**
     * Set the Calendar to control
     *
     * @param calendar Calendar to control time
     */
    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
        Update();
    }

    /**
     * Set the calendar controller
     * @param calendarController Value to set
     */
    public void setCalendarController(CalendarController calendarController){
        this.calendarController = calendarController;
    }

    /**
     * Get the Calendar
     *
     * @return Calendar which we are manipulating
     */
    public Calendar getCalendar() {
        return calendar;
    }

    private void Update() {
        if (calendar != null) {
            GregorianCalendar time = calendar.getTime();
            LocalDate date = LocalDate.of(time.get(GregorianCalendar.YEAR), time.get(GregorianCalendar.MONTH),
                    time.get(GregorianCalendar.DATE));
            if (timeView != null)
                timeView.setValue(date);
            if(calendarController != null)
                calendarController.updateDisplays();
        }
    }

    /**
     * Set the date to the value from the user
     */
    public void setClicked() {
        LocalDate date = timeView.getValue();
        GregorianCalendar time = calendar.getTime();
        time.set(GregorianCalendar.YEAR, date.getYear());
        time.set(GregorianCalendar.MONTH, date.getMonthValue());
        time.set(GregorianCalendar.DAY_OF_MONTH, date.getDayOfMonth());
        calendar.setTime(time);
        Update();
    }

    /**
     * Reset button: reset to current time
     */
    public void resetClicked() {
        GregorianCalendar time = calendar.getTime();
        LocalDate date = LocalDate.of(time.get(GregorianCalendar.YEAR), time.get(GregorianCalendar.MONTH),
                time.get(GregorianCalendar.DATE));
        timeView.setValue(date);
        Update();
    }

    /**
     * Increment day button
     */
    public void incDayClicked() {
        GregorianCalendar time = calendar.getTime();
        time.add(GregorianCalendar.DATE, 1);
        calendar.setTime(time);
        Update();
    }

    /**
     * Increment week button
     */
    public void incWeekClicked() {
        GregorianCalendar time = calendar.getTime();
        time.add(GregorianCalendar.DATE, 7);
        calendar.setTime(time);
        Update();
    }

    /**
     * Start this GUI page
     *
     */
    public void start() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("timeTraveller.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        TimeTravellerController timeTravellerController = fxmlLoader.getController();
        timeTravellerController.setCalendar(calendar); // This is correct although I think we are creating a second instance of time controller, so this might not be the best design
        Stage stage = new Stage(); //Create a new stage, so we can have both windows visible at once
        assert root != null;
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

}