package gui;

import entities.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import user.UserManager;

public class Share extends GraphicalUserInterface{
    Event event;
    UserManager userManger;
    String recipientUsername;
    String recipientCalendar;

    @FXML
    protected Button Username;

    @FXML
    protected Button CalendarName;

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
        closeGUI();
    }
}
