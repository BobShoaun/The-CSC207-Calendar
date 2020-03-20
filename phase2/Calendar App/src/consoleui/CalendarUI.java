package consoleui;

import entities.Alert;
import entities.Event;
import entities.EventCollection;
import exceptions.InvalidDateException;
import mt.Memo;
import mt.Tag;
import user.Calendar;
import user.DataSaver;
import user.User;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class CalendarUI extends UserInterface {
    private User user;
    private Calendar calendar;

    List<Alert> visibleAlerts = new ArrayList<>();
    List<EventUI> visibleEvents = new ArrayList<>();
    List<EventCollectionUI> visibleEventCollections = new ArrayList<>();

    public CalendarUI(User user, Calendar calendar) {
        this.user = user;
        this.calendar = calendar;
    }

    @Override
    public void display() {
        System.out.println("======= " + user.getName() + "'s " + calendar.getName() + " =======");
        displayAlerts();
        displayEvents();
    }

    private void displayAlerts() {
        System.out.println("New alerts: ");
        for (int i = 0; i < visibleAlerts.size(); i++) {
            Alert alert = visibleAlerts.get(i);
            Event correspondingEvent = calendar.getEvent(alert.getEventId());
            System.out.println("(" + i + ") " + alert.getTime().getTime().toString()
                    + " - Alert for " + correspondingEvent.getName());
        }
    }

    private void displayEvents() {
        System.out.println("Your events:");
        for (int i = 0; i < visibleEvents.size(); i++) {
            Event event = visibleEvents.get(i).getEvent();
            System.out.println("(" + i + ") " + event.toString());
        }
        if (visibleEvents.size() == 0) {
            System.out.println("No events!");
        }
    }

    @Override
    public void show() {
        visibleAlerts = calendar.getAlerts(user.getLastLoginTime(), calendar.getTime());
        calendar.removeOldAlerts();
        getEventsToday();
        while (true) {
            display();
            int command = getOptionsInput(new String[]{"Back",
                    "Show events",
                    "View event",
                    "Delete event",
                    "View memos",
                    "View memo",
                    "Delete memo",
                    "Add event",
                    "Add event series",
                    "Search events",
                    "Show all events",
                    "List all tags",
                    "List all event series",
                    "Edit event series"});
            switch (command) {
                case 0:
                    return;
                case 1:
                    showEvents();
                    break;
                case 2:
                    viewEvent();
                    break;
                case 3:
                    deleteEvent();
                    break;
                case 4:
                    viewMemos();
                    break;
                case 5:
                    viewMemo();
                    break;
                case 6:
                    removeMemo();
                    break;
                case 7:
                    addEvent();
                    break;
                case 8:
                    addEventSeries();
                    break;
                case 9:
                    search();
                    break;
                case 10:
                    showAllEvents();
                    break;
                case 11:
                    showAllTags();
                    break;
                default:
                    throw new UnsupportedOperationException();
                case 12:
                    showAllEventSeries();
                    break;
                case 13:
                    editEventSeries();
                    break;
            }
        }
    }

    private void getEventsToday() {
        GregorianCalendar startOfDay = calendar.getTime();
        startOfDay.set(startOfDay.get(java.util.Calendar.YEAR), startOfDay.get(java.util.Calendar.MONTH), startOfDay.get(java.util.Calendar.DATE), 0, 0);
        GregorianCalendar nextDay = (GregorianCalendar) startOfDay.clone();
        nextDay.add(java.util.Calendar.DATE, 1);
        getVisibleEvents(startOfDay, nextDay);
    }

    private void editEventSeries() {
        if (visibleEventCollections.size() == 0) {
            System.out.println("No event collections have been displayed so far");
        }
        int eventCollectionNum = getIntInput("Relative event collection id: ", 0, visibleEventCollections.size() - 1);
        visibleEventCollections.get(eventCollectionNum).show();
    }

    private void showAllEventSeries() {
        List<EventCollection> eventCollections = calendar.getEventCollections();
        System.out.println("Event series:");
        int i = 0;
        for (EventCollection eC : eventCollections) {
            if (!(eC.getName().equals("noname") || eC.getName().equals(""))) {
                System.out.println("(" + i + ") " + eC.getName());
                i += 1;
            }
        }
        visibleEventCollections = eventCollections.stream().filter(eC -> !eC.getName().equals(""))
                .map(eC -> new EventCollectionUI(eC, calendar)).collect(Collectors.toList());
        System.out.println("< End of series >");
    }

    private void showAllTags() {
        List<Tag> tags = calendar.getTags();
        System.out.println("Tags:");
        for (Tag t :
                tags) {
            System.out.println(t.getText());
        }
        System.out.println("----");
    }

    private void showAllEvents() {
        Iterator<Event> events = calendar.getEvents(new Date(0, 1, 1), new Date(1000, 1, 1)).iterator();
        ListUIView<Event> listUIView = new ListUIView<>(events, Event::toString, visibleEvents.size());
        listUIView.show();
    }

    private void search() {
        ListUIView<Event> listUIView = null;
        Iterator<Event> events;
        int searchOption = getOptionsInput(new String[]{"Exit", "Event name", "Memo title", "Event series", "Date", "Tag"});
        switch (searchOption) {
            case 0:
                return;
            case 1:
                String eventName = getStringInput("Event name: ");
                events = calendar.getEvents(eventName);
                listUIView = new ListUIView<>(events, Event::toString, visibleEvents.size());
                listUIView.show();
                break;
            case 2:
                String memoName = getStringInput("Memo name: ");
                Memo memo = calendar.getMemo(memoName);
                if (memo == null) {
                    System.out.println("Memo not found!");
                    break;
                }
                events = calendar.getLinkedEvents(memo).iterator();
                listUIView = new ListUIView<>(events, Event::toString, visibleEvents.size());
                listUIView.show();
                break;
            case 3:
                String eventSeriesName = getStringInput("Event series: ");
                if (calendar.getEventCollection(eventSeriesName) == null) {
                    System.out.println("That event series does not exist!");
                    break;
                }
                events = calendar.getEventCollection(eventSeriesName).getEventIterator(new Date(0));
                listUIView = new ListUIView<>(events, Event::toString, visibleEvents.size());
                listUIView.show();
                break;
            case 4:
                GregorianCalendar date = getDateInput("Events at time: ");
                events = calendar.getEvents(date.getTime()).iterator();
                listUIView = new ListUIView<>(events, Event::toString, visibleEvents.size());
                listUIView.show();
                break;
            case 5:
                String tag = getStringInput("Tag: ");
                if (calendar.getTag(tag) == null) {
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
        if (listUIView != null) {
            visibleEvents.addAll(listUIView.getElementsShown().stream()
                    .map(e -> new EventUI(e, calendar, new DataSaver(user.getName()))).collect(Collectors.toList()));
        }
    }

    private void addEventSeries() {
        String eventSeriesName = getStringInput("Name of event series: ", calendar.getEventSeriesNames());
        try {
            calendar.createEventSeries(eventSeriesName, new ArrayList<>());
        } catch (IOException e) {
            System.out.println("Error saving events!");
            e.printStackTrace();
        }
        EventCollectionUI eventCollectionUI = new EventCollectionUI(calendar.getEventCollection(eventSeriesName), calendar);
        eventCollectionUI.show();
    }

    private void addEvent() {
        GregorianCalendar time = calendar.getTime();
        GregorianCalendar endTime = (GregorianCalendar) time.clone();
        endTime.roll(6, 1);
        String eventName = getStringInput("Name of new event: ");
        Event event = null;
        try {
            event = new Event(time.getTime().toString() + eventName, eventName, time, endTime);
        } catch (InvalidDateException e) {
            System.out.println("Creating event failed... Try again!");
        }
        try {
            calendar.createEvent(event, "");
        } catch (IOException e) {
            System.out.println("Error saving events!");
            e.printStackTrace();
        }
        EventUI newEventUi = new EventUI(event, calendar, new DataSaver(user.getName()));
        newEventUi.show();
    }

    private void removeMemo() {
        String memoName = getStringInput("Memo name:");
        Memo memo = calendar.getMemo(memoName);
        if (memo == null) {
            System.out.println("Memo not found!");
            return;
        }
        calendar.removeMemo(calendar.getMemo(memoName));
    }

    private void viewMemo() {
        String memoName = getStringInput("Memo name:");
        Memo memo = calendar.getMemo(memoName);
        if (memo == null) {
            System.out.println("Memo not found!");
            return;
        }
        MemoUI memoUI = new MemoUI(memo, calendar);
        memoUI.show();
    }

    private void viewMemos() {
        ListUIView<Memo> memoUiView = new ListUIView<>(calendar.getMemos().iterator(), Memo::getTitle, 0);
        memoUiView.show();
    }

    private void deleteEvent() {
        int relativeId = getIntInput("Enter the event number (relative id): ", 0, visibleEvents.size());
        try {
            calendar.removeEvent(visibleEvents.get(relativeId).getEvent());
            visibleEventCollections.clear();
            getEventsToday();
        } catch (InvalidDateException e) {
            System.out.println("Internal error when removing the event!");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error saving events!");
            e.printStackTrace();
        }
    }

    private void viewEvent() {
        if (visibleEvents.size() == 0) {
            System.out.println("You have no events!");
            return;
        }
        int relativeId = getIntInput("Relative id: ", 0, visibleEvents.size() - 1);
        EventUI eventUI = visibleEvents.get(relativeId);
        eventUI.show();
    }

    private void showEvents() {
        GregorianCalendar start = getDateInput("Start date: ");
        GregorianCalendar end = getDateInput("End date: ", true);
        if (end != null) {
            getVisibleEvents(start, end);
            displayEvents();
        } else {
            Iterator<Event> eventIterator = calendar.getFutureEvents(start.getTime());
            ListUIView<Event> eventIteratorView = new ListUIView<>(eventIterator, Event::toString, visibleEvents.size());
            eventIteratorView.show();
        }
    }

    private void getVisibleEvents(GregorianCalendar startOfDay, GregorianCalendar nextDay) {
        visibleEvents = calendar.getEvents(startOfDay.getTime(),
                nextDay.getTime()).stream().map(e -> new EventUI(e, calendar, new DataSaver(user.getName()))).collect(Collectors.toList());
    }
}
