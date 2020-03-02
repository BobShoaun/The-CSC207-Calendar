import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class EventCollection implements Serializable
{
    private ArrayList<Event> events;
    private String name;

    /**
     * constructor for list of regular events
     * @param events list of regular events
     */
    public EventCollection(ArrayList<Event> events)
    {
        this.events = events;
    }

    /**
     * constructor for a series
     * @param name name of series
     * @param events list of events of the series
     */
    public EventCollection(String name, ArrayList<Event> events)
    {
        this.name = name;
        this.events = events;
    }

    public String getName(){return name;}

    public Event getEvent(String id){ throw new NotImplementedException(); };

    /**
     * Returns all events that is
     * @param date date of events demanded
     * @return a list of events that is on the same day as <date></>
     */
    public List<Event> getEvents(Date date)
    {
        List<Event> ret = new ArrayList<>();
        for (Event e:this.events)
        {
            //omitted for now
        }
        return ret;
    }
    public List<Event> getEvents(Date start, Date end) {throw new NotImplementedException();}
    public Iterator<Event> getEventIterator(Date start) {throw new NotImplementedException();}

    /**
     * add an event to this list
     * @param event the event to be added
     */
    public void addEvent(Event event){this.events.add(event);}

    public void removeEvent(Event event){throw new NotImplementedException();}

    public void addRepeatingEvent(Event baseEvent, Date start, Date end, Date frequency) {
        throw new NotImplementedException();
    }

    public void makeEventToSeries(String eventId, Date end, Date frequency) {
        throw new NotImplementedException();
    }
}
