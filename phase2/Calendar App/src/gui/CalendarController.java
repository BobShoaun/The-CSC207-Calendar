package gui;

import event.Event;
import event.EventCollection;
import event.RepeatingEvent;
import event.Series;
import exceptions.NoSuchSeriesException;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import memotag.Memo;
import memotag.Tag;
import user.User;
import user.UserManager;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * GUI controller class for the main calendar GUI
 */
public class CalendarController extends GraphicalUserInterface {

    private User user;
    private user.Calendar calendar;
    private Event currEvent;
    private String currAlert;
    private Series currSeries = null;
    private String currRepeatingEvent = null;
    private UserManager userManager;
    private int repeatingEventIndex;
    private ObservableList<String> seriesNameList;

    @FXML
    private ListView<String> alertList;
    @FXML
    private ListView<String> displayedRepeatingEventList;
    @FXML
    private ListView<String> displayedSeriesList;
    @FXML
    private CheckBox darkTheme;
    @FXML
    private ChoiceBox<String> searchByList;
    @FXML
    private DatePicker startDate;
    @FXML
    private DatePicker endDate;
    @FXML
    private TextField searchTermField;
    @FXML
    private ListView<String> displayedEventList;
    @FXML
    private Label lastLoginLabel;
    @FXML
    private Label eventErrorLabel;

    private ObservableList<Event> eventList;

    /**
     * initialize the calendar ui, namely the event list and search
     */
    private void initialize() {
        eventList = FXCollections.observableArrayList();
        seriesNameList = FXCollections.observableArrayList();
        displayedSeriesList.setItems(seriesNameList);

        searchByList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            switch (newValue) {
                case "Date":
                    startDate.setVisible(true);
                    GregorianCalendar time = calendar.getTime();
                    LocalDate date = LocalDate.of(time.get(GregorianCalendar.YEAR), time.get(GregorianCalendar.MONTH) + 1,
                            time.get(GregorianCalendar.DATE));
                    startDate.setValue(date);
                    LocalDate end = LocalDate.of(time.get(GregorianCalendar.YEAR), time.get(GregorianCalendar.MONTH) + 1,
                            time.get(GregorianCalendar.DATE)).plusDays(14);
                    endDate.setVisible(true);
                    endDate.setValue(end);
                    searchTermField.setVisible(false);
                    break;
                case "Series name":
                case "Tag": // Tag and memo name cases merged
                case "Memo name":
                    startDate.setVisible(false);
                    endDate.setVisible(false);
                    searchTermField.setVisible(true);
                    break;
                case "Postponed":
                    startDate.setVisible(false);
                    endDate.setVisible(false);
                    searchTermField.setVisible(false);
                    break;
            }
        });
        ObservableList<String> searchOptions = FXCollections.observableArrayList(
                Stream.of("Date", "Tag", "Memo name", "Postponed", "Series name").collect(Collectors.toList())
        );
        searchByList.setItems(searchOptions);
        searchByList.setValue("Date");
        startDate.valueProperty().addListener((time) -> updateDisplayedEvents());
        endDate.valueProperty().addListener((time) -> updateDisplayedEvents());

        eventList.addListener((ListChangeListener<Event>) c -> {
            displayedEventList.getItems().clear();
            for (Event event :
                    eventList) {
                displayedEventList.getItems().add(event.toString());
            }
            displayedEventList.refresh();
        });

        displayedEventList.setOnMouseClicked(displayedEventListClickHandler());
        updateDisplays();
    }

    /**
     * Update all the displays
     */
    @FXML
    protected void updateDisplays(){
        System.out.println(calendar.getSeries());
        updateDisplayedAlerts();
        updateDisplayedEvents();
        updateDisplayedSeries();
        updateDisplayedRepeatingEvents();
    }

    private EventHandler<MouseEvent> displayedEventListClickHandler() {
        return event -> {
            if (displayedEventList.getSelectionModel().getSelectedIndex() != -1) {
                currEvent = eventList.get(displayedEventList.getSelectionModel().getSelectedIndex());
            }
        };
    }

    /**
     * Update the displayed series ListView.
     */
    private void updateDisplayedSeries() {
    	if(seriesNameList == null)
    		return;
        seriesNameList.clear();
        for (Series series : calendar.getSeries()) {
            seriesNameList.add(series.getName());
        }
    }

    /**
     * Update the displayed SubSeries (Repeating events) ListView.
     */
    private void updateDisplayedRepeatingEvents() {
    	if(displayedRepeatingEventList == null)
    		return;
        ArrayList<String> stringRepeatingEvents = new ArrayList<>();
        if (currSeries != null) {
            for (RepeatingEvent repeatingEvent : currSeries.getRepeatingEvents()) {
                stringRepeatingEvents.add(repeatingEvent.toString());
            }
        }
        displayedRepeatingEventList.setItems(FXCollections.observableArrayList(stringRepeatingEvents));
    }


    /**
     * Set the UserManager for the Calendar.
     *
     * @param userManager UserManager
     */
    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    /**
     * Update the displayed events from search
     */
    @FXML
    protected void updateDisplayedEvents() {

        String searchCriterion = searchByList.getValue();
        if (searchCriterion == null) {
            return;
        }
        eventList.clear();
        switch (searchCriterion) {
            case "Date":
                searchByDate();
                break;
            case "Tag":
                searchByTag();
                break;
            case "Memo name":
                searchByMemo();
                break;
            case "Postponed":
                eventList.addAll(calendar.getPostponedEvents());
                break;
            case "Series name":
                searchBySeries();
                break;
        }
    }

    private void searchBySeries() {
        try {
            EventCollection eventCollection = calendar.getSeries(searchTermField.getText());
            eventList.addAll(eventCollection.getEvents(new GregorianCalendar(0, Calendar.JANUARY, 1),
                    new GregorianCalendar(2100, Calendar.JANUARY, 1)));
        } catch (NoSuchSeriesException ignored) {

        }
    }

    private void searchByMemo() {
        String memoString = searchTermField.getText();
        Memo memo = calendar.getMemo(memoString);
        if (memo != null) {
            List<Event> events = memo.getEvents().stream().map(e -> calendar.getEvent(e)).collect(Collectors.toList());
            eventList.addAll(events);
        }
    }

    private void searchByTag() {
        String tagString = searchTermField.getText();
        Tag tag = calendar.getTag(tagString);
        if (tag != null) {
            List<Event> events = tag.getEvents().stream().map(e -> calendar.getEvent(e)).collect(Collectors.toList());
            eventList.addAll(events);
        }
    }

    private void searchByDate() {
        if (startDate.getValue() != null && endDate.getValue() != null) {
            if (startDate.getValue().isBefore(endDate.getValue())) {
                GregorianCalendar start = calendar.getTime();
                start.set(GregorianCalendar.YEAR, startDate.getValue().getYear());
                start.set(GregorianCalendar.MONTH, startDate.getValue().getMonthValue() - 1);
                start.set(GregorianCalendar.DATE, startDate.getValue().getDayOfMonth() - 1);
                GregorianCalendar end = calendar.getTime();
                end.set(GregorianCalendar.YEAR, endDate.getValue().getYear());
                end.set(GregorianCalendar.MONTH, endDate.getValue().getMonthValue() - 1);
                end.set(GregorianCalendar.DATE, endDate.getValue().getDayOfMonth() - 1);
                List<Event> events = calendar.getEvents(start, end);
                eventList.addAll(events);
            }
        }
    }

    /**
     * Set the user for this calendar
     *
     * @param user The user object
     */
    public void setUser(User user) {
        this.user = user;
        setActiveCalendar(user.getCalendar(0));
        initialize();
        updateDisplays();

        setTheme();
        lastLoginLabel.setText("Last login on: " + user.getLastLoginTime().getTime());
    }

    /**
     * Set the calendar to be displayed
     *
     * @param calendar the Calendar to display
     */
    public void setActiveCalendar(user.Calendar calendar) {
        this.calendar = calendar;
        setWindowTitle(calendar.getName());
        updateDisplays();
    }

    /**
     * Update the visible alerts
     */
    private void updateDisplayedAlerts() {
        GregorianCalendar past = calendar.getTime();
        past.add(GregorianCalendar.DATE, -30);
        ObservableList<String> alertStrings = FXCollections.observableArrayList(
                calendar.getAlerts(past, calendar.getTime()).stream()
                        .map(alert -> calendar.getEvent(alert.getEventId()).getName() + " at " + alert.getTime().getTime().toString())
                        .collect(Collectors.toList()));
        alertList.setItems(alertStrings);
    }

    @FXML
    private void alertListClicked() {
        currAlert = alertList.getSelectionModel().getSelectedItems().get(0);
        updateDisplays();
    }

    @FXML
    private void seriesListClicked() {
        if(displayedSeriesList.getSelectionModel().getSelectedIndex() == -1)
            return;
        String stringSeries = displayedSeriesList.getSelectionModel().getSelectedItem();
        if(stringSeries != null) {
            try {
                currSeries = calendar.getSeries(stringSeries);
            } catch (NoSuchSeriesException e) {
                currSeries = null;
            }
        }
        currRepeatingEvent = null;
        updateDisplayedAlerts();
        updateDisplayedEvents(); //We cant use updateDisplays here because updating series list, will remove index
        updateDisplayedRepeatingEvents();
    }

    @FXML
    private void deleteSeries() {
        if (currSeries != null) {
            calendar.removeEventCollection(currSeries);
        }
        calendar.getDataSaver().saveSeries(calendar.getEventManager());
        updateDisplays();
    }

    /**
     * Handles when an event in a series is clicked
     */
    @FXML
    private void repeatingEventListClicked() {
        repeatingEventIndex = displayedRepeatingEventList.getSelectionModel().getSelectedIndex();
        updateDisplays();
    }

    @FXML
    private void deleteRepeatingEvent() {
        if (currRepeatingEvent != null) {
            currSeries.getRepeatingEvents().remove(repeatingEventIndex);
        }
        calendar.getDataSaver().saveEvents(calendar.getEventManager());
        updateDisplays();
    }

    /**
     * Handles when the time button is clicked and shows the time controller window
     *
     * @param mouseEvent The event information of the click
     */
    public void showTimeController(MouseEvent mouseEvent) {
        Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
        TimeTravellerController timeTravellerController = new TimeTravellerController();
        timeTravellerController.setCalendar(calendar);
        timeTravellerController.setCalendarController(this);
        timeTravellerController.start();
    }

    @FXML
    private void clearNotification() {
        if (currAlert != null) {
            calendar.removeAlert(currAlert);
            ObservableList<String> temp = alertList.getItems();
            temp.remove(currAlert);
            alertList.setItems(temp);
        }
    }

    /**
     * Clear the notifications
     */
    @FXML
    private void clearAllNotifications() {
        ObservableList<String> empty = FXCollections.observableArrayList();
        alertList.setItems(empty);
        calendar.removeOldAlerts();
    }

    /**
     * Update the theme between light/dark
     */
    @FXML
    private void updateTheme() {
        user.setDarkTheme(!user.getDarkTheme());
        if (user.getDarkTheme()) {
            com.sun.javafx.css.StyleManager.getInstance().addUserAgentStylesheet("gui/DarkTheme.css");
        } else {
            com.sun.javafx.css.StyleManager.getInstance().removeUserAgentStylesheet("gui/DarkTheme.css");
        }
    }

    /**
     * Set the active theme
     */
    protected void setTheme() {
        if (user.getDarkTheme()) {
            com.sun.javafx.css.StyleManager.getInstance().addUserAgentStylesheet("gui/DarkTheme.css");
            darkTheme.setSelected(true);
        } else {
            com.sun.javafx.css.StyleManager.getInstance().removeUserAgentStylesheet("gui/DarkTheme.css");
            darkTheme.setSelected(false);
        }
    }

    /**
     * Event when the search text has been changed
     */
    public void searchTermValueChange() {
        updateDisplays();
    }

    /**
     * Handle when a new event button is clicked
     */
    @FXML
    private void handleNewEvent() {
        AddEventController controller = openGUI("addEvent.fxml");
        controller.setCalendar(calendar);
        controller.setCalendarUIController(this);
    }

    /**
     * Handle when the edit button is clicked
     */
    @FXML
    private void handleEditEvent() {
        if (currEvent == null) {
            eventErrorLabel.setText("No Event has been selected");
            eventErrorLabel.setVisible(true);
        } else {
            eventErrorLabel.setVisible(false);
            EditEventController controller = openGUI("editEvent.fxml");
            controller.setUserManager(userManager);
            controller.setCalendar(calendar);
            controller.setEvent(currEvent);
            controller.setEventCollection(calendar.getEventCollection(currEvent));
            controller.setUsername(user.getName());
            controller.setCalendarUIController(this);
        }
    }

    /**
     * Show viewMemosUI
     */
    @FXML
    private void showViewMemosUI() {
        ViewMemosController controller = openGUI("viewMemos.fxml");
        controller.setCalendar(calendar);
    }

    /**
     * Show viewTagsUI
     */
    @FXML
    private void showViewTagsUI() {
        ViewTagsController controller = openGUI("viewTags.fxml");
        controller.setCalendar(calendar);
    }

    /**
     * Handle when the switch calendar button is clicked
     */
    @FXML
    private void handleSwitchCalendar() {
        CalendarSwitcherController controller = showGUI("calendarSwitcher.fxml");
        controller.setUser(user);
    }

    /**
     * Handle logout button clicked
     *
     * @throws IOException Error saving user last log out time
     */
    @FXML
    private void handleLogout() throws IOException {
        user.logout();
        LoginController loginController = showGUI("login.fxml");
        loginController.setDarkTheme();
    }

}
