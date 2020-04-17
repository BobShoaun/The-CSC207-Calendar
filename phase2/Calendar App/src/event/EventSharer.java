package event;

import exceptions.InvalidCalendarNameException;
import exceptions.InvalidUsernameException;
import user.Calendar;
import user.User;
import user.UserManager;


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
     * @throws InvalidCalendarNameException if the calendarName passed is invalid
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

        recipientCal.getManualEventCollection().addEvent(newEvent);
        recipientCal.getDataSaver().saveEvents(recipientCal.getEventManager());
    }
}
