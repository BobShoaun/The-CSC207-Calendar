import exceptions.InvalidDateException;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.Calendar;

public class EventCollection implements Iterable<Event>
{
    private List<Event> events;
    private EventGenerator eGen;
    private String name;
    private DataSaver saver;

    /**
     * constructor for a finite/manually created series, or a list of regular events if name == null
     *
     * @param name   name of series
     * @param events list of events of the series
     * @param saver  saver object to load/save this EventCollection
     */
    public EventCollection(String name, List<Event> events, DataSaver saver)
    {
        this.name = name;
        this.events = events;
        this.saver = saver;
    }

    /**
     * Constructor for a repeating/infinite series
     *
     * @param name  name of the series
     * @param eGen  Event generator for this series
     * @param saver saver object to load/save this EventCollection
     */
    public EventCollection(String name, EventGenerator eGen, DataSaver saver)
    {
        this.name = name;
        this.eGen = eGen;
        this.saver = saver;
    }

    /**
     * @return name of this EventCollection
     */
    public String getName()
    {
        return name;
    }

    /**
     * return a event in the list with the given id
     *
     * @param id of the event to be searched for
     * @return event with given id
     */
    public Event getEvent(String id)
    {
        Event ret = null;
        for (Event e : this.events)
        {
            if (e.getId().equals(id))
            {
                ret = e;
                break;
            }
        }
        return ret;
    }

    /**
     * Returns all events that is
     *
     * @param date date of events demanded
     * @return a list of events that is on the same day as <date></>
     */
    public List<Event> getEvents(Date date)
    {
        //find alternative
        Date startTime = roundUp(date);
        Date endTime = roundDown(date);
        return getEvents(startTime, endTime);
    }

    public List<Event> getEvents(Date start, Date end)
    {
        List<Event> ret = new ArrayList<>();
        for (Event e : this.events)
        {
            if (isOnTime(e, start, end))
            {
                ret.add(e);
            }
        }
        return ret;
    }

    /**
     * @param start the earliest start date of events in this iterator
     * @return a iterator containing all events but ones that have start dates before <start></>
     */
    public Iterator<Event> getEventIterator(Date start)
    {
        Iterator<Event> ite = events.iterator();
        while (ite.hasNext())
        {
            Event curr = ite.next();
            Date startDate = curr.getStartDate().getTime();
            if (startDate.compareTo(start) <= 0)
            {
                ite.remove();
            }
        }
        return ite;
    }

    /**
     * add an event to this list, if the event is infinite event will be ignored in eGen
     *
     * @param event the event to be added
     */
    public void addEvent(Event event)
    {
        if(eGen!=null)//if inf
        {
            eGen.getCalGen().addIgnore(event.getStartDate());
        }
        this.events.add(event);
    }

    /**
     * remove the given event from events/ or ignore if it is an infinite series
     *
     * @param event the event to br removed
     */
    public void removeEvent(Event event) throws InvalidDateException
    {
        String eventId = event.getId();
        if (eGen != null)
        {
            for (Event e:eGen.generateEvents())
            {
                if(e.getId().equals(eventId))
                {
                    eGen.getCalGen().addIgnore(e.getStartDate());
                }
            }
        }
        this.events.removeIf(e -> e.getId().equals(eventId));
    }

    /**
     *
     * @param oldEvent an Event that has been edited
     * @param newEvent the replacement event
     * @throws InvalidDateException
     */
    public void editEvent(Event oldEvent, Event newEvent) throws InvalidDateException
    {
        String eventId = oldEvent.getId();
        if (eGen != null && eventId.equals(eGen.getBaseEvent().getId()))
        {
            eGen.getCalGen().addIgnore(oldEvent.getStartDate());
        }
        removeEvent(oldEvent);
        this.events.add(newEvent);
    }

    public void addRepeatingEvent(Event baseEvent, Date start, Date end, Date frequency) throws InvalidDateException
    {
        //finite series
        if (eGen == null)
        {
            this.eGen = new EventGenerator(baseEvent, start, end, dateToDurationList(start, frequency));
        } else
        {
            CalendarGenerator CG = eGen.getCalGen();
            CG.addPeriod(dateToDuration(start, frequency));
            eGen.setCalGen(CG);
        }
        this.events.addAll(eGen.generateEvents());
    }

    public void makeEventToSeries(String eventId, Date end, Date frequency) throws InvalidDateException
    {
        //Assume originally finite series
        Event base = getEvent(eventId);
        Date start = base.getStartDate().getTime();
        addRepeatingEvent(base, start, end, frequency);
        // if end=null generate infinite events
    }

    public void addTag(String eventId, Tag tag)
    {
        for (Event e : this.events)
        {
            if (e.getId().equals(eventId))
            {
                tag.addEvent(eventId);
            }
        }
    }

    private List<Duration> dateToDurationList(Date start, Date end)
    {
        List<Duration> durList = new ArrayList<>();
        Duration dur = Duration.between((Temporal) start, (Temporal) end);
        durList.add(dur);
        return durList;
    }

    private Duration dateToDuration(Date start, Date end)
    {
        return Duration.between((Temporal) start, (Temporal) end);
    }

    @Override
    public String toString()
    {
//        StringBuilder result = new StringBuilder("Alert for EventID " + getEventId()
//                + ", which occurs at " + eventTime.getTime().toString() + ".\n");
//        result.append("===== MANUALLY CREATED ALERTS =====\n");
//        for (Alert a : manAlerts) {
//            result.append(a.toString()).append("\n");
//        }
//        result.append("===== REPEATING ALERTS =====\n");
//        result.append(calGen.toString());
//        return result.toString();
        return "";
    }

    private String regularEventsToString()
    {
        StringBuilder result = new StringBuilder("Events\n");
        result.append("===== YOUR EVENTS =====\n");
        for (Event e : this.events)
        {
            result.append(e.toString() + "\n");
        }
        return result.toString();
    }

    private String finiteSeriesToString()
    {
        StringBuilder result = new StringBuilder("Events for Series " + name + "\n");
        result.append("===== YOUR SERIES =====\n");
        for (Event e : this.events)
        {
            result.append(e.toString() + "\n");
        }
        return result.toString();
    }

    private String infiniteSeriesToString()
    {

        StringBuilder result = new StringBuilder("Events for Series " + name + "\n");
//        result.append("===== YOUR SERIES =====\n");
//        List<Event> infiniteList= new EventGenerator();
//        for (Event e: this.events) {
//            result.append(e.toString()+"\n");
//        }
        return result.toString();
    }


    public void load(String eventId) throws InvalidDateException
    {
        List<String> strings = null;
        try {
            strings = saver.loadStringsFromFile("/series/" + eventId + ".txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert strings != null;
        this.name = strings.get(0).trim();

        String[] eventsDetails = strings.get(1).trim().split(",");
        for (String e : eventsDetails) {
            String[] details = e.trim().split("\\n");
            String id = details[0];
            String name = details[1];
            GregorianCalendar start = new GregorianCalendar();
            start.setTime(new Date(Long.parseLong(details[2])));
            GregorianCalendar end = new GregorianCalendar();
            start.setTime(new Date(Long.parseLong(details[3])));

            this.events.add(new Event(id,name,start,end));
        }

        StringBuilder cgStr = new StringBuilder();
        for (int i = 2; i < strings.size(); i++) {
            cgStr.append(strings.get(i));
        }
        this.eGen.setCalGen(new CalendarGenerator(cgStr.toString()));
    }

    /**
     * Save this EventCollection's data into a text file.
     */
    public void save() throws IOException {
        List<String> contents = Arrays.asList(getString().split("\\s+"));
        saver.saveToFile("/series/" + name + ".txt", contents);
    }

    /**
     * Get a String representation of data in this AlertCollection.
     *
     * @return String representation of all the data in this AC, including the CalendarGenerator.
     */
    public String getString() {
        StringBuilder result = new StringBuilder(this.name + "\n ");
        if(this.events!=null)
        {
            for (Event e : this.events) {
                result.append(e.getString()).append(",");
            }
        }
        if(eGen!=null)
        {
            result.append("\n ").append(eGen.getCalGen().getString());
        }
        return result.toString();
    }

    /**
     * @param event     the event to br checked
     * @param startTime start time
     * @param endTime   end time
     * @return true iff the event time period is within the start and end time
     */
    private boolean isOnTime(Event event, Date startTime, Date endTime)
    {
        Date startEvent = GCToDate(event.getStartDate());
        Date endEvent = GCToDate(event.getEndDate());
        boolean within1 = startTime.before(startEvent) && endTime.after(startEvent);
        boolean within2 = startTime.before(endEvent) && endTime.after(endEvent);
        return within1 && within2;
    }

    private Date GCToDate(GregorianCalendar calendar)
    {
        return calendar.getTime();
    }

    private Date roundUp(Date date)
    {
        Calendar cl = Calendar.getInstance();
        cl.setTime(date);
        cl.set(Calendar.HOUR_OF_DAY, 0);
        cl.set(Calendar.MINUTE, 0);
        cl.set(Calendar.SECOND, 0);
        cl.set(Calendar.MILLISECOND, 0);
        return cl.getTime();
    }

    private Date roundDown(Date date)
    {
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
    public Iterator<Event> iterator()
    {
        return new EventCollectionIterator();
    }

    private class EventCollectionIterator implements Iterator<Event>
    {
        /**
         * The index of the next Event to return.
         */
        private int current = 0;

        /**
         * Returns whether there is another Event to return.
         *
         * @return whether there is another Event to return.
         */
        @Override
        public boolean hasNext()
        {
            return current < events.size();
        }

        /**
         * Returns the next Event.
         *
         * @return the next Event.
         */
        @Override
        public Event next()
        {
            Event res;

            // List.get(i) throws an IndexOutBoundsException if
            // we call it with i >= contacts.size().
            // But Iterator's next() needs to throw a
            // NoSuchElementException if there are no more elements.
            try
            {
                res = events.get(current);
            } catch (IndexOutOfBoundsException e)
            {
                throw new NoSuchElementException();
            }
            current += 1;
            return res;
        }

        /**
         * Removes the Event just returned.
         */
        @Override
        public void remove()
        {
            if (events.size() != 0)
            {
                events.remove(current - 1);
                current = -1;
            } else
            {
                throw new NoSuchElementException();
            }
        }
    }

    public void removeTag(String eventId, Tag tag)
    {
        throw new UnsupportedOperationException();
    }

}
