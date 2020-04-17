package gui;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import user.Calendar;
import user.User;

import javax.naming.InvalidNameException;

/**
 * GUI controller for calendar switcher gui
 *
 * @author Ng Bob Shoaun
 */
public class CalendarSwitcherController extends GraphicalUserInterface {

    private User user;

    @FXML
    private ListView<String> calendarListView;
    @FXML
    private TextField calendarNameField;

    /**
     * Set the current user using the gui
     *
     * @param user
     */
    public void setUser(User user) {
        this.user = user;
        updateCalendarList();
    }

    private void updateCalendarList() {
        calendarListView.getItems().clear();
        for (Calendar calendar : user.getCalendars())
            calendarListView.getItems().add(calendar.getName());
    }

    @FXML
    private void handleOK() {
        int index = calendarListView.getSelectionModel().getSelectedIndex();
        CalendarController cal = showGUI("calendar.fxml");
        cal.setUser(user);
        if (index != -1) //something is selected
            cal.setActiveCalendar(user.getCalendar(index));
    }

    @FXML
    private void handleCancel() {
        CalendarController cal = showGUI("calendar.fxml");
        cal.setUser(user);
    }

    @FXML
    private void handleAddCalendar() {
        String newCalendarName = calendarNameField.getText();
        calendarNameField.clear();
        try {
            user.addCalendar(newCalendarName);
        } catch (InvalidNameException e) {
            System.out.println("Invalid calendar name: \"" + newCalendarName + "\"");
        }
        updateCalendarList();
    }

    @FXML
    private void handleDelete() {
        if (user.getCalendars().size() == 1) {
            return;
        }
        int index = calendarListView.getSelectionModel().getSelectedIndex();
        user.removeCalendar(index);
        updateCalendarList();
    }

}