import exceptions.InvalidDateException;

import java.util.GregorianCalendar;

public class EventCollectionUI extends UserInterface {
    private EventCollection events;

    public EventCollectionUI(EventCollection eventCollection) {
        this.events = eventCollection;
    }

    @Override
    public void display() {
        System.out.println(this.events);
    }

    @Override
    public void show() {
        display();
        int option = getOptionsInput(new String[]{"Exit",
                "Display in Time frame",});
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
//            case 2:
//                String eventName = getStringInput("Enter the name of the event ou want to edit");
//                Event edit = events.
//                break;
            default:
        }
    }

//    private void regularEventsMenu()
//    {
//        int option = getOptionsInput(new String[]{"Exit",
//                "Display in Time frame",
//                "Edit an Event in Series"});
//        switch (option)
//        {
//            case 0:
//                break;
//            case 1:
//                break;
//            case 2:
//        }
//    }

//    private List<Event> selectEvents()
//    {
//        int option = getOptionsInput(this.events.regularEventDetails());
//        return null;
//    }
//
//    private Event createEvent() throws InvalidDateException
//    {
//        String name = getStringInput("Base Event Name:");
//        GregorianCalendar start = getDateInput("Start Date of Base Event");
//        GregorianCalendar end = getDateInput("End Date of Base Event");
//        return new Event(name + start.getTime(), name, start, end);
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
