package gui;

import entities.Alert;
import entities.Event;
import exceptions.InvalidDateException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import mt.Memo;
import mt.Tag;
import user.User;

import java.io.IOException;
import java.time.LocalDate;
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
    ListView<String> displayedEventList;
    @FXML
    private Label lastLoginLabel;

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
            } else if(searchCriterion.equals("Postponed")){
                startDate.setVisible(false);
                endDate.setVisible(false);
                searchTermField.setVisible(false);
            }
        });
        ObservableList<String> searchOptions = FXCollections.observableArrayList(
                Stream.of("Date", "Tag", "Memo name", "Postponed").collect(Collectors.toList())
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
        } else if(searchCriterion.equals("Postponed")){
            for (Event event :
                    calendar.getPostponedEvents()) {
                eventList.add(event.toString());
            }
        }
    }

    public void setUser(User user) throws InvalidDateException {
        this.user = user;
        setActiveCalendar(user.getCalendar(0));
        Initialize();
        updateAlerts();

        lastLoginLabel.setText("Last login on: " + user.getLastLoginTime().getTime());
    }

    public void setActiveCalendar(user.Calendar calendar) throws InvalidDateException {
        this.calendar = calendar;

        setWindowTitle(calendar.getName());

//        //For testing purposes until supported by ui
//        EventCollection singleEvents = calendar.getSingleEventCollection();
//        if (singleEvents.getEvents().size() == 0) {
//            GregorianCalendar tomorrow = new GregorianCalendar();
//            tomorrow.add(GregorianCalendar.DATE, 1);
//            GregorianCalendar tomorrowLater = (GregorianCalendar) tomorrow.clone();
//            tomorrowLater.add(GregorianCalendar.HOUR_OF_DAY, 1);
//            Event event = new Event("test%" + tomorrow.getTime().toString(), "test", tomorrow, tomorrowLater);
//            singleEvents.addEvent(event);
//            AlertCollection alertCollection = new AlertCollection(event, calendar.getDataSaver());
//            alertCollection.addAlert(tomorrow);
//            calendar.addAlertCollection(alertCollection);
//        }
    }

    private void updateAlerts() {
        GregorianCalendar past = calendar.getTime();
        past.add(GregorianCalendar.DATE, -1);
        ObservableList<String> alertStrings = FXCollections.observableArrayList(
                calendar.getAlerts(calendar.getTime(), past).stream()
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

    public void setTheme() {
        if (user.getDarkTheme()) {
            com.sun.javafx.css.StyleManager.getInstance().addUserAgentStylesheet("gui/DarkTheme.css");
            darkTheme.setSelected(true);
        } else {
            com.sun.javafx.css.StyleManager.getInstance().removeUserAgentStylesheet("gui/DarkTheme.css");
            darkTheme.setSelected(false);
        }
    }
    public void searchTermValueChange(KeyEvent keyEvent) {
        UpdateDisplayedEvents();
    }

    @FXML
    private void handleSwitchCalendar() {
        CalendarSwitcher controller = showGUI("calendarSwitcher.fxml");
        controller.setUser(user);
    }

    @FXML
    private void handleLogout() throws IOException {
        System.out.println("logout: " + user.getName());
        user.logout();
        Login login = showGUI("login.fxml");
        login.setDarkTheme();
    }

}
