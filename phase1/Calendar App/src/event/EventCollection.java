package event;

import dates.CalendarGenerator;
import dates.EventGenerator;
import exceptions.InvalidDateException;
import mt.Tag;
import user.DataSaver;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.Temporal;
import java.util.*;

public class EventCollection implements Iterable<Event>, Observer {
    private List<Event> events;
    private EventGenerator eGen;
    private String name;
    private DataSaver saver;

    //temporary
    public EventCollection() {

    }

    /**
     * constructor for a finite/manually created series, or a list of regular events if name == null
     *
     * @param name   name of series
     * @param events list of events of the series
     * @param saver  saver object to load/save this event.EventCollection
     */
    public EventCollection(String name, List<Event> events, DataSaver saver) {
        this.name = name;
        this.events = events;
        this.saver = saver;
    }

    /**
     * Constructor for a repeating/infinite series
     *
     * @param name  name of the series
     * @param eGen  event.Event generator for this series
     * @param saver saver object to load/save this event.EventCollection
     */
    public EventCollection(String name, EventGenerator eGen, DataSaver saver) {
        this.name = name;
        this.eGen = eGen;
        this.saver = saver;
    }

    public EventCollection(String name, DataSaver dataSaver) throws InvalidDateException {
        this.name = name;
        this.saver = dataSaver;
        load(name);
    }

    /**
     * @return name of this event.EventCollection
     */
    public String getName() {
        return name;
    }

    /**
     * return a event in the list with the given id
     *
     * @param id of the event to be searched for
     * @return event with given id
     */
    public Event getEvent(String id) {
        Event ret = null;
        for (Event e : this.events) {
            if (e.getId().equals(id)) {
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
     * @return a list of events that is on the same day as <date></> //TODO: Make this work like in calendar
     */
    public List<Event> getEvents(Date date) {
        //find alternative
        Date startTime = roundUp(date);
        Date endTime = roundDown(date);
        return getEvents(startTime, endTime);
    }

    /**
     * Gets all events which either start or end during this time period. The endpoints are INCLUDED
     *
     * @param start Earliest time for an event to end
     * @param end   Latest time for an event to end
     * @return List of all events where start point <= end and end point >= start
     */
    public List<Event> getEvents(Date start, Date end) {
        List<Event> ret = new ArrayList<>();
        GregorianCalendar startGC = new GregorianCalendar();
        startGC.setTime(start);
        GregorianCalendar endGC = new GregorianCalendar();
        endGC.setTime(end);
        for (Event e : this.events) {
            if (e.getStartDate().before(endGC) && e.getEndDate().after(startGC)) {
                ret.add(e);
            }
        }
        return ret;
    }

    /**
     * @param start the earliest start date of events in this iterator
     * @return a iterator containing all events but ones that have start dates before <start></>
     */
    public Iterator<Event> getEventIterator(Date start) {
        return events.stream().filter(event -> event.getStartDate().getTime().after(start)).iterator();
    }

    /**
     * add an event to this list, if the event is infinite event will be ignored in eGen
     *
     * @param event the event to be added
     */
    public void addEvent(Event event) throws IOException {
        if (eGen != null)//if inf
        {
            eGen.getCalGen().addIgnore(event.getStartDate());
        }
        this.events.add(event);
        event.addObserver(this::update);
        save();
    }

    /**
     * remove the given event from events/ or ignore if it is an infinite series
     *
     * @param event the event to br removed
     */
    public void removeEvent(Event event) throws InvalidDateException, IOException {
        String eventId = event.getId();
        if (eGen != null) {
            for (Event e : eGen.generateEvents()) {
                if (e.getId().equals(eventId)) {
                    eGen.getCalGen().addIgnore(e.getStartDate());
                }
            }
        }
        this.events.removeIf(e -> e.getId().equals(eventId));
        save();
    }

    /**
     * @param oldEvent an event.Event that has been edited
     * @param newEvent the replacement event
     * @throws InvalidDateException
     */
    public void editEvent(Event oldEvent, Event newEvent) throws InvalidDateException, IOException {
        String eventId = oldEvent.getId();
        if (eGen != null && eventId.equals(eGen.getBaseEvent().getId())) {
            eGen.getCalGen().addIgnore(oldEvent.getStartDate());
        }
        removeEvent(oldEvent);
        this.events.add(newEvent);
        save();
    }

    public void addRepeatingEvent(Event baseEvent, Date start, Date end, Date frequency) throws InvalidDateException, IOException {
        //finite series
        if (eGen == null) {
            this.eGen = new EventGenerator(baseEvent, start, end, dateToDurationList(start, frequency));
        } else {
            CalendarGenerator CG = eGen.getCalGen();
            CG.addPeriod(dateToDuration(start, frequency));
            eGen.setCalGen(CG);
        }
        this.events.addAll(eGen.generateEvents());
        save();
    }

    public void makeEventToSeries(String eventId, Date end, Date frequency) throws InvalidDateException, IOException {
        //Assume originally finite series
        Event base = getEvent(eventId);
        Date start = base.getStartDate().getTime();
        addRepeatingEvent(base, start, end, frequency);
        // if end=null generate infinite events
    }

    public void addTag(String eventId, Tag tag) {
        for (Event e : this.events) {
            if (e.getId().equals(eventId)) {
                tag.addEvent(eventId);
            }
        }
    }

    private List<Duration> dateToDurationList(Date start, Date end) {
        List<Duration> durList = new ArrayList<>();
        Duration dur = Duration.between((Temporal) start, (Temporal) end);
        durList.add(dur);
        return durList;
    }

    private Duration dateToDuration(Date start, Date end) {
        return Duration.between((Temporal) start, (Temporal) end);
    }

    @Override
    public String toString() {
        if (name == null) {
            return regularEventsToString();
        } else if (eGen == null) {
            return finiteSeriesToString();
        } else {
            try {
                return infiniteSeriesToString();
            } catch (InvalidDateException e) {
                e.printStackTrace();
            }
        }
        return "Invalid date";
    }

    private String regularEventsToString() {
        StringBuilder result = new StringBuilder("Events\n");
        result.append("===== EVENTS =====\n");
        for (Event e : this.events) {
            result.append(e.toString()).append("\n");
        }
        return result.toString();
    }

    private String finiteSeriesToString() {
        StringBuilder result = new StringBuilder("Events for Series " + name + "\n");
        result.append("===== SERIES =====\n");
        for (Event e : this.events) {
            result.append(e.toString()).append("\n");
        }
        return result.toString();
    }

    private String infiniteSeriesToString() throws InvalidDateException {

        StringBuilder result = new StringBuilder("Events for Series " + name + "\n");
        result.append("===== SERIES =====\n");
        result.append("Here is your manually created events\n");
        for (Event e : this.events) {
            result.append(e.toString()).append("\n");
        }
        String start = eGen.getCalGen().getStartTime().getTime().toString();
        String end = eGen.getCalGen().getEndTime().getTime().toString();

        result.append("Here is your repeating events in this series from ").append(start).append(" to ").append(end).append("\n");
        for (Event e : eGen.generateEvents()) {
            result.append(e.toString());
        }
        return result.toString();
    }

    /**
     * loads events from text file
     * problems with file path and date conversion.
     *
     * @param seriesName the series name that needs to be loaded
     * @throws InvalidDateException
     */
    public void load(String seriesName) throws InvalidDateException {
        List<String> strings = null;
        try {
            String path;
            if (seriesName.equals(""))
                path = "events/noname.txt";
            else
                path = "events/" + seriesName + ".txt";
            strings = saver.loadStringsFromFile(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (strings == null) throw new AssertionError();
        this.name = strings.get(0).trim();
        List<Event> newEvents = new ArrayList<>();

        String[] eventsDetails = strings.get(1).trim().split(",");
        for (String e : eventsDetails) {
            String[] details = e.trim().split(";");
            String id = details[0];
            String eventName = details[1];
            GregorianCalendar start = new GregorianCalendar();
            start.setTimeInMillis(Long.parseLong(details[2]));
            GregorianCalendar end = new GregorianCalendar();
            end.setTimeInMillis(Long.parseLong(details[3]));
            newEvents.add(new Event(id, eventName, start, end));
        }
        /**
         * can't load from this...
         */
        this.events = newEvents;
//        StringBuilder cgStr = new StringBuilder();
//        for (int i = 2; i < strings.size(); i++) {
//            cgStr.append(strings.get(i));
//        }
//
//        this.eGen.setCalGen(new CalendarGenerator(cgStr.toString()));
    }

    /**
     * Save this event.EventCollection's data into a text file.
     */
    public void save() throws IOException {
        List<String> contents = Arrays.asList(getString().split("\\n"));
        if (name == null || name.equals("")) {
            saver.saveToFile("events/noname.txt", contents);
        } else {
            saver.saveToFile("events/" + name + ".txt", contents);
        }
    }

    /**
     * Get a String representation of data in this alert.AlertCollection.
     *
     * @return String representation of all the data in this AC, including the dates.CalendarGenerator.
     */
    public String getString() {
        StringBuilder result = new StringBuilder(this.name + "\n");
        if (this.events != null) {
            for (Event e : this.events) {
                result.append(e.getString());
            }
        }
        if (eGen != null) {
            result.append("\n").append(eGen.getCalGen().getString());
        }
        return result.toString();
    }

    /**
     * @param event     the event to br checked
     * @param startTime start time
     * @param endTime   end time
     * @return true iff the event time period is within the start and end time
     */
    private boolean isOnTime(Event event, Date startTime, Date endTime) {
        Date startEvent = GCToDate(event.getStartDate());
        Date endEvent = GCToDate(event.getEndDate());
        boolean within1 = startTime.before(startEvent) && endTime.after(startEvent);
        boolean within2 = startTime.before(endEvent) && endTime.after(endEvent);
        return within1 && within2;
    }

    private Date GCToDate(GregorianCalendar calendar) {
        return calendar.getTime();
    }

    private Date roundUp(Date date) {
        Calendar cl = Calendar.getInstance();
        cl.setTime(date);
        cl.set(Calendar.HOUR_OF_DAY, 0);
        cl.set(Calendar.MINUTE, 0);
        cl.set(Calendar.SECOND, 0);
        cl.set(Calendar.MILLISECOND, 0);
        return cl.getTime();
    }

    private Date roundDown(Date date) {
        Calendar cl = Calendar.getInstance();
        cl.setTime(date);
        cl.set(Calendar.HOUR_OF_DAY, 23);
        cl.set(Calendar.MINUTE, 59);
        cl.set(Calendar.SECOND, 59);
        cl.set(Calendar.MILLISECOND, 999);
        return cl.getTime();
    }

    public List<Event> getEventsBetween(GregorianCalendar start, GregorianCalendar end) throws InvalidDateException {
        List<Event> ret = new ArrayList<>();
        if (events != null) {
            for (Event e : events) {
                if (isOnTime(e, start.getTime(), end.getTime())) {
                    ret.add(e);
                }
            }
        }
        if (eGen != null) {
            EventGenerator newEG = new EventGenerator(eGen.getBaseEvent(), start.getTime(),
                    end.getTime(), eGen.getCalGen().getPeriods());
            ret.addAll(newEG.generateEvents());
        }
        return ret;
    }

    /**
     * Returns an iterator over elements of type {@code event.Event}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<Event> iterator() {
        return new EventCollectionIterator();
    }

    @Override
    public void update(Observable o, Object arg) {
        try {
            save();
        } catch (IOException e) {

        }
    }

    private class EventCollectionIterator implements Iterator<Event> {
        /**
         * The index of the next event.Event to return.
         */
        private int current = 0;

        /**
         * Returns whether there is another event.Event to return.
         *
         * @return whether there is another event.Event to return.
         */
        @Override
        public boolean hasNext() {
            return current < events.size();
        }

        /**
         * Returns the next event.Event.
         *
         * @return the next event.Event.
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
         * Removes the event.Event just returned.
         */
        @Override
        public void remove() {
            if (events.size() != 0) {
                events.remove(current - 1);
                current = -1;
            } else {
                throw new NoSuchElementException();
            }
        }
    }


    /**
     * Remove the event from the tag
     *
     * @param eventId the id of the event to be removed
     * @param tag     the tag that needs to remove the event
     */
    public void removeTag(String eventId, Tag tag) {
        tag.removeEvent(eventId);
    }
}
