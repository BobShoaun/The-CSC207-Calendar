package entities;

import dates.CalendarGenerator;
import exceptions.InvalidDateException;
import mt.Tag;
import user.DataSaver;

import java.io.IOException;
import java.time.Duration;
import java.util.*;

public class Series extends EventCollection implements Iterable<Event> {

    private String name;
    private Event baseEvent;
    private CalendarGenerator calGen;

    //this attribute holds the events generated from calGen in memory, never saved nor loaded, but updated when CalGen is updated
    //To save memory...
    private List<Event> seriesEvents;

    /**
     * @param name      the name of this Infinite Series
     * @param baseEvent the base Event this Series is modelled upon
     * @param calGen    List of start date of the events of the series, which also contains the DISPLAY endTime of this Infinite series
     * @param saver     saver object handling save and load of this series
     * @throws InvalidDateException invalid dates in events
     */
    public Series(String name, Event baseEvent, CalendarGenerator calGen, DataSaver saver) throws InvalidDateException {
        //TODO: Watch it for save/load
        super(new ArrayList<>(), saver);
        this.name = name;
        this.baseEvent = baseEvent;
        this.calGen = calGen;
        if(baseEvent != null){
            this.seriesEvents = generateEvents();
        }
    }

    public void setDisplayPeriod(GregorianCalendar start, GregorianCalendar end){
        if(start.after(calGen.getStartTime())) {this.calGen.setStartTime(start);}
        this.calGen.setEndTime(end);
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

    //TODO: addRepeatingEvent should not be a thing, it should just call the constructor of Infinite Series
    // Series should not be within another Series, CalGen takes a single start and end date not a list of them.
    // And i'm not going to make a list of Cal Gen
    // if a user want a new series simply create a new one, but put a series in a series
    // MAYBE users can edit the add additional durations for cal gen
    // OR MAYBE users can clone a series/frequency and edit start date etc...
    // OR MAYBE calGen accepts a list of start dates

//    public void addRepeatingEvent(Event baseEvent, GregorianCalendar start, GregorianCalendar end, Duration frequency) throws InvalidDateException, IOException {
//        if (eGen == null) {
//            List<Duration> durs = new ArrayList<>();
//            durs.add(Duration.ofMillis(frequency.getTime()));
//            this.eGen = new EventGenerator(baseEvent, start, end, durs);
//        } else {
//            CalendarGenerator CG = eGen.getCalGen();
//            CG.addPeriod(Duration.ofMillis(frequency.getTime()));
//            eGen.setCalGen(CG);
//        }
//        flush(eGen);
//        save();
//    }

    /**
     * Add addition frequency of event repetition for this series.
     * i.e. it was repeating every 2 weeks, now I want it to repeat every 3 days as well
     *
     * @param frequency the new period of delay between start date of events in the series
     */
    public void addDuration(Duration frequency) {
        this.calGen.addPeriod(frequency);
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

    @Override
    public void load() throws IOException, InvalidDateException {
        setEvents(loadHelper("series/" + this.name + "/"));
        setPostponedEvents(loadHelper("series/" + this.name + "/postponed/"));
        String CG = getSaver().loadStringFromFile("series/" + this.name + "/CalenderGenerator.txt");
        this.calGen = new CalendarGenerator(CG);
    }

    /**
     * Generate events based on the current CalGen
     * if calGen.endTime == null i.e. the current display endtime of this infinite series is undefined
     * then set a default display endtime of 1 year from calGen.starTime
     *
     * @return List of events that has start time according to CalGen
     * @throws InvalidDateException invalid dates in events
     */
    public List<Event> generateEvents() throws InvalidDateException {
        if (calGen.getEndTime() == null) {
            long time = Duration.ofDays(365).toMillis();
            GregorianCalendar startTime = calGen.getStartTime();
            GregorianCalendar endTime = addTime(startTime, time);
            CalendarGenerator defaultCG = new CalendarGenerator(startTime, calGen.getPeriods(), endTime);

            calGen.setEndTime(endTime);
            return generateEventsHelper(defaultCG);
        }
        return generateEventsHelper(calGen);
    }

    private List<Event> generateEventsHelper(CalendarGenerator CG) throws InvalidDateException {
        List<Event> ret = new ArrayList<>();
        for (GregorianCalendar GC : CG) {
            String id = baseEvent.getName() + "%" + GC.getTime();
            Event event = new Event(id, baseEvent.getName(), GC, addTime(GC, baseEvent.getDuration()));
            ret.add(event);
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
