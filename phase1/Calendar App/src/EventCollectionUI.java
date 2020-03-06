import exceptions.InvalidDateException;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class EventCollectionUI extends UserInterface
{
    private EventCollection events;
    private DataSaver saver;

    public EventCollectionUI(EventCollection eventCollection)
    {
        this.events = eventCollection;
    }

    @Override
    public void display()
    {
        System.out.println(this.events);
    }

    @Override
    public void show()
    {
        display();
        int option = getOptionsInput(new String[]{"Exit",
                "Display in Time frame",
                "Edit an Event in Series",});
        switch (option)
        {
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
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
