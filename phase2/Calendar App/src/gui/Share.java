package gui;

import entities.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import user.UserManager;

import java.net.URL;
import java.util.ResourceBundle;

public class Share extends GraphicalUserInterface implements Initializable{
    Event event;
    UserManager userManger;
    String recipientUsername;
    String recipientCalendar;
    Calendar calendarController;

    @FXML
    protected Button Username;

    @FXML
    protected Button CalendarName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setCalendarController(Calendar calendarController){
        this.calendarController = calendarController;
    }

    public void setEvent(Event event){
        this.event = event;
    }

    public void setUserManger(UserManager userManger){
        this.userManger = userManger;
    }

    public void setRecipientCalendar(){
        this.recipientCalendar = CalendarName.getText();
    }

    public void setRecipientUsername(){
        this.recipientUsername = Username.getText();
    }

    public void ShareWith(){
        try {
            userManger.getEventSharer().share(event, recipientUsername, recipientCalendar);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
