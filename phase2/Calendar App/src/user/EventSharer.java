package user;

import entities.Event;
import exceptions.InvalidCalendarNameException;
import exceptions.InvalidUsernameException;
import java.io.IOException;


public class EventSharer {
    private UserManager userManager;

    public EventSharer(UserManager userManager){
        this.userManager = userManager;
    }

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
