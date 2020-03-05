import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.Serializable;
import java.util.*;
import java.util.Calendar;
import java.util.function.Consumer;

public class EventCollection implements Iterable<Event>
{
    private List<Event> events;
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

    /**
     *
     * @param start the earliest start date of events in this iterator
     * @return a iterator containing all events but ones that have start dates before <start></>
     */
    public Iterator<Event> getEventIterator(Date start) {
        Iterator<Event> ite = events.iterator();
        while(ite.hasNext())
        {
            Event curr = ite.next();
            Date startDate = curr.getStartDate().getTime();
            if (startDate.compareTo(start)<=0)
            {
                ite.remove();
            }
        }
        return ite;
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

    public void addTag(String eventId, Tag tag) {
        for (Event e:this.events){
            if (e.getId().equals(eventId)){
                tag.addEvent(eventId);
            }
        }
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

    /**
     * Returns an iterator over elements of type {@code Event}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<Event> iterator() {return new EventCollectionIterator();}

    private class EventCollectionIterator implements Iterator<Event>{
        /** The index of the next Event to return. */
        private int current = 0;

        /**
         * Returns whether there is another Event to return.
         *
         * @return whether there is another Event to return.
         */
        @Override
        public boolean hasNext() {
            return current < events.size();
        }

        /**
         * Returns the next Event.
         *
         * @return the next Event.
         */
        @Override
        public Event next() {
            Event res;

            // List.get(i) throws an IndexOutBoundsException if
            // we call it with i >= contacts.size().
            // But Iterator's next() needs to throw a
            // NoSuchElementException if there are no more elements.
            try {
                res = events.get(current);
            } catch (IndexOutOfBoundsException e) {
                throw new NoSuchElementException();
            }
            current += 1;
            return res;
        }

        /**
         * Removes the Event just returned.
         */
        @Override
        public void remove() {
            if(events.size()!=0)
            {
                events.remove(current-1);
                current=-1;
            }
            else {throw new NoSuchElementException();}
        }
    }

    public void removeTag(String eventId, Tag tag) {
        throw new NotImplementedException();
    }

}
