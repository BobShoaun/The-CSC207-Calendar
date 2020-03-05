import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;
import java.util.stream.Collectors;

public class CalendarUI extends UserInterface{
    private User user;
    private Calendar calendar;

    List<Alert> visibleAlerts = new ArrayList<>();
    List<EventUI> visibleEvents = new ArrayList<>();

    public CalendarUI(User user, Calendar calendar){
        this.user = user;

        this.calendar = calendar;
    }

    @Override
    public void display() {
        //Show current alerts
        System.out.println("======= " + user.getName() + "'s Calendar =======");
        System.out.println("New alerts: ");
        for (int i = 0; i < visibleAlerts.size(); i++) {
            Alert alert = visibleAlerts.get(i);
            Event correspondingEvent = calendar.getEvent(alert.getEventId());
            System.out.println("(" + i + ") " + alert.getTime().toString() + " - Alert for " + correspondingEvent.getName());
        }
        displayEvents();
    }

    private void displayEvents() {
        System.out.println("Your events:");
        for (int i = 0; i < visibleEvents.size(); i++) {
            Event event = visibleEvents.get(i).getEvent();
            System.out.println("(" + i + ") " +  event.getName() + " at " + event.getStartDate() + " for " + event.getDuration());
        }
    }

    @Override
    public void show() {
        visibleAlerts = calendar.getAlerts(user.getLastLoginTime(), calendar.getTime());
        GregorianCalendar startOfDay = calendar.getTime();
        startOfDay.set(startOfDay.get(java.util.Calendar.YEAR), startOfDay.get(java.util.Calendar.MONTH), startOfDay.get(java.util.Calendar.DATE), 0, 0);
        GregorianCalendar nextDay = (GregorianCalendar)startOfDay.clone();
        nextDay.roll(java.util.Calendar.DATE, true);
        getVisibleEvents(startOfDay, nextDay);
        while (true){
            display();
            int command = getOptionsInput(new String[]{"Exit", "Show events", "View event"});
            switch (command){
                case 0:
                    return;
                case 1:
                    GregorianCalendar start = getDateInput("Start date: ");
                    GregorianCalendar end = getDateInput("End date: ", true);
                    if(end != null){
                        getVisibleEvents(start, end);
                        displayEvents();
                    } else{
                        Iterator<Event> eventIterator = calendar.getFutureEvents(start.getTime());
                        while (true){
                            int startInt = visibleEvents.size();
                            for (int i = 0; i < 10; i++) {
                                Event event = eventIterator.next();
                                if(event == null)
                                    break;
                                visibleEvents.add(new EventUI(event));
                            }
                            for (int i = startInt; i < visibleEvents.size(); i++) {
                                Event event = visibleEvents.get(i).getEvent();
                                System.out.println("(" + i + ") " +  event.getName() + " at " + event.getStartDate() + " for " + event.getDuration());
                            }
                            command = getOptionsInput(new String[]{"Stop", "Finish"});
                            if(command == 0){
                                break;
                            }
                        }
                    }
                    break;
                case 2:
                    int relativeId = getIntInput("Relative id:", 0, visibleEvents.size());
                    EventUI eventUI = visibleEvents.get(relativeId);
                    eventUI.show();
                default:
                    throw new NotImplementedException();
            }
        }
    }

    private void getVisibleEvents(GregorianCalendar startOfDay, GregorianCalendar nextDay) {
        visibleEvents = calendar.getEvents(startOfDay.getTime(), nextDay.getTime()).stream().map(EventUI::new).collect(Collectors.toList());
    }
}
