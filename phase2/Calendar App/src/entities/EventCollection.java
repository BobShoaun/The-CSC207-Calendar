package entities;

import exceptions.InvalidDateException;
import mt.Tag;
import user.DataSaver;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class EventCollection implements Iterable<Event>, Observer {
    private List<Event> events;
    //discuss implementation of postponed (maybe extend event or events extends postponed event?)
    private List<Event> postponedEvents;
    private DataSaver saver;


    /**
     * constructor for a finite/manually created series, or a list of regular events if name == ""
     *
     * @param events list of events of the series
     * @param saver  saver object to load/save this EventCollection
     */
    public EventCollection(List<Event> events, DataSaver saver) {
        this.events = events;
        this.saver = saver;
        this.postponedEvents = new ArrayList<>();
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public void setPostponedEvents(List<Event> postponedEvents) {
        this.postponedEvents = postponedEvents;
    }

    public DataSaver getSaver() {
        return saver;
    }


    /**
     * @return the regular event list
     */
    public List<Event> getEvents() {
        return events;
    }

    /**
     * return a event in the list with the given id
     *
     * @param id of the event to be searched for
     * @return event with given id
     */
    public Event getEvent(String id) {
        for (Event e : this.events) {
            if (e.getId().equals(id)) {
                return e;
            }
        }
        return null;
    }

    /**
     * Gets all events which either start or end during this time period. The endpoints are INCLUDED
     *
     * @param start Earliest time for an event to end
     * @param end   Latest time for an event to end
     * @return List of all events where start point <= end and end point >= start
     */
    public List<Event> getEvents(GregorianCalendar start, GregorianCalendar end) {
        List<Event> ret = new ArrayList<>();
        if (events != null) {
            for (Event e : this.events) {
                if (isOnTime(e, start, end)) {
                    ret.add(e);
                }
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
    //TODO: test this
    public List<Event> getEvents(GregorianCalendar date) {
        //find alternative
        GregorianCalendar startTime = roundUp(date);
        GregorianCalendar endTime = roundDown(date);
        return getEvents(startTime, endTime);
    }

    public List<Event> getPostponedEvents() {
        return postponedEvents;
    }

    /**
     * add an event to this list
     *
     * @param event the event to be added
     */
    public void addEvent(Event event) {
        this.events.add(event);
        Collections.sort(events);
        event.addObserver(this::update);
    }

    /**
     * remove the given event from events/ or ignore if it is an infinite series
     *
     * @param event the event to br removed
     * @return if the event is removed or not
     */
    public boolean removeEvent(Event event) throws InvalidDateException {
        String eventId = event.getId();
        boolean removed = this.events.removeIf(e -> e.getId().equals(eventId));

        if (removed) {
            event.addObserver(this::update);
        }
        return removed;
    }

    /**
     * @param oldEvent an Event that has been edited
     * @param newEvent the replacement event
     * @return if the events was edited or not
     * @throws InvalidDateException invalid dates in events
     */
    public boolean editEvent(Event oldEvent, Event newEvent) throws InvalidDateException {
        boolean removed = removeEvent(oldEvent);
        if (removed) {
            addEvent(newEvent);
            newEvent.addObserver(this::update);
        }
        return removed;
    }

    /**
     * Postpone an event id the event exists
     *
     * @param event the event to be postponed
     * @return if the event is found and postponed
     * @throws InvalidDateException invalid date for an event
     */
    public boolean postponedEvent(Event event) throws InvalidDateException {
        if (removeEvent(event)) {
            addPostponedEvent(event);
            event.addObserver(this::update);
            return true;
        }
        return false;
    }

    public void rescheduleEvent(Event event, GregorianCalendar newStart, GregorianCalendar newEnd) throws InvalidDateException {
        for (Event e : postponedEvents) {
            if (e.getId().equals(event.getId())) {
                postponedEvents.remove(e);
                String newID = e.getName() + newStart.getTimeInMillis();
                Event newEvent = new Event(newID, e.getName(), newStart, newEnd);
                addEvent(newEvent);
                newEvent.addObserver(this::update);
                return;
            }
        }
    }

    /**
     * adds a tag to the events with the eventId, do nothing is eventID is not found
     *
     * @param eventId the id of the event to be tagged
     * @param tag     the tag
     */
    public boolean addTag(String eventId, Tag tag) {
        for (Event e : this.events) {
            //check if the event ID is valid
            if (e.getId().equals(eventId)) {
                tag.addEvent(eventId);
                return true;
            }
        }
        return false;
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

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("===== EVENTS =====\n");
        if (events.size() == 0) {
            result.append("You don't have any events");
        }
        for (Event e : this.events) {
            result.append(e.toString()).append("\n");
        }
        result.append(postponedToString());

        return result.toString();
    }

    /**
     * @return a String representation of the postponed events
     */
    protected String postponedToString() {
        StringBuilder result = new StringBuilder("\n==== Postponed Events====\n");
        if (postponedEvents.size() == 0) {
            result.append("No events are postponed");
        }
        for (Event e : this.postponedEvents) {
            result.append(e.toString()).append("\n");
        }
        return result.toString();
    }

    /**
     * Save this EventCollection's data into text files.
     */
    public void save() throws IOException {
        saveHelper("events/", this.events);
        saveHelper("events/postponed/", this.postponedEvents);
    }

    protected void saveHelper(String path, List<Event> events) throws IOException {
        for (Event e : events) {
            saver.saveToFile(path + e.getId() + ".txt", e.getString());
        }
    }

    /**
     * loads events from text file
     * problems with file path and date conversion.
     */
    public void load() throws IOException, InvalidDateException {
        events = loadHelper("events/");
        postponedEvents = loadHelper("events/postponed/");
    }

    protected List<Event> loadHelper(String path) throws IOException, InvalidDateException {
        List<Event> loadedEvents = new ArrayList<>();
        File[] data = saver.getFilesInDirectory(path);
        for (File f : data) {
            String id = f.getName();
            id = id.replaceAll(".txt", "");
            String[] eventData = saver.loadStringFromFile(path + id + ".txt").split("\\n");
            String name = eventData[1];
            GregorianCalendar start = new GregorianCalendar();
            GregorianCalendar end = new GregorianCalendar();
            start.setTimeInMillis(Long.parseLong(eventData[1]));
            end.setTimeInMillis(Long.parseLong(eventData[2]));
            loadedEvents.add(new Event(id, name, start, end));
        }
        return loadedEvents;
    }

    /**
     * @param event     the event to br checked
     * @param startTime start time
     * @param endTime   end time
     * @return true if the event's start time is within the start and end time
     */
    protected boolean isOnTime(Event event, GregorianCalendar startTime, GregorianCalendar endTime) {
        //TODO: not inclusive of end points, test this
        return event.getStartDate().before(endTime) && event.getEndDate().after(startTime);
//        Date startEvent = GCToDate(event.getStartDate());
//        Date endEvent = GCToDate(event.getEndDate());
//        boolean within1 = startTime.before(startEvent) && endTime.after(startEvent);
//        boolean within2 = startTime.before(endEvent) && endTime.after(endEvent);
//        return within1 && within2;
    }

    /**
     * round date to the beginning of that date day i.e. 0 am
     *
     * @param date the date to be rounded up
     * @return the beginning of that date day i.e. 0 am
     */
    protected GregorianCalendar roundUp(GregorianCalendar date) {
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        return date;
    }

    /**
     * round date to the end of that date day i.e. 23:59:59:999 pm
     *
     * @param date the date to be rounded up
     * @return end of that date day i.e. 23:59:59:999 pm
     */
    protected GregorianCalendar roundDown(GregorianCalendar date) {
        date.set(Calendar.HOUR_OF_DAY, 23);
        date.set(Calendar.MINUTE, 59);
        date.set(Calendar.SECOND, 59);
        date.set(Calendar.MILLISECOND, 999);
        return date;
    }

    /**
     * add an postponed event
     *
     * @param event the event to be postponed
     */
    protected void addPostponedEvent(Event event) {
        postponedEvents.add(event);
    }

    @Override
    public void update(Observable o, Object arg) {
        try {
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param start the earliest start date of events in this iterator
     * @return a iterator containing all events but ones that have start dates before <start></>
     */
    public Iterator<Event> getEventIterator(Date start) {
        return events.stream().filter(event -> event.getStartDate().getTime().after(start)).iterator();
    }

    /**
     * Returns an iterator over elements of type {@code events}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<Event> iterator() {
        return new EventCollectionIterator();
    }

    private class EventCollectionIterator implements Iterator<Event> {
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
            try {
                res = events.get(current);
                current += 1;
            } catch (IndexOutOfBoundsException e) {
                throw new NoSuchElementException();
            }
            return res;
        }
    }


    //TODO: re-evaluate this method with new UI
    public String[] getEventOptions() {
        String[] eventList = new String[events.size() + 1];
        eventList[0] = "Exit";
        for (int i = 0; i < events.size(); i++) {
            eventList[i + 1] = events.get(i).toString();
        }
        return eventList;
    }


}
