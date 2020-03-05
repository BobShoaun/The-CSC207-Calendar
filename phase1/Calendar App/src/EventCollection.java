import java.io.Serializable;
import java.util.*;
import java.util.Calendar;

public class EventCollection implements Serializable // TODO: shouldn't this be TextFileSerializer?
{
    private ArrayList<Event> events;
    private String name;

    /**
     * constructor for a series, or a list of regular events if name == null
     *
     * @param name   name of series
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
    public List<Event> getEvents(Date date) {
        //find alternative
        Date startTime = roundUp(date);
        Date endTime = roundDown(date);
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
        Date startEvent = GCToDate(event.getStartDate());
        Date endEvent = GCToDate(event.getEndDate());
        boolean within1 = startTime.before(startEvent) && endTime.after(startEvent);
        boolean within2 = startTime.before(endEvent) && endTime.after(endEvent);
        return  within1 && within2;

    }
    private Date GCToDate(GregorianCalendar calendar)
    {
        return calendar.getTime();
    }
    private Date roundUp(Date date){
        Calendar cl = Calendar.getInstance();
        cl.setTime(date);
        cl.set(Calendar.HOUR_OF_DAY, 23);
        cl.set(Calendar.MINUTE, 59);
        cl.set(Calendar.SECOND, 59);
        cl.set(Calendar.MILLISECOND, 999);
        return cl.getTime();
    }
    private Date roundDown(Date date){
        Calendar cl = Calendar.getInstance();
        cl.setTime(date);
        cl.set(Calendar.HOUR_OF_DAY, 23);
        cl.set(Calendar.MINUTE, 59);
        cl.set(Calendar.SECOND, 59);
        cl.set(Calendar.MILLISECOND, 999);
        return cl.getTime();
    }
}
