package consoleui;

import alert.Alert;
import event.Event;
import exceptions.InvalidDateException;
import mt.Memo;
import mt.Tag;
import user.Calendar;
import user.User;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class CalendarUI extends UserInterface {
    private User user;
    private Calendar calendar;

    List<Alert> visibleAlerts = new ArrayList<>();
    List<EventUI> visibleEvents = new ArrayList<>();

    public CalendarUI(User user, Calendar calendar) {
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
            System.out.println("(" + i + ") " + alert.getTime().toString()
                    + " - Alert for " + correspondingEvent.getName());
        }
        displayEvents();
    }

    private void displayEvents() {
        System.out.println("Your events:");
        for (int i = 0; i < visibleEvents.size(); i++) {
            Event event = visibleEvents.get(i).getEvent();
            System.out.println("(" + i + ") " +  event.toString());
        }
        if(visibleEvents.size() == 0){
            System.out.println("No events!");
        }
    }

    @Override
    public void show() {
        visibleAlerts = calendar.getAlerts(user.getLastLoginTime(), calendar.getTime());
        GregorianCalendar startOfDay = calendar.getTime();
        startOfDay.set(startOfDay.get(java.util.Calendar.YEAR), startOfDay.get(java.util.Calendar.MONTH), startOfDay.get(java.util.Calendar.DATE), 0, 0);
        GregorianCalendar nextDay = (GregorianCalendar)startOfDay.clone();
        nextDay.add(java.util.Calendar.DATE, 1);
        getVisibleEvents(startOfDay, nextDay);
        while (true){
            display();
            int command = getOptionsInput(new String[]{"Logout", "Show events", "View event", "Delete event", "View memos",
                    "View memo", "Delete memo", "Add event", "Add event series", "Search events", "Show all events", "List all tags"});
            switch (command){
                case 0:
                    user.setLastLoginTime(new GregorianCalendar()); // logout
                    return;
                case 1:
                    GregorianCalendar start = getDateInput("Start date: ");
                    GregorianCalendar end = getDateInput("End date: ", true);
                    if(end != null){
                        getVisibleEvents(start, end);
                        displayEvents();
                    } else{
                        Iterator<Event> eventIterator = calendar.getFutureEvents(start.getTime());
                        ListUIView<Event> eventIteratorView = new ListUIView<>(eventIterator, Event::toString, visibleEvents.size());
                        eventIteratorView.show();
                    }
                    break;
                case 2:
                    if(visibleEvents.size() == 0){
                        System.out.println("You have no events!");
                        break;
                    }
                    int relativeId = getIntInput("Relative id:", 0, visibleEvents.size() - 1);
                    EventUI eventUI = visibleEvents.get(relativeId);
                    eventUI.show();
                    break;
                case 3:
                    relativeId = getIntInput("Relative id:", 0, visibleEvents.size());
                    try {
                        calendar.removeEvent(visibleEvents.get(relativeId).getEvent());
                    } catch (InvalidDateException e) {
                        System.out.println("Internal error when removing the event!");
                        e.printStackTrace();
                    } catch (IOException e) {
                        System.out.println("Error saving events!");
                        e.printStackTrace();
                    }
                    break;
                case 4:
                    ListUIView<Memo> memoUiView = new ListUIView<>(calendar.getMemos().iterator(), Memo::getTitle, 0);
                    memoUiView.show();
                    break;
                case 5:
                    String memoName = getStringInput("Memo name:");
                    Memo memo = calendar.getMemo(memoName);
                    if(memo == null){
                        System.out.println("Memo not found!");
                        break;
                    }
                    MemoUI memoUI = new MemoUI(memo, calendar);
                    memoUI.show();
                    break;
                case 6:
                    memoName = getStringInput("Memo name:");
                    memo = calendar.getMemo(memoName);
                    if(memo == null){
                        System.out.println("Memo not found!");
                        break;
                    }
                    calendar.removeMemo(calendar.getMemo(memoName));
                    break;
                case 7:
                    GregorianCalendar time = calendar.getTime();
                    GregorianCalendar endTime = (GregorianCalendar)time.clone();
                    endTime.roll(6, 1);
                    String eventName = getStringInput("Name of new event: ");
                    Event event = null;
                    try {
                        event = new Event(time.getTime().toString()+eventName, eventName, time, endTime);
                    } catch (InvalidDateException e) {
                        System.out.println("Creating event failed... Try again!");
                    }
                    try {
                        calendar.createEvent(event,"");
                    } catch (IOException e) {
                        System.out.println("Error saving events!");
                        e.printStackTrace();
                    }
                    EventUI newEventUi = new EventUI(event, calendar);
                    newEventUi.show();
                    break;
                case 8:
                    String eventSeriesName = getStringInput("Name of event series", calendar.getEventSeriesNames());
                    try {
                        calendar.createEventSeries(eventSeriesName, new ArrayList<>());
                    } catch (IOException e) {
                        System.out.println("Error saving events!");
                        e.printStackTrace();
                    }
                    EventCollectionUI eventCollectionUI = new EventCollectionUI(calendar.getEventCollection(eventSeriesName), calendar);
                    eventCollectionUI.show();
                case 9:
                    ListUIView<Event> listUIView = null;
                    Iterator<Event> events = null;
                    int searchOption = getOptionsInput(new String[]{"Event name", "Memo title", "Event series", "Date", "Tag"});
                    switch(searchOption){
                        case 0:
                            eventName = getStringInput("Event name:");
                            events = calendar.getEvents(eventName);
                            listUIView = new ListUIView<>(events, Event::toString, visibleEvents.size());
                            listUIView.show();
                            break;
                        case 1:
                            memoName = getStringInput("Memo name:");
                            memo = calendar.getMemo(memoName);
                            if(memo == null){
                                System.out.println("Memo not found!");
                                break;
                            }
                            events = calendar.getLinkedEvents(memo).iterator();
                            listUIView = new ListUIView<>(events, Event::toString, visibleEvents.size());
                            listUIView.show();
                            break;
                        case 2:
                            eventSeriesName = getStringInput("Event series:");
                            if(calendar.getEventCollection(eventSeriesName) == null){
                                System.out.println("That event series does not exist!");
                                break;
                            }
                            events = calendar.getEventCollection(eventSeriesName).getEventIterator(new Date(0));
                            listUIView = new ListUIView<>(events, Event::toString, visibleEvents.size());
                            listUIView.show();
                            break;
                        case 3:
                            GregorianCalendar date = getDateInput("Events at time: ");
                            events = calendar.getEvents(date.getTime()).iterator();
                            listUIView = new ListUIView<>(events, Event::toString, visibleEvents.size());
                            listUIView.show();
                            break;
                        case 4:
                            String tag = getStringInput("Tag: ");
                            if(calendar.getTag(tag) == null){
                                System.out.println("This tag cannot be found!");
                                break;
                            }
                            events = calendar.getTag(tag).getEvents().stream().map(id -> calendar.getEvent(id)).iterator();
                            listUIView = new ListUIView<>(events, Event::toString, visibleEvents.size());
                            listUIView.show();
                            break;
                        default:
                            throw new UnsupportedOperationException();
                    }
                    if(listUIView != null){
                        visibleEvents.addAll(listUIView.getElementsShown().stream().map(e -> new EventUI(e,calendar)).collect(Collectors.toList()));
                    }
                    break;
                case 10:
                    events = calendar.getEvents(new Date(0, 1, 1), new Date(1000, 1, 1)).iterator();
                    listUIView = new ListUIView<>(events, Event::toString, visibleEvents.size());
                    listUIView.show();
                    break;
                case 11:
                    List<Tag> tags = calendar.getTags();
                    System.out.println("Tags:");
                    for (Tag t :
                            tags) {
                        System.out.println(t.getText());
                    }
                    System.out.println("----");
                    break;
                default:
                    throw new UnsupportedOperationException();
            }
        }
    }

    private void getVisibleEvents(GregorianCalendar startOfDay, GregorianCalendar nextDay) {
        visibleEvents = calendar.getEvents(startOfDay.getTime(), nextDay.getTime()).stream().map(e -> new EventUI(e, calendar)).collect(Collectors.toList());
    }
}
