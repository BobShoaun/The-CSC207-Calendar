package user;

import entities.Event;
import exceptions.InvalidCalendarNameException;
import exceptions.InvalidUsernameException;
import java.io.IOException;


/**
 * EventSharer class
 */
public class EventSharer {
    private UserManager userManager;

    /**
     * Constructor for creating an EventSharer
     */
    public EventSharer(UserManager userManager){
        this.userManager = userManager;
    }

    /**
     * @param event the event being shared
     * @param recipientName the username of the user who the event is being shared with
     * @param recipientCalName the calendar name of the user with who the event is being shared calendar in which the event is added
     * @throws InvalidUsernameException if the username passed is invalid
     * @throws InvalidCalendarNameException if the username calendarnName is invalid
     */
    public void share(Event event, String recipientName, String recipientCalName) throws InvalidUsernameException, InvalidCalendarNameException {
        User recipientUser = userManager.getUser(recipientName);
        if (recipientUser == null){
            throw new InvalidUsernameException();
        }

        Calendar recipientCal = recipientUser.getCalendar(recipientCalName);
        if (recipientCal == null){
            throw new InvalidCalendarNameException();
        }

        Event newEvent = null;
        try {
            newEvent = (Event) event.clone();
        } catch (CloneNotSupportedException e){
            e.printStackTrace();
        }

        try {
            recipientCal.getEventCollection("Shared").addEvent(newEvent);
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
