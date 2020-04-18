package event;

import dates.CalendarGenerator;
import exceptions.InvalidDateException;
import memotag.Memo;
import memotag.Tag;

import java.time.Duration;
import java.util.*;

/**
 * A series of events, made up of manual and repeating events.
 */
public class Series extends EventCollection implements Iterable<Event> {

    private final String name;
    private List<RepeatingEvent> repeatingEvents;
    //this attribute holds the events generated from calGen in memory, never saved nor loaded, but updated when CalGen is updated
    //To save memory...

    /**
     * @param name      the name of this Infinite Series
     * @param baseEvent the base Event this Series is modelled upon
     * @param calGen    List of start date of the events of the series, which also contains the DISPLAY endTime of this Infinite series
     */
    public Series(String name, Event baseEvent, CalendarGenerator calGen) {
        super(new ArrayList<>());
        this.name = name;
        repeatingEvents = new ArrayList<>();
        if (baseEvent != null) {
            repeatingEvents.add(new RepeatingEvent(baseEvent, calGen));
        }
    }

    /**
     * Set the subseries (repeating events) for
     *
     * @param RepeatingEvents new repeating events
     */
    public void setRepeatingEvents(List<RepeatingEvent> RepeatingEvents) {
        this.repeatingEvents.addAll(RepeatingEvents);
    }

    /**
     * @return name of this Infinite Series
     */
    public String getName() {
        return name;
    }

    /**
     * Get the list of subseries (repeating events)
     *
     * @return SubSeries list
     */
    public List<RepeatingEvent> getRepeatingEvents() {
        return repeatingEvents;
    }

    /**
     * @return the regular events and series events within the time period
     */
    @Override
    public List<Event> getEvents() {
        List<Event> ret = new ArrayList<>();
        ret.addAll(generateEvents(new GregorianCalendar(0, GregorianCalendar.JANUARY, 1)));
        Collections.sort(ret);
        return ret;
    }

    /**
     * Get the list of manual events
     *
     * @return List of manual events.
     */
    public List<Event> getManualEvents() {
        return super.getEvents();
    }

    /**
     * Search for an Event by its ID
     *
     * @param id of the event to be searched for
     * @return Event with the corresponding id.
     */
    @Override
    public Event getEvent(String id) {
        Event ret = super.getEvent(id);
        if (ret == null) {
            for (Event e : generateEvents(new GregorianCalendar(0, Calendar.JANUARY, 0))) {
                if (e.getId().equals(id)) {
                    return e;
                }
            }
        }
        return ret;
    }

    /**
     * Get all events between a set of times
     *
     * @param start Earliest time for an event to end
     * @param end   Latest time for an event to end
     * @return Events between start and end.
     */
    @Override
    public List<Event> getEvents(GregorianCalendar start, GregorianCalendar end) {
        List<Event> ret = new ArrayList<>();
        for (Event e : generateEvents(new GregorianCalendar(0, Calendar.JANUARY, 0))) {
            if (isOnTime(e, start, end)) {
                ret.add(e);
            }
        }
        Collections.sort(ret);
        return ret;
    }

    /**
     * Get the events at a certain time.
     *
     * @param date date of events demanded
     * @return Events at that date.
     */
    @Override
    public List<Event> getEvents(GregorianCalendar date) {
        GregorianCalendar startTime = roundUp(date);
        GregorianCalendar endTime = roundDown(date);
        return getEvents(startTime, endTime);
    }

    /**
     * Remove an event.
     *
     * @param event the event to be removed
     * @return True iff the event could be removed
     * @throws InvalidDateException if the time is invalid
     */
    @Override
    public boolean removeEvent(Event event) throws InvalidDateException {
        boolean removed = super.removeEvent(event);
        if (!removed) {
            String eventId = event.getId();
            for (Event e : generateEvents(new GregorianCalendar(0, Calendar.JANUARY, 0))) {
                if (e.getId().equals(eventId)) {
                    for (RepeatingEvent repeatingEvent :
                            repeatingEvents) {
                        if (repeatingEvent.getBase().getName().equals(event.getName())) {
                            repeatingEvent.getCalGen().addIgnore(event.getStartDate());
                        }
                    }
                    return true;
                }
            }
        }
        return removed;
    }

    /**
     * Edit an event.
     *
     * @param oldEvent an Event that has been edited
     * @param newEvent the replacement event
     * @return True iff the event was successfully swapped.
     * @throws InvalidDateException if there is a date error
     */
    @Override
    public boolean editEvent(Event oldEvent, Event newEvent) throws InvalidDateException {
        boolean edited = super.editEvent(oldEvent, newEvent);
        if (!edited && removeEvent(oldEvent)) {
            super.addEvent(newEvent);
            edited = true;
        }
        return edited;
    }

    /**
     * Postpone an event.
     *
     * @param event the event to be postponed
     * @return True iff the event could be postponed
     * @throws InvalidDateException If there is a date error.
     */
    @Override
    public boolean postponeEvent(Event event) throws InvalidDateException {
        if (!super.postponeEvent(event)) {
            removeEvent(event);
            addPostponedEvent(event);
            return true;
        }
        return false;
    }

    /**
     * Add a tag to an event.
     *
     * @param eventId the id of the event to be tagged
     * @param tag     the tag
     * @return True iff the tag could be added.
     */
    @Override
    public boolean addTag(String eventId, Tag tag) {
        if (!super.addTag(eventId, tag)) {
            for (Event e : generateEvents(new GregorianCalendar(0, Calendar.JANUARY, 0))) {
                if (e.getId().equals(eventId)) {
                    tag.addEvent(eventId);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Add a memo to an event.
     *
     * @param eventId ID of the event
     * @param memo    Memo to add
     * @return True iff the memo could be added.
     */
    @Override
    public boolean addMemo(String eventId, Memo memo) {
        if (!super.addMemo(eventId, memo)) {
            for (Event e : generateEvents(new GregorianCalendar(0, Calendar.JANUARY, 0))) {
                if (e.getId().equals(eventId)) {
                    memo.addEvent(e);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param baseEvent the event that the new subSeries is modelled on
     * @param start     start of the sub series
     * @param end       the end of the sub series
     * @param frequency the frequency of repetition of the series
     */
    public void addRepeatingEvent(Event baseEvent, GregorianCalendar start, GregorianCalendar end, Duration frequency) {
        List<Duration> dur = new ArrayList<>();
        dur.add(frequency);
        CalendarGenerator newCG = new CalendarGenerator(start, dur, end);
        repeatingEvents.add(new RepeatingEvent(baseEvent, newCG));
    }

    /**
     * Get a nice String representation of this Series.
     *
     * @return String representation of the Series.
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("===== Series =====\n");
        List<Event> allEvents = getEvents();

        if (allEvents.size() == 0) {
            result.append("You don't have any events");
        }
        for (Event e : allEvents) {
            result.append(e.toString()).append("\n");
        }
        result.append(postponedToString());
        return result.toString();
    }

    /**
     * Generate events based on the current CalGen
     * if calGen.endTime == null i.e. the current display endtime of this infinite series is undefined
     * then set a default display endtime of 1 year from calGen.starTime
     *
     * @return List of events that has start time according to CalGen
     */
    private List<Event> generateEvents(GregorianCalendar start) {
        List<Event> ret = new ArrayList<>();
        for (Event event :
                getManualEvents()) {
            if (event.getStartDate().after(start)) {
                ret.add(event);
            }
        }

        for (RepeatingEvent s : repeatingEvents) {
            try {
                ret.addAll(generateEventsHelper(s.getBase(), s.getCalGen(), start));
            } catch (InvalidDateException ignored) {

            }
        }
        return ret;
    }

    private List<Event> generateEventsHelper(Event base, CalendarGenerator CG, GregorianCalendar startTime) throws InvalidDateException {
        List<Event> ret = new ArrayList<>();
        int max = 20;
        for (GregorianCalendar startDate : CG) {
            if (startDate.after(startTime)) {
                Event event = new Event(base.getName(), startDate, addTime(startDate, base.getDuration()));
                ret.add(event);
                max--;
                if (max == 0) {
                    break;
                }
            }
        }
        return ret;
    }

    /**
     * Adds time (in millis) to a begin date and return it
     *
     * @param begin the begin date
     * @param time  the time in millis to be added to begin
     * @return a GC that is <time> later than <begin>
     */
    private GregorianCalendar addTime(GregorianCalendar begin, long time) {
        //Adding the time to offset each event
        GregorianCalendar newGC = new GregorianCalendar();
        long dur = begin.getTimeInMillis() + time;
        Date d2 = new Date(dur);
        newGC.setTime(d2);
        return newGC;
    }

    /**
     * Get the EventIterator.
     *
     * @param start the earliest start date of events in this iterator
     * @return Event Iterator
     */
    //TODO: Not too sure how this work/ if it works
    @Override
    public Iterator<Event> getEventIterator(Date start) {
        return getEvents().stream().filter(event -> event.getStartDate().getTime().after(start)).iterator();
    }

    /**
     * Returns an iterator over elements of type {@code events}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<Event> iterator() {
        return new InfiniteSeriesIterator();
    }

    private class InfiniteSeriesIterator implements Iterator<Event> {
        /**
         * The index of the next Event to return.
         */
        private int current = 0;
        private List<Event> allEvents = getEvents();

        /**
         * Returns whether there is another Event to return.
         *
         * @return whether there is another Event to return.
         */
        @Override
        public boolean hasNext() {
            return current < allEvents.size();
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
                res = allEvents.get(current);
                current += 1;
            } catch (IndexOutOfBoundsException e) {
                throw new NoSuchElementException();
            }
            return res;
        }
    }
}
