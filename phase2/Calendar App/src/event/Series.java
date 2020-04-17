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
    private final Event baseEvent;
    private final CalendarGenerator calGen;
    private List<RepeatingEvent> repeatingEvents;
    //this attribute holds the events generated from calGen in memory, never saved nor loaded, but updated when CalGen is updated
    //To save memory...
    private List<Event> seriesEvents;
    private final GregorianCalendar startTime;

    /**
     * @param name      the name of this Infinite Series
     * @param baseEvent the base Event this Series is modelled upon
     * @param calGen    List of start date of the events of the series, which also contains the DISPLAY endTime of this Infinite series
     * @throws InvalidDateException invalid dates in events
     */
    public Series(String name, Event baseEvent, CalendarGenerator calGen) throws InvalidDateException {
        super(new ArrayList<>());
        this.name = name;
        this.baseEvent = baseEvent;
        this.calGen = calGen;
        this.startTime = calGen.getStartTime();
        this.repeatingEvents = new ArrayList<>();
        if (baseEvent != null) {
            this.seriesEvents = generateEvents();
        } else
            this.seriesEvents = new ArrayList<>();
    }

    /**
     * Set the display period of this series up given user input
     *
     * @param start the start display time of this Series
     * @param end   the end display time of this Series
     */
    public void setDisplayPeriod(GregorianCalendar start, GregorianCalendar end) {
        //TODO: set display time for all sub series
        setStartDisplayTime(start);
        this.calGen.setEndTime(end);
    }

    /**
     * Set the start time for the display 'window'.
     *
     * @param start Display window start time
     */
    protected void setStartDisplayTime(GregorianCalendar start) {
        if (start.before(startTime)) {
            //The anchor for the startTime
            calGen.setStartTime(startTime);
        } else {
            calGen.setStartTime(start);
        }
    }

    /**
     * Set the subseries (repeating events) for
     *
     * @param RepeatingEvents
     */
    public void setRepeatingEvents(List<RepeatingEvent> RepeatingEvents) {
        this.repeatingEvents = RepeatingEvents;
        try {
            this.seriesEvents = generateEvents();
        } catch (InvalidDateException ignored) {

        }
    }
    //TODO: test

    /**
     * @return name of this Infinite Series
     */
    public String getName() {
        return name;
    }

    /**
     * Get the calendar Generator.
     *
     * @return CalendarGenerator instance
     */
    public CalendarGenerator getCalGen() {
        return calGen;
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
        ret.addAll(getManualEvents());
        ret.addAll(this.seriesEvents);
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
     * Get the starting/base event
     *
     * @return Base event
     */
    public Event getBaseEvent() {
        return baseEvent;
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
        if (null == ret) {
            for (Event e : this.seriesEvents) {
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
        List<Event> ret = super.getEvents(start, end);
        for (Event e : this.seriesEvents) {
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
            for (Event e : seriesEvents) {
                if (e.getId().equals(eventId)) {
                    calGen.addIgnore(e.getStartDate());
                    seriesEvents = generateEvents();
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
            for (Event e : seriesEvents) {
                if (e.getId().equals(event.getId())) {
                    calGen.addIgnore(e.getStartDate());
                    addPostponedEvent(e);
                    seriesEvents = generateEvents();
                    return true;
                }
            }
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
            for (Event e : seriesEvents) {
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
            for (Event e : seriesEvents) {
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
        try {
            this.seriesEvents = generateEvents();
        } catch (InvalidDateException ignored) {

        }
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
     * @throws InvalidDateException invalid dates in events
     */
    private List<Event> generateEvents() throws InvalidDateException {
        List<Event> ret = new ArrayList<>();
        if (calGen.getEndTime() == null) {
            CalendarGenerator defaultCG = defaultCG(calGen);
            this.setDisplayPeriod(defaultCG.getStartTime(), defaultCG.getEndTime());
            ret.addAll(generateEventsHelper(baseEvent, defaultCG));
        } else {
            ret.addAll(generateEventsHelper(baseEvent, calGen));
        }

        for (RepeatingEvent s : repeatingEvents) {
            ret.addAll(generateEventsHelper(s.getBase(), s.getCalGen()));
        }
        return ret;
    }

    private List<Event> generateEventsHelper(Event base, CalendarGenerator CG) throws InvalidDateException {
        List<Event> ret = new ArrayList<>();
        for (GregorianCalendar startDates : CG) {
            Event event = new Event(base.getName(), startDates, addTime(startDates, base.getDuration()));
            ret.add(event);
        }
        return ret;
    }

    private CalendarGenerator defaultCG(CalendarGenerator CG) {
        long time = Duration.ofDays(31).toMillis();
        GregorianCalendar startTime = CG.getStartTime();
        GregorianCalendar endTime = addTime(startTime, time);
        return new CalendarGenerator(startTime, calGen.getPeriods(), endTime);
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
