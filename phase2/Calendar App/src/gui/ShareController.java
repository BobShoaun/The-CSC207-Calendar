package gui;

import event.Event;
import exceptions.InvalidCalendarNameException;
import exceptions.InvalidUsernameException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import user.UserManager;

/**
 * GUI controller class for sharing events between users.
 */
public class ShareController extends GraphicalUserInterface {

    private Event event;
    private UserManager userManger;

    @FXML
    private Label usernameError;
    @FXML
    private Label calendarNameError;
    @FXML
    private TextField username;
    @FXML
    private TextField calendarName;

    /**
     * Set the Event to be shared.
     *
     * @param event Event to share
     */
    public void setEvent(Event event) {
        this.event = event;
    }

    /**
     * Set the UserManager for saving/loading and users
     *
     * @param userManger UserManager to set
     */
    public void setUserManger(UserManager userManger) {
        this.userManger = userManger;
    }

    /**
     * Close the window.
     */
    public void exit() {
        closeGUI();
    }

    /**
     * Share the event with the set user.
     */
    public void shareWith() {
        calendarNameError.setOpacity(0);
        usernameError.setOpacity(0);
        String recipientUsername = username.getText();
        String recipientCalendar = calendarName.getText();
        try {
            userManger.getEventSharer().share(event, recipientUsername, recipientCalendar);
            closeGUI();
        } catch (InvalidCalendarNameException e) {
            calendarNameError.setOpacity(1);
        } catch (InvalidUsernameException e) {
            usernameError.setOpacity(1);
        }
    }

}