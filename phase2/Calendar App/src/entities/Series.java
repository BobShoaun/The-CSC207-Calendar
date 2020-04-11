package entities;

import dates.CalendarGenerator;
import exceptions.InvalidDateException;
import mt.Tag;

import java.time.Duration;
import java.util.*;

public class Series extends EventCollection implements Iterable<Event> {

    private String name;
    private Event baseEvent;
    private CalendarGenerator calGen;
    private List<SubSeries> subSeries;
    //this attribute holds the events generated from calGen in memory, never saved nor loaded, but updated when CalGen is updated
    //To save memory...
    private List<Event> seriesEvents;
    protected GregorianCalendar startTime;

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
        if (baseEvent != null) {
            this.seriesEvents = generateEvents();
        } else
            this.seriesEvents = new ArrayList<>();
    }

    /**
     * set the startTime of this Series manually
     *
     * @param startTime the new start Time of this Series
     */
    public void setStartTime(GregorianCalendar startTime) {
        this.startTime = startTime;
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

    protected void setStartDisplayTime(GregorianCalendar start) {
        if (start.before(startTime)) {
            //The anchor for the startTime
            calGen.setStartTime(startTime);
        } else {
            calGen.setStartTime(start);
        }
    }

    //TODO: test

    /**
     * @return name of this Infinite Series
     */
    public String getName() {
        return name;
    }


    public CalendarGenerator getCalGen() {
        return calGen;
    }

    /**
     * @return the regular events and series events within the time period
     */
    @Override
    public List<Event> getEvents() {
        List<Event> ret = super.getEvents();
        ret.addAll(this.seriesEvents);
        Collections.sort(ret);
        return ret;
    }

    public List<Event> getManualEvents() {
        return super.getEvents();
    }

    public Event getBaseEvent() {
        return baseEvent;
    }

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

    @Override
    public List<Event> getEvents(GregorianCalendar date) {
        GregorianCalendar startTime = roundUp(date);
        GregorianCalendar endTime = roundDown(date);
        return getEvents(startTime, endTime);
    }

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

    @Override
    public boolean editEvent(Event oldEvent, Event newEvent) throws InvalidDateException {
        boolean edited = super.editEvent(oldEvent, newEvent);
        if (!edited && removeEvent(oldEvent)) {
            super.addEvent(newEvent);
            edited = true;
        }
        return edited;
    }

    @Override
    public boolean postponedEvent(Event event) throws InvalidDateException {
        if (!super.postponedEvent(event)) {
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
     * @param baseEvent the event that the new subSeries is modelled on
     * @param start     start of the sub series
     * @param end       the end of the sub series
     * @param frequency the frequency of repetition of the series
     */

    public void addRepeatingEvent(Event baseEvent, GregorianCalendar start, GregorianCalendar end, Duration frequency) {
        List<Duration> dur = new ArrayList<>();
        dur.add(frequency);
        CalendarGenerator newCG = new CalendarGenerator(start, dur, end);
        subSeries.add(new SubSeries(baseEvent, newCG));
    }


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

//    @Override
//    public void save() throws IOException {
//        saveHelper("series/" + this.name + "/", super.getEvents());
//        saveHelper("series/" + this.name + "/postponed/", super.getPostponedEvents());
//        getSaver().saveToFile("series/" + this.name + "/CalenderGenerator.txt", this.calGen.getString());
//    }

//    @Override
//    public void load() throws IOException, InvalidDateException {
//        setEvents(loadHelper("series/" + this.name + "/"));
//        setPostponedEvents(loadHelper("series/" + this.name + "/postponed/"));
//        String CG = getSaver().loadStringFromFile("series/" + this.name + "/CalenderGenerator.txt");
//        this.calGen = new CalendarGenerator(CG);
//    }

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

        for (SubSeries s : subSeries) {
            ret.addAll(generateEventsHelper(s.getBase(), s.getCalGen()));
        }
        return ret;
    }

    private List<Event> generateEventsHelper(Event base, CalendarGenerator CG) throws InvalidDateException {
        List<Event> ret = new ArrayList<>();
        for (GregorianCalendar GC : CG) {
            String id = base.getName() + "%" + GC.getTime();
            Event event = new Event(id, base.getName(), GC, addTime(GC, base.getDuration()));
            ret.add(event);
        }
        return ret;
    }

    private CalendarGenerator defaultCG(CalendarGenerator CG) {
        long time = Duration.ofDays(365).toMillis();
        GregorianCalendar startTime = calGen.getStartTime();
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
