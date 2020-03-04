
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
     * constructor for a series, or a list of regular events if name == null
     * @param name name of series
     * @param events list of events of the series
     */
    public EventCollection(String name, ArrayList<Event> events)
    {
        this.name = name;
        this.events = events;
    }

    /**
     *
     * @return name of this EventCollection
     */
    public String getName(){return name;}

    /**
     * return a event in the list with the given id
     * @param id of the event to be searched for
     * @return event with given id
     */
    public Event getEvent(String id){
        Event ret = null;
        for (Event e:this.events) {
            if(e.getId().equals(id))
            {
                ret = e;
                break;
            }
        }
        return ret;
    }

    /**
     * Returns all events that is
     * @param date date of events demanded
     * @return a list of events that is on the same day as <date></>
     */
    public List<Event> getEvents(Date date)
    {
        //find alternative
        Date startTime = DateUtils.truncate(date, Calendar.DAY_OF_MONTH);
        Date endTime = DateUtils.ceiling(date, Calendar.DAY_OF_MONTH);
        return getEvents(startTime, endTime);
    }

    public List<Event> getEvents(Date start, Date end) {
        List<Event> ret = new ArrayList<>();
        for (Event e:this.events)
        {
            if(isOnTime(e,start, end)){
                ret.add(e);
            }
        }
        return ret;
    }

    public Iterator<Event> getEventIterator(Date start) {
        throw new UnsupportedOperationException();
    }

    /**
     * add an event to this list
     * @param event the event to be added
     */
    public void addEvent(Event event){this.events.add(event);}

    /**
     * remove the given event from events
     * @param event the event to br removed
     */
    public void removeEvent(Event event){
            String eventId = event.getId();
            this.events.removeIf(e -> e.getId().equals(eventId));
    }

    public void addRepeatingEvent(Event baseEvent, Date start, Date end, Date frequency) {

    }

    public void makeEventToSeries(String eventId, Date end, Date frequency) {

    }

    public void addTag(String eventId, MT tag) {

    }

    /**
     *
     * @param event the event to br checked
     * @param startTime start time
     * @param endTime end time
     * @return true iff the event time period is within the start and end time
     */
    private boolean isOnTime(Event event, Date startTime, Date endTime)
    {
        //find alternative
//        Date startEvent = event.getStartDate();
//        Date endEvent = event.getEndDate();
//
//        boolean within1 = (startTime.before(startEvent) && endTime.after(startEvent)) || (startTime.after(startEvent) && startTime.before(endEvent));
//        boolean within2 = (startTime.before(endEvent) && endTime.after(endEvent)||(endTime.after(startEvent) && endTime.before(endEvent)));
//        return within1 && within2;
        return true;
    }
}
