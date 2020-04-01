package gui;

import entities.Alert;
import entities.AlertCollection;
import entities.Event;
import entities.EventCollection;
import exceptions.InvalidDateException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import user.User;

import java.util.GregorianCalendar;
import java.util.stream.Collectors;

public class Calendar {
    private User user;
    private user.Calendar calendar;

    @FXML
    ListView alertList;

    public void setUser(User user) throws InvalidDateException {
        this.user = user;
        setActiveCalendar(user.getCalendar(0));
        updateAlerts();
    }

    public void setActiveCalendar(user.Calendar calendar) throws InvalidDateException {
        this.calendar = calendar;

        //For testing purposes until supported by ui
        EventCollection singleEvents = calendar.getSingleEventCollection();
        if(singleEvents.getEvents().size() == 0){
            GregorianCalendar tomorrow = new GregorianCalendar();
            tomorrow.add(GregorianCalendar.DATE, 1);
            GregorianCalendar tomorrowLater = (GregorianCalendar)tomorrow.clone();
            tomorrowLater.add(GregorianCalendar.HOUR_OF_DAY, 1);
            Event event = new Event(tomorrow.getTime().toString()+"test", "test", tomorrow, tomorrowLater);
            singleEvents.addEvent(event);
            AlertCollection alertCollection = new AlertCollection(event, calendar.getDataSaver());
            alertCollection.addAlert(tomorrow);
            calendar.addAlertCollection(alertCollection);
        }
    }

    private void updateAlerts(){
        GregorianCalendar future = calendar.getTime();
        future.add(GregorianCalendar.DATE, 14);
        ObservableList<String> alertStrings = FXCollections.observableArrayList(
                calendar.getAlerts(calendar.getTime(), future).stream()
                        .map(Alert::toString).collect(Collectors.toList()));
        alertList.setItems(alertStrings);
    }

    public void alertListClicked(MouseEvent mouseEvent) {
        System.out.println("Clicked on alert: " + alertList.getSelectionModel().getSelectedItems().get(0));
    }
}
