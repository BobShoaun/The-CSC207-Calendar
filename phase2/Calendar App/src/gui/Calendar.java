package gui;

import com.sun.org.apache.xml.internal.security.Init;
import entities.Alert;
import entities.AlertCollection;
import entities.Event;
import entities.EventCollection;
import exceptions.InvalidDateException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import mt.Memo;
import mt.Tag;
import user.User;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Calendar extends GraphicalUserInterface {

    private User user;
    private user.Calendar calendar;
    private String currAlert;

    @FXML
    ListView<String> alertList;

    @FXML
    CheckBox darkTheme;
    @FXML
    ChoiceBox searchByList;
    @FXML
    DatePicker startDate;
    @FXML
    DatePicker endDate;
    @FXML
    TextField searchTermField;
    @FXML
    ListView displayedEventList;

    ObservableList<String> eventList;

    private void Initialize(){
        eventList = FXCollections.observableArrayList();

        displayedEventList.setItems(eventList);

        searchByList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            String searchCriterion = (String)newValue;
            if(searchCriterion.equals("Date")){
                startDate.setVisible(true);
                GregorianCalendar time = calendar.getTime();
                LocalDate date = LocalDate.of(time.get(GregorianCalendar.YEAR), time.get(GregorianCalendar.MONTH),
                        time.get(GregorianCalendar.DATE));
                startDate.setValue(date);
                LocalDate end =  LocalDate.of(time.get(GregorianCalendar.YEAR), time.get(GregorianCalendar.MONTH),
                        time.get(GregorianCalendar.DATE) + 1);
                endDate.setVisible(true);
                endDate.setValue(end);
                searchTermField.setVisible(false);
            } else if(searchCriterion.equals("Tag")){
                startDate.setVisible(false);
                endDate.setVisible(false);
                searchTermField.setVisible(true);
            } else if(searchCriterion.equals("Memo name")){
                startDate.setVisible(false);
                endDate.setVisible(false);
                searchTermField.setVisible(true);
            }
        });
        ObservableList<String> searchOptions = FXCollections.observableArrayList(
                Stream.of("Date", "Tag", "Memo name").collect(Collectors.toList())
        );
        searchByList.setItems(searchOptions);
        searchByList.setValue("Date");
        startDate.valueProperty().addListener((time) -> {
            UpdateDisplayedEvents();
        });
        endDate.valueProperty().addListener((time) -> {
            UpdateDisplayedEvents();
        });
    }

    void UpdateDisplayedEvents(){
        eventList.clear();
        String searchCriterion = (String)searchByList.getValue();
        if(searchCriterion.equals("Date")){
            if(startDate.getValue() != null && endDate.getValue() != null){
                if(startDate.getValue().isBefore(endDate.getValue())){
                    GregorianCalendar start = calendar.getTime();
                    start.set(GregorianCalendar.YEAR, startDate.getValue().getYear());
                    start.set(GregorianCalendar.MONTH, startDate.getValue().getMonthValue());
                    start.set(GregorianCalendar.DATE, startDate.getValue().getDayOfMonth());
                    GregorianCalendar end = calendar.getTime();
                    end.set(GregorianCalendar.YEAR, endDate.getValue().getYear());
                    end.set(GregorianCalendar.MONTH, endDate.getValue().getMonthValue());
                    end.set(GregorianCalendar.DATE, endDate.getValue().getDayOfMonth());
                    List<Event> events = calendar.getEvents(start, end);
                    for (Event event :
                            events) {
                        eventList.add(event.toString());
                    }
                }
            }
        } else if(searchCriterion.equals("Tag")){
            String tagString = searchTermField.getText();
            Tag tag = calendar.getTag(tagString);
            if(tag != null){
                List<Event> events = tag.getEvents().stream().map(e -> calendar.getEvent(e)).collect(Collectors.toList());
                for (Event event :
                        events) {
                    eventList.add(event.toString());
                }
            }
        } else if(searchCriterion.equals("Memo name")){
            String memoString = searchTermField.getText();
            Memo memo = calendar.getMemo(memoString);
            if(memo != null){
                List<Event> events = memo.getEvents().stream().map(e -> calendar.getEvent(e)).collect(Collectors.toList());
                for (Event event :
                        events) {
                    eventList.add(event.toString());
                }
            }
        }
    }

    public void setUser(User user) throws InvalidDateException {
        this.user = user;
        setActiveCalendar(user.getCalendar(0));
        Initialize();
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
        TimeController timeController = new TimeController(calendar);
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

    public void setTheme() {
        if (user.getDarkTheme()) {
            com.sun.javafx.css.StyleManager.getInstance().addUserAgentStylesheet("gui/DarkTheme.css");
            darkTheme.setSelected(true);
        }
    }
    public void searchTermValueChange(KeyEvent keyEvent) {
        UpdateDisplayedEvents();
    }

    @FXML
    private void handleSwitchCalendar() {
        System.out.println("Switch Calendar clicked");
        CalendarSwitcher controller = showGUI("calendarSwitcher.fxml");
        controller.setUser(user);
    }

}
