package gui;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import user.Calendar;
import user.User;

public class CalendarSwitcher extends GraphicalUserInterface {

    private User user;

    @FXML private ListView<String> calendarListView;

    public void setUser (User user) {
        this.user = user;
        for (Calendar calendar : user.getCalendars()) {
            calendarListView.getItems().add(calendar.getName());
        }
    }

}