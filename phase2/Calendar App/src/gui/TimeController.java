package gui;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;
import user.Calendar;

import java.io.IOException;
import java.time.LocalDate;
import java.util.GregorianCalendar;

public class TimeController extends Application {
    @FXML
    DatePicker timeView;

    private Calendar calendar;

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
        Update();
    }

    public Calendar getCalendar() {
        return calendar;
    }

    private void Update(){
        if(calendar != null){
            GregorianCalendar time = calendar.getTime();
            LocalDate date = LocalDate.of(time.get(GregorianCalendar.YEAR), time.get(GregorianCalendar.MONTH),
                    time.get(GregorianCalendar.DATE));
            if(timeView != null)
                timeView.setValue(date);
        }
    }

    public void setClicked(MouseEvent event){
        Update();
    }

    public void resetClicked(MouseEvent event){
        LocalDate date = timeView.getValue();
        GregorianCalendar time = calendar.getTime();
        time.set(GregorianCalendar.YEAR, date.getYear());
        time.set(GregorianCalendar.MONTH, date.getMonthValue());
        time.set(GregorianCalendar.DAY_OF_MONTH, date.getDayOfMonth());
        calendar.setTime(time);
        Update();
    }

    public void incDayClicked(MouseEvent event){
        GregorianCalendar time = calendar.getTime();
        time.add(GregorianCalendar.DATE, 1);
        calendar.setTime(time);
        Update();
    }

    public void incWeekClicked(MouseEvent event){
        GregorianCalendar time = calendar.getTime();
        time.add(GregorianCalendar.DATE, 7);
        calendar.setTime(time);
        Update();
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("timeController.fxml"));
        Parent root = fxmlLoader.load();

        TimeController timeController = fxmlLoader.getController();
        timeController.setCalendar(calendar); // TODO: I feel like this is not right
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
