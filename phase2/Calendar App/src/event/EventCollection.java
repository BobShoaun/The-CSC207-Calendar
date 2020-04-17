package event;

import exceptions.InvalidDateException;
import memotag.Memo;
import memotag.Tag;

import java.util.*;

/**
 * A collection of events
 */
public class EventCollection implements Iterable<Event>, Observer {
    private List<Event> events;
    //discuss implementation of postponed (maybe extend event or events extends postponed event?)
    private List<Event> postponedEvents;

    /**
     * constructor for a finite/manually created series, or a list of regular events if name == ""
     *
     * @param events list of events of the series
     */
    public EventCollection(List<Event> events) {
        this.events = events;
        this.postponedEvents = new ArrayList<>();
    }

    /**
     * Set the list of manual events
     *
     * @param events Manual events list
     */
    public void setEvents(List<Event> events) {
        this.events = events;
    }

    /**
     * Set the list of postponed events
     *
     * @param postponedEvents List of postponed events
     */
    public void setPostponedEvents(List<Event> postponedEvents) {
        for (Event event :
                postponedEvents) {
            event.setPostponed(true);
        }
        this.postponedEvents = postponedEvents;
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

    /**
     * Get the list of postponed events
     *
     * @return Postponed events List
     */
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
        event.addObserver(this);
    }

    /**
     * remove the given event from events/ or ignore if it is an infinite series
     *
     * @param event the event to be removed
     * @return if the event is removed or not
     */
    public boolean removeEvent(Event event) throws InvalidDateException {
        String eventId = event.getId();
        System.out.println("target id :" + eventId);
        System.out.println();
        for (Event e : events) {
            System.out.println(e.getId() + "\t" + e.getId().equals(eventId));
        }
        boolean removed = this.events.removeIf(e -> e.getId().equals(eventId));
        if (removed) {
            event.addObserver(this);
        }
        System.out.println("Is is removed?"+removed);
        return removed;
    }

    /**
     * @param oldEvent an Event that has been edited
     * @param newEvent the replacement event
     * @return if the events was edited or not
     */
    public boolean editEvent(Event oldEvent, Event newEvent) throws InvalidDateException {
        boolean removed = removeEvent(oldEvent);
        if (removed) {
            addEvent(newEvent);
            newEvent.addObserver(this);
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
    public boolean postponeEvent(Event event) throws InvalidDateException {
        if (removeEvent(event)) {
            addPostponedEvent(event);
            event.addObserver(this);
            return true;
        }
        return false;
    }

    /**
     * Reschedule an event.
     *
     * @param event    Event to reschedule
     * @param newStart New start time
     * @param newEnd   New end time
     * @throws InvalidDateException if the date is bad
     */
    public void rescheduleEvent(Event event, GregorianCalendar newStart, GregorianCalendar newEnd) throws InvalidDateException {
        if (!newStart.before(event.getStartDate()) && !newStart.after(event.getStartDate())
                && !newEnd.before(event.getEndDate()) && !newEnd.after(event.getEndDate()))
            return;
        event.setEndDate(newEnd);
        event.setStartDate(newStart);
        for (Event e : postponedEvents) {
            if (e.getId().equals(event.getId())) {
                e.setPostponed(false);
                postponedEvents.remove(e);
                events.add(event);
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
     * Add a memo to an event
     *
     * @param eventId ID of the event
     * @param memo    Memo to add
     * @return True iff the memo could be added
     */
    public boolean addMemo(String eventId, Memo memo) {
        for (Event e : this.events) {
            //check if the event ID is valid
            if (e.getId().equals(eventId)) {
                memo.addEvent(e);
                return true;
            }
        }
        return false;
    }

    /**
     * toString method for easy reading
     *
     * @return String representation of the EventCollection
     */
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
     * @param event     the event to br checked
     * @param startTime start time
     * @param endTime   end time
     * @return true if the event's start time is within the start and end time
     */
    protected boolean isOnTime(Event event, GregorianCalendar startTime, GregorianCalendar endTime) {
        return event.getStartDate().before(endTime) && event.getEndDate().after(startTime);
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
        event.setPostponed(true);
    }

    /**
     * Update the observers
     *
     * @param o   Observable object
     * @param arg The update
     */
    @Override
    public void update(Observable o, Object arg) {
        //TODO: implement update
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

    //Legacy method for console UI
    public String[] getEventOptions() {
        String[] eventList = new String[events.size() + 1];
        eventList[0] = "Exit";
        for (int i = 0; i < events.size(); i++) {
            eventList[i + 1] = events.get(i).toString();
        }
        return eventList;
    }


}
