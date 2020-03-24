package entities;

import exceptions.InvalidDateException;
import mt.Tag;
import user.DataSaver;

import java.io.IOException;
import java.time.Duration;
import java.util.*;

public class EventCollection implements Iterable<Event>, Observer {
    private List<Event> events;
    //discuss implementation of postponed (maybe extend event or events extends postponed event?)
    private List<Event> postPonedEvents;
    private DataSaver saver;

    //TODO: Discuss the file name of regular events and need to talk through new save/load system
    String path = "Event_List";

    /**
     * constructor for a finite/manually created series, or a list of regular events if name == ""
     *
     * @param events list of events of the series
     * @param saver  saver object to load/save this alert.EventCollection
     */
    public EventCollection(List<Event> events, DataSaver saver) throws InvalidDateException {
        this.events = events;
        this.saver = saver;
        //TODO: have to reimplement load
//        load(path);
    }

    //TODO: re-evaluate overriding in Finite Series

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
    public void addEvent(Event event) {
        boolean added = false;
        for (int i = 0; i < this.events.size(); i++) {
            if (event.getStartDate().compareTo(this.events.get(i).getStartDate()) <= 0) {
                this.events.add(i, event);
                added = true;
                break;
            }
        }
        if (!added) {
            this.events.add(event);
        }

        //TODO:observer
        event.addObserver(this::update);
//        save();
    }

    /**
     * remove the given event from events/ or ignore if it is an infinite series
     *
     * @param event the event to br removed
     */
    public void removeEvent(Event event) throws IOException {
        String eventId = event.getId();
        this.events.removeIf(e -> e.getId().equals(eventId));
//        save();
    }

    /**
     * @param oldEvent an alert.Event that has been edited
     * @param newEvent the replacement event
     * @throws InvalidDateException
     */
    public void editEvent(Event oldEvent, Event newEvent) throws IOException {
        //TODO: could implement mutation instead of removing and adding
        removeEvent(oldEvent);
        this.events.add(newEvent);
//        save();
    }

    /**
     * adds a tag to the events with th eventId
     *
     * @param eventId the id of the event to be tagged
     * @param tag     the tag
     */
    public void addTag(String eventId, Tag tag) {
        for (Event e : this.events) {
            if (e.getId().equals(eventId)) {
                tag.addEvent(eventId);
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

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("Events\n");
        result.append("===== EVENTS =====\n");
        for (Event e : this.events) {
            result.append(e.toString()).append("\n");
        }
        return result.toString();
    }

    //TODO: re-implement save
    /**
     * Save this alert.EventCollection's data into a text file.
     */
//    public void save() throws IOException {
//        List<String> contents = Arrays.asList(getString().split("\\n"));
//        if (name.equals("")) {
//            saver.saveToFile("events/noname.txt", contents);
//        } else {
//            saver.saveToFile("events/" + name + ".txt", contents);
//        }
//    }

    //TODO: reimplement Load
    /**
     * loads events from text file
     * problems with file path and date conversion.
     *
     * @param seriesName the series name that needs to be loaded
     * @throws InvalidDateException
     */
//    public void load(String seriesName) throws InvalidDateException {
//        List<String> strings = null;
//        try {
//            String path;
//            if (seriesName.equals(""))
//                path = "events/noname.txt";
//            else
//                path = "events/" + seriesName + ".txt";
//            strings = saver.loadStringsFromFile(path);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        if (strings == null || strings.size() == 0) {
//            this.events = new ArrayList<>();
//            return;
//        }
//        this.name = strings.get(0).trim();
//        List<Event> newEvents = new ArrayList<>();
//
//        String[] eventsDetails = strings.get(1).trim().split(",");
//        for (String e : eventsDetails) {
//            String[] details = e.trim().split(";");
//            String id = details[0];
//            String eventName = details[1];
//            GregorianCalendar start = new GregorianCalendar();
//            start.setTimeInMillis(Long.parseLong(details[2]));
//            GregorianCalendar end = new GregorianCalendar();
//            end.setTimeInMillis(Long.parseLong(details[3]));
//            newEvents.add(new Event(id, eventName, start, end));
//        }
//        /**
//         * can't load from this...
//         */
//        this.events = newEvents;
////        StringBuilder cgStr = new StringBuilder();
////        for (int i = 2; i < strings.size(); i++) {
////            cgStr.append(strings.get(i));
////        }
////
////        this.eGen.setCalGen(new CalendarGenerator(cgStr.toString()));
//    }

    /**
     * Get a String representation of data in this EventCollection
     *
     * @return String representation of all the data in this AC, including the dates.CalendarGenerator.
     */
    protected String getString() {
        StringBuilder result = new StringBuilder();
        if (this.events != null) {
            for (Event e : this.events) {
                result.append(e.getString());
            }
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
        //TODO: not inclusive of end points
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
     * @param date
     * @return
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
     * @param date
     * @return
     */
    protected GregorianCalendar roundDown(GregorianCalendar date) {
        date.set(Calendar.HOUR_OF_DAY, 23);
        date.set(Calendar.MINUTE, 59);
        date.set(Calendar.SECOND, 59);
        date.set(Calendar.MILLISECOND, 999);
        return date;
    }

    /**
     * Returns an iterator over elements of type {@code alert.Event}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<Event> iterator() {
        return new EventCollectionIterator();
    }

    @Override
    public void update(Observable o, Object arg) {
        //TODO: reimplement save
        //save();
    }

    private class EventCollectionIterator implements Iterator<Event> {
        /**
         * The index of the next alert.Event to return.
         */
        private int current = 0;

        /**
         * Returns whether there is another alert.Event to return.
         *
         * @return whether there is another alert.Event to return.
         */
        @Override
        public boolean hasNext() {
            return current < events.size();
        }

        /**
         * Returns the next alert.Event.
         *
         * @return the next alert.Event.
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
         * Removes the alert.Event just returned.
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


    public String[] getEventOptions() {
        String[] eventList = new String[events.size() + 1];
        eventList[0] = "Exit";
        for (int i = 0; i < events.size(); i++) {
            eventList[i + 1] = events.get(i).toString();
        }
        return eventList;
    }


}
