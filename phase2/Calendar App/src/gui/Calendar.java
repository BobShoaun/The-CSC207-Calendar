package gui;

import entities.Alert;
import entities.AlertCollection;
import entities.Event;
import entities.EventCollection;
import exceptions.InvalidDateException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import user.User;

import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.stream.Collectors;

public class Calendar {
    private User user;
    private user.Calendar calendar;
    private String currAlert;

    @FXML
    ListView<String> alertList;

    @FXML
    CheckBox darkTheme;

    public void setUser(User user) throws InvalidDateException {
        this.user = user;
        setActiveCalendar(user.getCalendar(0));
        updateAlerts();
    }

    public void setActiveCalendar(user.Calendar calendar) throws InvalidDateException {
        this.calendar = calendar;

        //For testing purposes until supported by ui
        EventCollection singleEvents = calendar.getSingleEventCollection();
        if (singleEvents.getEvents().size() == 0) {
            GregorianCalendar tomorrow = new GregorianCalendar();
            tomorrow.add(GregorianCalendar.DATE, 1);
            GregorianCalendar tomorrowLater = (GregorianCalendar) tomorrow.clone();
            tomorrowLater.add(GregorianCalendar.HOUR_OF_DAY, 1);
            Event event = new Event("test%" + tomorrow.getTime().toString(), "test", tomorrow, tomorrowLater);
            singleEvents.addEvent(event);
            AlertCollection alertCollection = new AlertCollection(event, calendar.getDataSaver());
            alertCollection.addAlert(tomorrow);
            calendar.addAlertCollection(alertCollection);
        }
    }

    private void updateAlerts() {
        GregorianCalendar future = calendar.getTime();
        future.add(GregorianCalendar.DATE, 14);
        ObservableList<String> alertStrings = FXCollections.observableArrayList(
                calendar.getAlerts(calendar.getTime(), future).stream()
                        .map(Alert::toString).collect(Collectors.toList()));
        alertList.setItems(alertStrings);
    }

    @FXML
    private void alertListClicked() {
        currAlert = alertList.getSelectionModel().getSelectedItems().get(0);
        System.out.println("Clicked on alert: " + currAlert);
    }

    public void showTimeController(MouseEvent mouseEvent) throws IOException {
        Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
        TimeController timeController = new TimeController();
        timeController.setCalendar(calendar);
        timeController.start(stage);
    }

    @FXML
    private void clearNotification() {
        System.out.println("Clicked clear");
        if (currAlert != null) {
            ObservableList<String> temp = alertList.getItems();
            temp.remove(currAlert);
            alertList.setItems(temp);
        } // TODO: actually delete the Alert?
    }

    @FXML
    private void clearAllNotifications() {
        System.out.println("Clicked clear all");
        ObservableList<String> empty = FXCollections.observableArrayList();
        alertList.setItems(empty);
        // TODO: delete all shown Alerts
    }

    @FXML
    private void updateTheme(){
        user.setDarkTheme(!user.getDarkTheme());
        if(user.getDarkTheme()){
            com.sun.javafx.css.StyleManager.getInstance().addUserAgentStylesheet("gui/DarkTheme.css");
        } else {
            com.sun.javafx.css.StyleManager.getInstance().removeUserAgentStylesheet("gui/DarkTheme.css");
        }
    }

    public void setTheme(){
        if(user.getDarkTheme()){
            com.sun.javafx.css.StyleManager.getInstance().addUserAgentStylesheet("gui/DarkTheme.css");
            darkTheme.setSelected(true);
        }
    }
}
