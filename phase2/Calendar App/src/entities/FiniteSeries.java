package entities;

import dates.CalendarGenerator;
import exceptions.InvalidDateException;
import user.DataSaver;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class FiniteSeries extends EventCollection {

    private String name;
    private Event baseEvent;
    private CalendarGenerator calGen;

    /**
     * Constructor for a repeating/infinite series
     *
     * @param calGen alert.Event generator for this series
     * @param saver  saver object to load/save this alert.EventCollection
     */
    public FiniteSeries(String name, Event baseEvent, CalendarGenerator calGen, DataSaver saver) throws InvalidDateException {
        super(new ArrayList<Event>(), saver);
        this.name = name;
        this.baseEvent = baseEvent;
        this.calGen = calGen;
        //load();
    }

    //TODO: test

    /**
     * Generate events based on the current CalGen
     * @return List of events that has start time according to CalGen
     * @throws InvalidDateException
     */
    public List<Event> generateEvents() throws InvalidDateException {
        List<Event> ret = new ArrayList<>();
        List<GregorianCalendar> test = new ArrayList<>();
        for (GregorianCalendar GC : calGen) {
            test.add(GC);
            String id = baseEvent.getName() + baseEvent.getStartDate().getTime();
            Event event = new Event(id, baseEvent.getName(), GC, addTime(GC, baseEvent.getDuration()));
            ret.add(event);
        }
        return ret;
    }

    /**
     * @return name of this Finite Series
     */
    public String getName() {
        return name;
    }

//    public void addEvent(Event event) throws IOException {
//        if (eGen != null)//if inf
//        {
//            eGen.getCalGen().addIgnore(event.getStartDate());
//        }

//    /**
//     * remove the given event from events/ or ignore if it is an infinite series
//     *
//     * @param event the event to br removed
//     */
//    public void removeEvent(Event event) throws InvalidDateException, IOException {
//        String eventId = event.getId();
//        if (eGen != null) {
//            for (Event e : eGen.generateEvents()) {
//                if (e.getId().equals(eventId)) {
//                    eGen.getCalGen().addIgnore(e.getStartDate());
//                }
//            }
//        }
//        this.events.removeIf(e -> e.getId().equals(eventId));
//        save();
//    }

//    /**
//     * @param oldEvent an alert.Event that has been edited
//     * @param newEvent the replacement event
//     * @throws InvalidDateException
//     */
//    public void editEvent(Event oldEvent, Event newEvent) throws InvalidDateException, IOException {
//        String eventId = oldEvent.getId();
//        if (eGen != null && eventId.equals(eGen.getBaseEvent().getId())) {
//            eGen.getCalGen().addIgnore(oldEvent.getStartDate());
//        }
//        removeEvent(oldEvent);
//        this.events.add(newEvent);
//        save();
//    }


    //TODO: make frequency as duration not date
//    public void addRepeatingEvent(Event baseEvent, Date start, Date end, Date frequency) throws InvalidDateException, IOException {
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

//    public void makeEventToSeries(String eventId, Date end, Date frequency) throws InvalidDateException, IOException {
//        //Assume originally finite series
//        Event base = getEvent(eventId);
//        Date start = base.getStartDate().getTime();
//        addRepeatingEvent(base, start, end, frequency);
//        // if end=null generate infinite events
//    }


//    @Override
//    public String toString() {
//        if (name.equals("")) {
//            return regularEventsToString();
//        } else if (eGen == null) {
//            return finiteSeriesToString();
//        } else {
//            try {
//                return infiniteSeriesToString();
//            } catch (InvalidDateException e) {
//                e.printStackTrace();
//            }
//        }
//        return "Invalid date";
//    }
//
//    private String regularEventsToString() {
//        StringBuilder result = new StringBuilder("Events\n");
//        result.append("===== EVENTS =====\n");
//        for (Event e : this.events) {
//            result.append(e.toString()).append("\n");
//        }
//        return result.toString();
//    }
//
//    private String finiteSeriesToString() {
//        StringBuilder result = new StringBuilder("Events for Series " + name + "\n");
//        result.append("===== SERIES =====\n");
//        for (Event e : this.events) {
//            result.append(e.toString()).append("\n");
//        }
//        return result.toString();
//    }
//
//    private String infiniteSeriesToString() throws InvalidDateException {
//
//        StringBuilder result = new StringBuilder("Events for Series " + name + "\n");
//        result.append("===== SERIES =====\n");
//        result.append("Here is your manually created events\n");
//        for (Event e : this.events) {
//            result.append(e.toString()).append("\n");
//        }
//        String start = eGen.getCalGen().getStartTime().getTime().toString();
//        String end = eGen.getCalGen().getEndTime().getTime().toString();
//
//        result.append("Here is your repeating events in this series from ").append(start).append(" to ").append(end).append("\n");
//        for (Event e : eGen.generateEvents()) {
//            result.append(e.toString());
//        }
//        return result.toString();
//    }

//    /**
//     * Get a String representation of data in this EventCollection
//     *
//     * @return String representation of all the data in this AC, including the dates.CalendarGenerator.
//     */
//    public String getString() {
//        StringBuilder result = new StringBuilder(this.name + "\n");
//        if (this.events != null) {
//            for (Event e : this.events) {
//                result.append(e.getString());
//            }
//        }
//        if (eGen != null) {
//            result.append("\n").append(eGen.getCalGen().getString());
//        }
//        return result.toString();
//    }

//    /**
//     * Checks if the series is infinite or not, if finite send all events to this.events and clear eGen
//     *
//     * @param eg
//     */
//    private void flush(EventGenerator eg) throws InvalidDateException {
//        if (eg.getCalGen().getEndTime() != null) {
//            this.events.addAll(eGen.generateEvents());
//            this.eGen = null;
//        }
//    }


    //    public List<Event> getEventsBetween(GregorianCalendar start, GregorianCalendar end) throws InvalidDateException {
//        List<Event> ret = new ArrayList<>();
//        if (events != null) {
//            for (Event e : events) {
//                if (isOnTime(e, start, end)) {
//                    ret.add(e);
//                }
//            }
//        }
//        if (eGen != null) {
//            EventGenerator newEG = new EventGenerator(eGen.getBaseEvent(), start.getTime(),
//                    end.getTime(), eGen.getCalGen().getPeriods());
//            ret.addAll(newEG.generateEvents());
//        }
//        return ret;
//    }

    /**
     * Adds time (in millis) to a begin date and return it
     * @param begin the begin date
     * @param time the time in millis to be added to begin
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
}
