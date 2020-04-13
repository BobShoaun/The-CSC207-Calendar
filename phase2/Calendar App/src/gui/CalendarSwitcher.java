package gui;

import exceptions.InvalidDateException;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import user.Calendar;
import user.User;

import javax.naming.InvalidNameException;

public class CalendarSwitcher extends GraphicalUserInterface {

    private User user;

    @FXML
    private ListView<String> calendarListView;
    @FXML
    private TextField calendarNameField;

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
    private void handleOK () throws InvalidDateException {
        int index = calendarListView.getSelectionModel().getSelectedIndex();
        gui.Calendar cal = showGUI("calendar.fxml");
        cal.setUser(user);
        if (index != -1) //something is selected
            cal.setActiveCalendar(user.getCalendar(index));
    }

    @FXML
    private void handleCancel () throws InvalidDateException {
        gui.Calendar cal = showGUI("calendar.fxml");
        cal.setUser(user);
    }

    @FXML
    private void handleAddCalendar () {
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
            System.out.println("Not allowed to delete the last calendar");
            return;
        }
        int index = calendarListView.getSelectionModel().getSelectedIndex();
        user.removeCalendar(index);
        updateCalendarList();
    }

}