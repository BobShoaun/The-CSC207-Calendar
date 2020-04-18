package user;

import dates.CalendarGenerator;
import event.Event;
import event.EventCollection;
import event.Series;
import exceptions.InvalidDateException;
import exceptions.NoSuchSeriesException;
import memotag.Memo;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Manages a Calendar's EventCollections.
 */
public class EventManager {

    private final DataSaver dataSaver;
    private final List<EventCollection> eventCollections;

    /**
     * Constructor for EventManager
     *
     * @param eventCollections Calendar's EventCollections.
     * @param dataSaver        DataSaver for saving
     */
    public EventManager(List<EventCollection> eventCollections, DataSaver dataSaver) {
        this.eventCollections = eventCollections;
        List<Event> defaultList = new ArrayList<>();
        eventCollections.add(new EventCollection(defaultList));
        this.dataSaver = dataSaver;
    }

    /**
     * @return the single regular list of Event
     */
    public EventCollection getManualEventCollection() {
        return eventCollections.stream().filter(eC -> !(eC instanceof Series)).findAny().get();
    }

    /**
     * @return the list of series
     */
    public List<Series> getSeries() {
        List<Series> ret = new ArrayList<Series>();
        for (EventCollection ec : this.eventCollections) {
            if (ec instanceof Series) {
                ret.add((Series) ec);
            }
        }
        return ret;
    }

    /**
     * @param name of the series to be searched
     * @return the Series with this name
     */
    public Series getSeries(String name) throws NoSuchSeriesException {
        for (Series s : getSeries()) {
            if (s.getName().equals(name)) {
                return s;
            }
        }
        throw new NoSuchSeriesException();
    }

    /**
     * Get all event collections
     *
     * @return List of all event collections including all series
     */
    public List<EventCollection> getEventCollections() {
        return eventCollections;
    }

    /**
     * @param event the Event to be searched
     * @return the EventCollection that this event belongs to
     */
    public EventCollection getEventCollection(Event event) {
        for (EventCollection e : getEventCollections()) {
            if (e.getEvent(event.getId()) != null) {
                return e;
            }
        }
        System.out.println("Didn't find any EventCollection");
        return getManualEventCollection();
    }

    /**
     * Return all events which occur at day of that event
     *
     * @param date Date during which the event should occur
     * @return Unsorted list of all events which occur at that day
     */
    public List<Event> getEvents(GregorianCalendar date) {
        List<Event> toReturn = new ArrayList<Event>();

        for (EventCollection eventCollection :
                eventCollections) {
            toReturn.addAll(eventCollection.getEvents(date));
        }

        return toReturn;
    }

    /**
     * Gets all events which either start or end during this time period. The endpoints are INCLUDED
     *
     * @param start Earliest time for an event to end
     * @param end   Latest time for an event to end
     * @return List of all events where start point <= end and end point >= start
     */
    public List<Event> getEvents(GregorianCalendar start, GregorianCalendar end) {
        ArrayList<Event> toReturn = new ArrayList<Event>();
        EventIterator eventIterator = new EventIterator(start.getTime(), event -> event.getStartDate().before(end));
        eventIterator.forEachRemaining(toReturn::add);
        return toReturn;
    }

    /**
     * Returns an iterator to get future events sorted by start time.
     * [Warning] The iterator will not be updated and become unusable if the number of event collections change
     *
     * @param start The earliest possible start time of the returned events
     * @return Null if no event collections exist, otherwise an Iterator<alert.Event>
     */
    public Iterator<Event> getFutureEvents(Date start) {
        if (eventCollections.size() == 0) {
            return null;
        }
        return new EventIterator(start, null);
    }

    /**
     * Will return the event with the given id.
     *
     * @param id ID of the requested event
     * @return Return the event, if not found return null;
     */
    public Event getEvent(String id) {
        for (EventCollection eventCollection :
                eventCollections) {
            Event event = eventCollection.getEvent(id);
            if (event != null)
                return event;
        }
        return null;
    }

    /**
     * Create a new event series
     *
     * @param event the event that thi series is modeled upon
     */
    public void addEventSeries(Event event, String seriesName){
        Series newSeries = new Series(seriesName, null, new CalendarGenerator(null, null, null));
        newSeries.addEvent(event);
        eventCollections.add(newSeries);
        System.out.println(newSeries);
        dataSaver.saveEvents(this);
    }

    /**
     * Add an event series either to an existing series or it creates a new series
     *
     * @param name       Name of the event collection it is added to
     * @param start      Time of the first repeating event
     * @param end        No event will occur after this time. Can be null if event will go on for ever
     * @param difference The time difference between two created events
     * @param baseEvent  The event on which the other events will be based
     */
    public void addEventSeries(String name, GregorianCalendar start, GregorianCalendar end, Duration difference, Event baseEvent) throws InvalidDateException {
        try {
            Series existingSeries = getSeries(name);
            existingSeries.addRepeatingEvent(baseEvent, start, end, difference);
        } catch (NoSuchSeriesException e) {
            Series eventCollection = new Series(name, baseEvent, new CalendarGenerator(start, Collections.singletonList(difference), end));
            System.out.println(eventCollection.getClass());
            eventCollections.add(eventCollection);
        }
        dataSaver.saveEvents(this);
    }

    /**
     * Return all events linked to a memo
     *
     * @param memo The memo to search by
     * @return List of events with memo
     */
    public List<Event> getLinkedEvents(Calendar calendar, Memo memo) {
        return memo.getEvents().stream().map(calendar::getEvent).collect(Collectors.toList());
    }

    /**
     * Get the event collection with a certain name
     *
     * @param eventSeriesName Name of the event collection
     * @return Event collection if it exists otherwise null
     */
    public EventCollection getEventCollection(String eventSeriesName) {
        return eventCollections.stream().filter(eC -> eC instanceof Series
                && ((Series) eC).getName().equals(eventSeriesName)).findAny().orElse(null);
    }

    /**
     * Get all events with a certain name
     *
     * @param eventName The name of the event to search by
     * @return Iterator with all events with same name
     */
    public Iterator<Event> getEvents(String eventName) {
        return new EventIterator(new Date(0), (Event e) -> e.getName().equals(eventName));
    }

    /**
     * Get all postponed events
     *
     * @return List of all postponed events
     */
    public List<Event> getPostponedEvents() {
        ArrayList<Event> postponedEvents = new ArrayList<Event>();
        for (EventCollection eventCollection :
                eventCollections) {
            postponedEvents.addAll(eventCollection.getPostponedEvents());
        }
        postponedEvents.sort(Event::compareTo);
        return postponedEvents;
    }

    /**
     * Changes the name of an event
     *
     * @param event Event to change name of
     * @param name  new name
     */
    public void renameEvent(Event event, String name) {
        dataSaver.deleteFile("/events/" + event.getId() + ".txt");
        event.setName(name);
        dataSaver.saveEvents(this);
    }

    /**
     * remove event
     *
     * @param event The event to be removed
     * @throws InvalidDateException Internal error
     */
    public void removeEvent(Event event) throws InvalidDateException {
        eventCollections.stream().filter(eC -> eC.getEvent(event.getId()) != null).findAny().orElseThrow(null).removeEvent(event);
    }

    /**
     * get all event series's name
     *
     * @return A list containing all the names of all event series in order of internal representation
     */
    public List<String> getEventSeriesNames() {
        return eventCollections.stream().filter(eC -> eC instanceof Series)
                .map(eC -> ((Series) eC).getName()).filter(f -> !f.equals("")).collect(Collectors.toList());
    }

    /**
     * Remove the event collection
     *
     * @param eventCollection Event collection to remove
     */
    public void removeEventCollection(EventCollection eventCollection) {
        Series series = (Series)eventCollection;
        if(series != null){
            try {
                dataSaver.deleteDirectory("/series/"+series.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        eventCollections.remove(eventCollection);
    }

    /**
     * Event Iterator is used to iterate over the individual event collections to get the next time
     */
    private class EventIterator implements Iterator<Event> {
        private final Predicate<Event> isValid;
        private final List<Iterator<Event>> eventCollectionEventIterators;
        private final List<Event> possibleNext;

        /**
         * Initialise a new event iterator which iterates overall event collections at once returning events by their
         * start time.
         * This will not observe changes to the number of event collections so a new one must be created after that occurs
         *
         * @param start   The earliest possible time for an event
         * @param isValid A predicate to filer events by. Can be null
         */
        public EventIterator(Date start, Predicate<Event> isValid) {
            if (isValid == null)
                this.isValid = e -> true;
            else
                this.isValid = isValid;
            eventCollectionEventIterators = new ArrayList<Iterator<Event>>();
            possibleNext = new ArrayList<Event>();

            for (EventCollection eventCollection :
                    eventCollections) {
                eventCollectionEventIterators.add(eventCollection.getEventIterator(start));
                possibleNext.add(null);
            }
        }

        /**
         * Returns true if any of the individualS event collection event iterators still has an item
         *
         * @return If there are further events in the future
         */
        @Override
        public boolean hasNext() {
            // Checks if any event is still cached
            for (Event event :
                    possibleNext) {
                if (event != null) {
                    return true;
                }
            }

            // Checks if an additional event can be gotten from an iterator,
            for (int i = 0; i < eventCollectionEventIterators.size(); i++) {
                findNextInIterator(i);
                if (possibleNext.get(i) != null)
                    return true;
            }
            return false;
        }

        /**
         * Return the next event in any of the collections sorted by start time
         *
         * @return The next event. This will never be null
         */
        @Override
        public Event next() {
            //Update the possible next list to include values from all event collection iterators which still have events
            for (int i = 0; i < possibleNext.size(); i++) {
                findNextInIterator(i);
            }

            // Select the earliest event
            int index = -1;
            Event first = null;
            for (Event event :
                    possibleNext) {
                if (event != null) {
                    if (first == null || first.getStartDate().after(event.getStartDate())) {
                        first = event;
                        index = possibleNext.indexOf(event);
                    }
                }
            }

            //Remove the returned item from the cache
            possibleNext.set(index, null);

            return first;
        }

        /**
         * Find the next event in a certain event collection
         *
         * @param i The index of the event collection iterator
         */
        private void findNextInIterator(int i) {
            if (possibleNext.get(i) == null && eventCollectionEventIterators.get(i).hasNext()) {
                Event next = null;
                while (eventCollectionEventIterators.get(i).hasNext()) {
                    Event possible = eventCollectionEventIterators.get(i).next();
                    if (isValid.test(possible)) {
                        next = possible;
                        break;
                    }
                }
                if (next != null)
                    possibleNext.set(i, next);
            }
        }
    }
}