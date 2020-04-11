package gui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class CalendarSwitcher {

    @FXML private ListView<String> calendarListView;

    public void initialize () {
        calendarListView.getItems().addAll("Ehllo", "edfadf");

    }

}
