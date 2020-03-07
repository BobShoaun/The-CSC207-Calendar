package consoleui;

import event.Event;
import event.EventCollection;
import exceptions.InvalidDateException;
import user.Calendar;

import java.io.IOException;
import java.util.GregorianCalendar;

public class EventCollectionUI extends UserInterface {
    private EventCollection events;
    private user.Calendar cal;

    public EventCollectionUI(EventCollection eventCollection, Calendar cal) {
        this.events = eventCollection;
        this.cal = cal;
    }

    @Override
    public void display() {
        System.out.println(this.events);
    }

    @Override
    public void show() {
        display();
        int option = getOptionsInput(new String[]{"Exit",
                "Display in Time frame",
                "Create an series manually,",
                "Create an repeating series "});
//                "Edit an Event in Series",});
        switch (option) {
            case 0:
                break;
            case 1:
                GregorianCalendar start = getDateInput("Select a Start Time frame");
                GregorianCalendar end = getDateInput("Select a End Time Frame");
                try {
                    for (Event e:events.getEventsBetween(start,end)) {
                        System.out.println(e.toString());
                    }
                } catch (InvalidDateException e) {
                    System.out.println("Invalid date input");
                }
                break;
            case 2:
                String name = getStringInput("name of the series:");
                EventCollection regularEvents = cal.getEventCollection(null);

                int option1 = getOptionsInput(regularEvents.getEventOptions());
                String eventId = regularEvents.getEvents().get(option1-1).getId();

                while(option1!=0)
                {
                    try {
                        //keep asking for events to select till exit at 0
                        cal.addToSeries(eventId, name);
                        option1 = getOptionsInput(regularEvents.getEventOptions());
                        eventId = regularEvents.getEvents().get(option1-1).getId();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //get ECollections find the regular one, diaplay it for choice and add that using addToSeries to a given Series name...
                break;
            case 3:
//            case 2:
//                String eventName = getStringInput("Enter the name of the event ou want to edit");
//                event.Event edit = events.
//                break;
            default:
        }
    }

//    private void regularEventsMenu()
//    {
//        int option = getOptionsInput(new String[]{"Exit",
//                "Display in Time frame",
//                "Edit an event.Event in Series"});
//        switch (option)
//        {
//            case 0:
//                break;
//            case 1:
//                break;
//            case 2:
//        }
//    }

//    private List<event.Event> selectEvents()
//    {
//        int option = getOptionsInput(this.events.regularEventDetails());
//        return null;
//    }
//
//    private event.Event createEvent() throws InvalidDateException
//    {
//        String name = getStringInput("Base event.Event Name:");
//        GregorianCalendar start = getDateInput("Start Date of Base event.Event");
//        GregorianCalendar end = getDateInput("End Date of Base event.Event");
//        return new event.Event(name + start.getTime(), name, start, end);
//    }
//
//    private List<Duration> getFrequency()
//    {
//        List<Duration> ret = new ArrayList<>();
//        int option = 1;
//        while (option != 0)
//        {
//            Duration dur = getDurationInput("Choose a the frequency of repetition for your event");
//            option = getOptionsInput(new String[]{"Exit",
//                    "Choose another frequency of repetition for your event"});
//            ret.add(dur);
//        }
//        return ret;
//    }
}
