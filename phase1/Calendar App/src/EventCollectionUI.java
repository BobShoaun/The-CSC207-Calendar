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

    public EventCollectionUI(EventCollection eventCollection,DataSaver saver){
        this.events = eventCollection;
        this.saver = saver;
    }

    @Override
    public void display() {
        System.out.println(this.events);
    }

    @Override
    public void show() throws IOException, InvalidDateException
    {
        //not a series
        if(events == null)
        {
            regularEventsMenu();
        }else//a series
        {
            display();
            int option = getOptionsInput(new String[]{"Exit",
                    "Display in Time frame",
                    "Edit an Event in Series",
                    "Add a Series manually",
                    "Add a repeating Series"});
            switch(option){
                case 0:
                    break;
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    String name = getStringInput("Series name:");
                    EventCollection newManualSeries = new EventCollection(name, selectEvents(), saver);
                    newManualSeries.save();
                    break;
                case 4:
                    String name1 = getStringInput("Series name:");
                    Event base = createEvent();
                    GregorianCalendar endPeriod = getDateInput("Select end date for this series or null for " +
                            "infinite repeating events ", true);
                    EventGenerator EG = new EventGenerator(base, base.getStartDate().getTime(),
                            endPeriod.getTime(), getFrequency());
                    EventCollection series = new EventCollection(name1,EG,saver);
                    series.save();
                    break;
                default:
            }
        }
    }
    private void regularEventsMenu()
    {
            int option = getOptionsInput(new String[]{"Exit",
                    "Display in Time frame",
                    "Edit an Event in Series"});
            switch(option){
                case 0:
                    break;
                case 1:
                    break;
                case 2:
            }
    }
    private List<Event> selectEvents()
    {
        return null;
    }
    private Event createEvent() throws InvalidDateException
    {
        String name = getStringInput("Base Event Name:");
        GregorianCalendar start = getDateInput("Start Date of Base Event");
        GregorianCalendar end = getDateInput("End Date of Base Event");
        return new Event(name+start.getTime(), name, start, end);
    }
    private List<Duration> getFrequency()
    {
        List<Duration> ret = new ArrayList<>();
        int option = 1;
        while(option!=0)
        {
            Duration dur = getDurationInput("Choose a the frequency of repetition for your event");
            option = getOptionsInput(new String[]{"Exit",
                    "Choose another frequency of repetition for your event"});
            ret.add(dur);
        }
        return ret;
    }

}
