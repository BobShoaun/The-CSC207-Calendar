
import org.apache.commons.lang3.time.DateUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
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

    public Event getEvent(String id){  throw new UnsupportedOperationException();}

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
    public List<Event> getEvents(Date start, Date end) {
        throw new UnsupportedOperationException();
    }

    public Iterator<Event> getEventIterator(Date start) {
        throw new UnsupportedOperationException();
    }

    /**
     * add an event to this list
     * @param event the event to be added
     */
    public void addEvent(Event event){this.events.add(event);}

    public void removeEvent(Event event){

    }

    public void addRepeatingEvent(Event baseEvent, Date start, Date end, Date frequency) {

    }

    public void makeEventToSeries(String eventId, Date end, Date frequency) {

    }

    public void addTag(String eventId, MT tag) {

    }

    private boolean isOnDate(Event event, Date date)
    {
        Date start = event.getStartDate();
        Date end = event.getEndDate();
        Date am = DateUtils.truncate(date, Calendar.DAY_OF_MONTH);
        Date pm = DateUtils.ceiling(date, Calendar.DAY_OF_MONTH);
        boolean within1 = (am.before(start) && pm.after(start)) || (am.after(start) && am.before(end));
        boolean within2 = (am.before(end) && pm.after(end)||(pm.after(start) && pm.before(end)));
        return within1 && within2;
    }
}
