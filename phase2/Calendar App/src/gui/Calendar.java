package gui;

import entities.Alert;
import entities.Event;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
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

/**
 * GUI controller class for the main calendar GUI
 */
public class Calendar extends GraphicalUserInterface {

    private User user;
    private user.Calendar calendar;
    private Event currEvent;
    private String currAlert;
    private String currSeries;
    private String currSeriesEvent;

    @FXML private ListView<String> alertList;
    @FXML private ListView<String> seriesEventList;
    @FXML private ListView<String> seriesList;
    @FXML private CheckBox darkTheme;
    @FXML private ChoiceBox searchByList;
    @FXML private DatePicker startDate;
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

        searchByList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            String searchCriterion = (String) newValue;
            if (searchCriterion.equals("Date")) {
                startDate.setVisible(true);
                GregorianCalendar time = calendar.getTime();
                LocalDate date = LocalDate.of(time.get(GregorianCalendar.YEAR), time.get(GregorianCalendar.MONTH) + 1,
                        time.get(GregorianCalendar.DATE));
                startDate.setValue(date);
                LocalDate end = LocalDate.of(time.get(GregorianCalendar.YEAR), time.get(GregorianCalendar.MONTH) + 1,
                        time.get(GregorianCalendar.DATE) + 14);
                endDate.setVisible(true);
                endDate.setValue(end);
                searchTermField.setVisible(false);
            } else if (searchCriterion.equals("Tag")) {
                startDate.setVisible(false);
                endDate.setVisible(false);
                searchTermField.setVisible(true);
            } else if (searchCriterion.equals("Memo name")) {
                startDate.setVisible(false);
                endDate.setVisible(false);
                searchTermField.setVisible(true);
            } else if (searchCriterion.equals("Postponed")) {
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
            updateDisplayedEvents();
        });
        endDate.valueProperty().addListener((time) -> {
            updateDisplayedEvents();
        });

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
    }

    /**
     * Update the displayed events from search
     */
    @FXML
    protected void updateDisplayedEvents() {
        String searchCriterion = (String) searchByList.getValue();
        if (searchCriterion == null) {
            return;
        }
        eventList.clear();
        if (searchCriterion.equals("Date")) {
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
                    List<entities.Event> events = calendar.getEvents(start, end);
                    eventList.addAll(events);
                }
            }
        } else if (searchCriterion.equals("Tag")) {
            String tagString = searchTermField.getText();
            Tag tag = calendar.getTag(tagString);
            if (tag != null) {
                List<entities.Event> events = tag.getEvents().stream().map(e -> calendar.getEvent(e)).collect(Collectors.toList());
                eventList.addAll(events);
            }
        } else if (searchCriterion.equals("Memo name")) {
            String memoString = searchTermField.getText();
            Memo memo = calendar.getMemo(memoString);
            if (memo != null) {
                List<entities.Event> events = memo.getEvents().stream().map(e -> calendar.getEvent(e)).collect(Collectors.toList());
                eventList.addAll(events);
            }
        } else if (searchCriterion.equals("Postponed")) {
            eventList.addAll(calendar.getPostponedEvents());
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
        updateAlerts();

        setTheme();
        lastLoginLabel.setText("Last login on: " + user.getLastLoginTime().getTime());
    }

    /**
     * Set the calendar to be displayed
     *
     * @param calendar
     */
    public void setActiveCalendar(user.Calendar calendar) {
        this.calendar = calendar;
        setWindowTitle(calendar.getName());
        updateDisplayedEvents();
    }

    /**
     * Update the visible alerts
     */
    private void updateAlerts() {
        GregorianCalendar past = calendar.getTime();
        past.add(GregorianCalendar.DATE, -1);
        ObservableList<String> alertStrings = FXCollections.observableArrayList(
                calendar.getAlerts(calendar.getTime(), past).stream()
                        .map(Alert::toString).collect(Collectors.toList()));
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
        updateAlerts();
    }

    /**
     * Handles when the series list is clicked
     */
    @FXML
    private void seriesListClicked() {
        currSeries = seriesList.getSelectionModel().getSelectedItem();
        System.out.println("Clicked on series: " + currSeries);
        //TODO: Actually do something
    }

    /**
     * Handles when an event in a series is clicked
     */
    @FXML
    private void seriesEventClicked() {
        currSeriesEvent = alertList.getSelectionModel().getSelectedItem();
        System.out.println("Clicked on series event: " + currSeriesEvent);
        //TODO: Actually do something
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
            ObservableList<String> temp = alertList.getItems();
            temp.remove(currAlert);
            alertList.setItems(temp);
        } // TODO: actually delete the Alert?
    }

    /**
     * Clear the notifications
     */
    @FXML
    private void clearAllNotifications() {
        System.out.println("Clicked clear all");
        ObservableList<String> empty = FXCollections.observableArrayList();
        alertList.setItems(empty);
        // TODO: delete all shown Alerts
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
    private void setTheme() {
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
     *
     * @param keyEvent Event from new key
     */
    public void searchTermValueChange(KeyEvent keyEvent) {
        updateDisplayedEvents();
    }

    /**
     * Handle when a new event button is clicked
     */
    @FXML
    private void handleNewEvent() {
        System.out.println("New clicked");
        EventAddUI controller = openGUI("EventAddUI.fxml");
        controller.setCalendar(calendar);
        controller.setCalendarController(this);
    }

    /**
     * Handle when the edit button is clicked
     */
    //TODO: Is this still needed with how event list works?
    @FXML
    private void handleEditEvent() {
        System.out.println("Edit Clicked");

        if (currEvent == null) {
            eventErrorLabel.setText("No Event has been selected");
            eventErrorLabel.setVisible(true);
        } else {
            eventErrorLabel.setVisible(false);
            EventEditUI controller = openGUI("EventEditUI.fxml");
            controller.setEvent(currEvent);
            controller.setCalendar(calendar);
            controller.showEventDetails(currEvent);
            controller.setUsername(user.getName());
            controller.setCalendarController(this);
        }

        //TODO: handle no event is selected

    }

    /**
     * Show viewMemosUI
     */
    @FXML
    private void showViewMemosUI() {
        System.out.println("Memos clicked");

        ViewMemos controller = showGUI("viewMemos.fxml");
        controller.setCalendar(calendar);
    }

    /**
     * Show viewTagsUI
     */
    @FXML
    private void showViewTagsUI() {
        System.out.println("Tags clicked");

        ViewTags controller = showGUI("viewTags.fxml");
        controller.setCalendar(calendar);
    }

    /**
     * Handle when the switch calendar button is clicked
     */
    @FXML
    private void handleSwitchCalendar() {
        CalendarSwitcher controller = showGUI("calendarSwitcher.fxml");
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
        Login login = showGUI("login.fxml");
        login.setDarkTheme();
    }
}
