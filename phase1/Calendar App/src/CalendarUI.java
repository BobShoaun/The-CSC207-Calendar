import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Scanner;
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
        System.out.println("Your events for today");
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
        visibleEvents = calendar.getEvents(startOfDay.getTime(), nextDay.getTime()).stream().map(EventUI::new).collect(Collectors.toList());
        while (true){
            display();
            String command = getWordInput("$:");
            switch (command){
                case "exit":
                case "..":
                    return;
            }
        }
    }
}
