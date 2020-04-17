package gui;

import alert.Alert;
import event.Event;
import event.Series;
import event.SubSeries;
import exceptions.NoSuchSeriesException;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
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
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * GUI controller class for the main calendar GUI
 */
public class CalendarUI extends GraphicalUserInterface {

    private User user;
    private user.Calendar calendar;
    private Event currEvent;
    private String currAlert;
    private Series currSeries = null;
    private String currSubSeries = null;
    private UserManager userManager;
    private int subSeriesIndex;
    private ObservableList<String> seriesNameList;

    @FXML private ListView<String> alertList;
    @FXML private ListView<String> displayedSubSeriesList;
    @FXML private ListView<String> displayedSeriesList;
    @FXML
    private CheckBox darkTheme;
    @FXML
    private ChoiceBox<String> searchByList;
    @FXML
    private DatePicker startDate;
    @FXML private DatePicker endDate;
    @FXML private TextField searchTermField;
    @FXML private ListView<String> displayedEventList;
    @FXML private Label lastLoginLabel;
    @FXML private Label eventErrorLabel;

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
                Stream.of("Date", "Tag", "Memo name", "Postponed").collect(Collectors.toList())
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

        // TODO: refactor this into a separate method
        displayedEventList.setOnMouseClicked(event -> {
            System.out.println("Clicked on event at id: " + displayedEventList.getSelectionModel().getSelectedIndex());
            if (displayedEventList.getSelectionModel().getSelectedIndex() != -1) {
                currEvent = eventList.get(displayedEventList.getSelectionModel().getSelectedIndex());
            }
        });
        updateDisplayedEvents();
        updateDisplayedSeries();
        updateDisplayedSubSeries();
    }

    protected void updateDisplayedSeries() {
        seriesNameList.clear();
        for (Series series : calendar.getSeries()) {
            seriesNameList.add(series.getName());
        }
    }

    protected void updateDisplayedSubSeries() {
        ArrayList<String> stringSubSeries = new ArrayList<>();
        if (currSeries != null) {
            for (SubSeries subSeries : currSeries.getSubSeries()) {
                stringSubSeries.add(subSeries.getString());
            }
        }
        displayedSubSeriesList.setItems(FXCollections.observableArrayList(stringSubSeries));
    }

    /*protected void updateDisplayedSeriesEvents() {
        ArrayList<String> events = new ArrayList<>();
        if (currSeries != null) {
            for (Event event : currSeries) {
                events.add(event.toString());
            }
            displayedSubSeriesList.setItems(FXCollections.observableArrayList(events));
        }
    }*/

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    /**
     * Update the displayed events from search
     */
    @FXML
    protected void updateDisplayedEvents() {
//        System.out.println(calendar.getSeries().size());
//        for (Series s:calendar.getSeries()) {
//            System.out.println(s);
//        }
        String searchCriterion = searchByList.getValue();
        if (searchCriterion == null) {
            return;
        }
        eventList.clear();
        switch (searchCriterion) {
            case "Date":
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
                break;
            case "Tag":
                String tagString = searchTermField.getText();
                Tag tag = calendar.getTag(tagString);
                if (tag != null) {
                    List<Event> events = tag.getEvents().stream().map(e -> calendar.getEvent(e)).collect(Collectors.toList());
                    eventList.addAll(events);
                }
                break;
            case "Memo name":
                String memoString = searchTermField.getText();
                Memo memo = calendar.getMemo(memoString);
                if (memo != null) {
                    List<Event> events = memo.getEvents().stream().map(e -> calendar.getEvent(e)).collect(Collectors.toList());
                    eventList.addAll(events);
                }
                break;
            case "Postponed":
                eventList.addAll(calendar.getPostponedEvents());
                break;
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
        updateDisplayedAlerts();

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
        updateDisplayedEvents();
    }

    /**
     * Update the visible alerts
     */
    protected void updateDisplayedAlerts() {
        GregorianCalendar past = calendar.getTime();
        past.add(GregorianCalendar.DATE, -30);
        ObservableList<String> alertStrings = FXCollections.observableArrayList(
                calendar.getAlerts(past, calendar.getTime()).stream()
                        .map(alert -> "For " + calendar.getEvent(alert.getEventId()).getName() + " at " + alert.getTime().getTime().toString())
                        .collect(Collectors.toList()));
        alertList.setItems(alertStrings);
    }

//    /**
//     * Handles when the event list is clicked
//     */
//    @FXML
//    private void eventListClicked() {
//        currEvent = displayedEventList.getSelectionModel().getSelectedItem();
//        System.out.println("Clicked on alert: " + currEvent);
//    }

    /**
     * Handles when the alert list is clicked, displays information about current alert
     */
    @FXML
    private void alertListClicked() {
        currAlert = alertList.getSelectionModel().getSelectedItems().get(0);
        System.out.println("Clicked on alert: " + currAlert);
        updateDisplayedEvents();
        updateDisplayedAlerts();
    }

    /**
     * Handles when the series list is clicked
     */
    @FXML
    private void seriesListClicked() {
        String stringSeries = displayedSeriesList.getSelectionModel().getSelectedItem();
        System.out.println("Clicked on series: " + stringSeries);
        try {
            currSeries = calendar.getSeries(stringSeries);
        } catch (NoSuchSeriesException e){
            System.out.println("Series does not exist: " + stringSeries);
        }
        currSubSeries = null;
        updateDisplayedSeries();
        updateDisplayedSubSeries();
        //updateDisplayedSeriesEvents();
    }

    @FXML
    private void deleteSeries() {
        if (currSeries != null) {
            calendar.removeEventCollection(currSeries);
        }
    }

    /**
     * Handles when an event in a series is clicked
     */
    @FXML
    private void subSeriesListClicked() {
        subSeriesIndex = displayedSubSeriesList.getSelectionModel().getSelectedIndex();
        System.out.println("Clicked on sub series: " + currSubSeries);
        updateDisplayedSubSeries();
    }

    @FXML
    private void deleteSubSeries() {
        if (currSubSeries != null) {
            currSeries.getSubSeries().remove(subSeriesIndex);
        }
    }

    /**
     * Handles when the time button is clicked and shows the time controller window
     *
     * @param mouseEvent The event information of the click
     */
    public void showTimeController(MouseEvent mouseEvent) {
        Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
        TimeController timeController = new TimeController();
        timeController.setCalendar(calendar);
        timeController.start(stage);
    }

    /**
     * Clear the selected alert, if none selected, nothing happens
     */
    @FXML
    private void clearNotification() {
        System.out.println("Clicked clear");
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
        System.out.println("Clicked clear all");
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
        updateDisplayedEvents();
    }

    /**
     * Handle when a new event button is clicked
     */
    @FXML
    private void handleNewEvent() {
        System.out.println(calendar.getSeries().size());
        System.out.println("New clicked");
        EventAddUI controller = openGUI("EventAddUI.fxml");
        controller.setCalendar(calendar);
        controller.setCalendarUIController(this);
    }

    /**
     * Handle when the edit button is clicked
     */
    @FXML
    private void handleEditEvent() {
        System.out.println("Edit Clicked");

        if (currEvent == null) {
            eventErrorLabel.setText("No Event has been selected");
            eventErrorLabel.setVisible(true);
        } else {
            eventErrorLabel.setVisible(false);
            EventEditUI controller = openGUI("EventEditUI.fxml");
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
        System.out.println("Memos clicked");
        ViewMemosUI controller = openGUI("viewMemos.fxml");
        controller.setCalendar(calendar);
    }

    /**
     * Show viewTagsUI
     */
    @FXML
    private void showViewTagsUI() {
        System.out.println("Tags clicked");
        ViewTagsUI controller = openGUI("viewTags.fxml");
        controller.setCalendar(calendar);
    }

    /**
     * Handle when the switch calendar button is clicked
     */
    @FXML
    private void handleSwitchCalendar() {
        CalendarSwitcherUI controller = showGUI("calendarSwitcher.fxml");
        controller.setUser(user);
    }

    /**
     * Handle logout button clicked
     *
     * @throws IOException Error saving user last log out time
     */
    @FXML
    private void handleLogout() throws IOException {
        System.out.println("logout: " + user.getName());
        user.logout();
        LoginUI loginUI = showGUI("login.fxml");
        loginUI.setDarkTheme();
    }

}
