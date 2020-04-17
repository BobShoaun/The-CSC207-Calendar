package gui;

import event.Event;
import exceptions.InvalidCalendarNameException;
import exceptions.InvalidUsernameException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import user.UserManager;

public class ShareUI extends GraphicalUserInterface{
    private Event event;
    private UserManager userManger;
    private String recipientUsername;
    private String recipientCalendar;

    @FXML
    protected Label UsernameError;

    @FXML
    protected Label CalendarNameError;

    @FXML
    protected TextField Username;

    @FXML
    protected TextField CalendarName;

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

    public void exit(){
        closeGUI();
    }

    public void ShareWith(){
        CalendarNameError.setOpacity(0);
        UsernameError.setOpacity(0);
        try {
            userManger.getEventSharer().share(event, recipientUsername, recipientCalendar);
            closeGUI();
        } catch (InvalidCalendarNameException e) {
            CalendarNameError.setOpacity(1);
        } catch (InvalidUsernameException e){
            UsernameError.setOpacity(1);
        }
    }
}
