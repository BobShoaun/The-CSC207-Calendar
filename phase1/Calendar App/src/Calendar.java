import exceptions.PeriodAlreadyExistsException;

import java.time.Duration;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Calendar class
 */
public class Calendar {

    List<EventCollection> eventCollections;
    List<AlertCollection> alertCollections;
    List<Tag> memos;
    List<Tag> tags;
    private DataSaver dataSaver;

    /**
     * Constructor for creating a new calendar with no data
     */
    public Calendar(DataSaver dataSaver) {
        this.dataSaver = dataSaver;
        eventCollections = new ArrayList<>();
        eventCollections.add(new EventCollection(null, new ArrayList<>()));
        alertCollections = new ArrayList<>();
        memos = new ArrayList<>();
        tags = new ArrayList<>();
    }

    /**
     * Constructor for calendar with already existing data
     *
     * @param eventCollections List of  event collections for new calendar
     * @param alertCollections List of alert collections for new calendar
     * @param memos            List of memos for new calendar
     * @param tags             List of tags for new calendar
     */
    public Calendar(List<EventCollection> eventCollections, List<AlertCollection> alertCollections, List<Tag> memos,
                    List<Tag> tags) {
        if (eventCollections == null || alertCollections == null || memos == null || tags == null) {
            throw new NullPointerException();
        }
        this.eventCollections = eventCollections;
        this.alertCollections = alertCollections;
        this.memos = memos;
        this.tags = tags;
    }


    /**
     * Return all events which occur during at a certain time period
     *
     * @param date Date during which the event should occur
     * @return A list of events which occur at that time
     */
    public List<Event> getEvents(Date date) {
        List<Event> toReturn = new ArrayList<>();

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
    public List<Event> getEvents(Date start, Date end) {
        List<Event> toReturn = new ArrayList<>();

        for (EventCollection eventCollection :
                eventCollections) {
            toReturn.addAll(eventCollection.getEvents(start, end));
        }

        return toReturn;
    }

    /**
     * Returns an iterator to get future events sorted by start time.
     * [Warning] The iterator will no be updated and become unusable if the number of event collections change
     *
     * @param start The earlist possible start time of the returned events
     * @return Null if no event collections exist, otherwise an Iterator<Event>
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
     * Add an event to the correct event collection
     *
     * @param event    Event to add
     * @param seriesId Series to add event to
     */
    public void createEvent(Event event, String seriesId) throws IllegalArgumentException {
        for (EventCollection eventCollection :
                eventCollections) {
            if (eventCollection.getName().equals(seriesId)) {
                eventCollection.addEvent(event);
                return;
            }
        }
        throw new IllegalArgumentException();
    }

    /**
     * Return sorted alerts which occur in [start, end]
     *
     * @param start The inclusive start time
     * @param end   The inclusive end time
     * @return All alerts in sorted order
     */
    public List<Alert> getAlerts(GregorianCalendar start, GregorianCalendar end) {
        List<Alert> alerts = new ArrayList<>();

        for (AlertCollection alertCollection :
                alertCollections) {
            alerts.addAll(alertCollection.getAlerts(start, end));
        }

        alerts.sort(new AlertComparator());

        return alerts;
    }

    /**
     * Add a single alert to an event
     * @param time The time of the alert to be added
     * @param eventId id of connected event
     */
    //TODO: use boolean return value of addAlert
    public void addAlert(GregorianCalendar time, String eventId){
        for (AlertCollection alertCollection :
                alertCollections) {
            if (alertCollection.getEventId().equals(eventId)) {
                alertCollection.addAlert(time);
                return;
            }
        }

        AlertCollection alertCollection = new AlertCollection(getEvent(eventId), new DataSaver(""));
        alertCollection.addAlert(time);
        alertCollections.add(alertCollection);
    }

    /**
     * Add a repeating alert to the event
     *
     * @param start   The relative start time of the alert
     * @param period  The time between repeating alerts starting from the relative start time
     * @param eventId The event this alert should be linked to
     */
    public void addAlert(GregorianCalendar start, Duration period, String eventId) throws PeriodAlreadyExistsException {
        for (AlertCollection alertCollection :
                alertCollections) {
            if (alertCollection.getEventId().equals(eventId)) {
                alertCollection.addAlert(start, period);
                return;
            }
        }
        Event event = getEvent(eventId);
        if (event == null) {
            throw new IllegalArgumentException();
        }
        AlertCollection alertCollection = new AlertCollection(event, new DataSaver(""));
        alertCollection.addAlert(start, period);
        alertCollections.add(alertCollection);
    }

    /**
     * Move an event to a series from another series. If the event is already part of that series nothing changes
     * @param eventId The id of the event to move
     * @param seriesName The name of the new series to add it to
     */
    public void addToSeries(String eventId, String seriesName) throws IllegalArgumentException {
        EventCollection from = null;
        EventCollection eventCollection = null;
        Event event = null;
        for (EventCollection eC :
                eventCollections) {
            if(eC.getName().equals(seriesName)){
                eventCollection = eC;
            }
            if(eC.getEvent(eventId) != null){
                from = eC;
                event = eC.getEvent(eventId);
            }
        }
        if(eventCollection == null || event == null){
            throw new IllegalArgumentException();
        }
        from.removeEvent(event);
        eventCollection.addEvent(event);
    }

    /**
     * Remove an event from a series and put in the collection series with no name
     * @param eventId The event to move
     * @throws IllegalArgumentException If the event collection or the event can not be found
     */
    public void removeFromSeries(String eventId) throws IllegalArgumentException{
        EventCollection from = null;
        EventCollection to = eventCollections.stream().filter(p -> p.getName().equals("")).findAny().orElseThrow(null);
        Event event = null;
        for (EventCollection eventCollection :
                eventCollections) {
            event = eventCollection.getEvent(eventId);
            if(event != null){
                from = eventCollection;
                break;
            }
        }
        if(event == null){
            throw new IllegalArgumentException();
        }
        from.removeEvent(event);
        to.addEvent(event);
    }

    /**
     * Add an event series either to an existing series or it creates a new series
     * @param name Name of the event collection it is added to
     * @param start Time of the first repeating event
     * @param end No event will occur after this time. Can be null if event will go on for ever
     * @param difference The time difference between two created events
     * @param baseEvent The event on which the other events will be based
     */
    public void addEventSeries(String name, Date start, Date end, Date difference, Event baseEvent){
        for (EventCollection eventCollection :
                eventCollections) {
            if (eventCollection.getName().equals(name)) {
                eventCollection.addRepeatingEvent(baseEvent, start, end, difference);
                return;
            }
        }
        EventCollection eventCollection = new EventCollection(name, new ArrayList<>());
        eventCollection.addRepeatingEvent(baseEvent, start, end, difference);
        eventCollections.add(eventCollection);
    }

    /**
     * Make an event within an existing series into a new repeating event either within the same series or within a new one
     * @param eventId Id of the event
     * @param end No event will be created after this time. Can be null if there is no end time
     * @param difference The time difference between two created events
     * @throws IllegalArgumentException Will throw when no event collection was found
     */
    public void makeEventToSeries(String eventId, Date end, Date difference, String seriesName) throws IllegalArgumentException {
        for (EventCollection eventCollection :
                eventCollections) {
            if (eventCollection.getEvent(eventId) != null) {
                if(!eventCollection.getName().equals(seriesName)){
                    Event event =  eventCollection.getEvent(eventId);
                    eventCollection.removeEvent(event);
                    EventCollection newEventCollection = new EventCollection(seriesName, new ArrayList<>());
                    addEventSeries(seriesName, event.getStartDate().getTime(), end, difference, event);
                } else{
                    eventCollection.makeEventToSeries(eventId, end, difference);
                }
                return;
            }
        }
        throw new IllegalArgumentException();
    }

    /**
     * Creates a new tag or add an existing tag to an event
     * @param eventId Event the tag is added to
     * @param tagName Name of the tag to be added
     * @throws IllegalArgumentException If the event cannot be found
     */
    public void tagEvent(String eventId, String tagName) throws IllegalArgumentException {
        Tag tag;
        Optional<Tag> optionalTag = tags.stream().filter(t -> t.getText().equals(tagName)).findAny();
        if (!optionalTag.isPresent()) {
            tag = new Tag(tagName);
            tags.add(tag);
        } else {
            tag = optionalTag.get();
        }
        for (EventCollection eventCollection :
                eventCollections) {
            if (eventCollection.getEvent(eventId) != null) {
                eventCollection.addTag(eventId, tag);
                return;
            }
        }
        throw new IllegalArgumentException();
    }

    /**
     * Returns an iterator overall events sorted by start time, which have a certain tag
     *
     * @param start The earliest time for the events to start at
     * @param tag   The tag to search by
     * @return The iterator overall events. This will become invalid if new event collections or individual events are manipulated
     */
    public Iterator<Event> searchEvents(Date start, Tag tag) {
        return new EventIterator(start, e -> tag.hasEvent(e.getName()));
    }

    /**
     * Add a new memo to the system
     * @param text The text of the new memo
     * @throws IllegalArgumentException If a memo with the same text already exists
     */
    public void addMemo(String text) throws IllegalArgumentException {
        if(memos.stream().filter(mt -> mt.getText().equals(text)).anyMatch(mt -> true)){
            throw new IllegalArgumentException();
        }
        memos.add(new Tag(text));
    }

    /**
     * Link new event to a memo
     * @param memoText The memo to add the event to. The memo must already exist
     * @param eventId The id of the event. It must exist
     * @throws IllegalArgumentException if the event or the memo does not exist
     */
    public void linkMemo(String memoText, String eventId) throws IllegalArgumentException {
        Tag memo = memos.stream().filter(m -> m.getText().equals(memoText)).findAny().orElseThrow(null);
        if(getEvent(eventId) == null){
            throw new IllegalArgumentException();
        }
        memo.addEvent(eventId);
    }

    /**
     * Remove link between event and memo
     * @param memoText The memo. It must exist
     * @param eventId The id of the event. It must exist
     * @throws IllegalArgumentException if the event or the memo does not exist
     */
    public void unlinkMemo(String memoText, String eventId) throws IllegalArgumentException {
        Tag memo = memos.stream().filter(m -> m.getText().equals(memoText)).findAny().orElseThrow(null);
        if(getEvent(eventId) == null){
            throw new IllegalArgumentException();
        }
        memo.removeEvent(eventId);
    }

    /**
     * Return all memos which are attributed with a certain event
     *
     * @param eventId The event which memos must be linked to
     * @return Unsorted list of memos
     */
    public List<Tag> getMemos(String eventId) {
        return memos.stream().filter(m -> m.hasEvent(eventId)).collect(Collectors.toList());
    }

    /**
     * Get all memos stored in this calendar
     *
     * @return An unsorted list of memos
     */
    public List<Tag> getMemos() {
        return memos;
    }

    /**
     * Return all events linked to a memo
     *
     * @param memo The memo to search by
     * @return List of events with memo
     */
    public List<Event> getLinkedEvents(Tag memo) {
        return memo.getEvents();
    }

    /**
     * Event Iterator is used to iterate over the individual event collections to get the next time
     */
    private class EventIterator implements Iterator<Event> {
        private Predicate<Event> isValid;
        private List<Iterator<Event>> eventCollectionEventIterators;
        private List<Event> possibleNext;

        /**
         * Initialise a new event iterator which iterates overall event collections at once returning events by their
         * start time.
         * This will not observe changes to the number of event collections so a new one must be created after that occurs
         *
         * @param start The earliest possible time for an event
         * @param isValid A predicate to filer events by. Can be null
         */
        public EventIterator(Date start, Predicate<Event> isValid) {
            if(isValid == null)
                this.isValid = e -> true;
            else
                this.isValid = isValid;
            eventCollectionEventIterators = new ArrayList<>();
            possibleNext = new ArrayList<>();

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

            // Checks if an additional event can be gotten from an iterator
            for (Iterator<Event> iterator :
                    eventCollectionEventIterators) {
                if (iterator.hasNext()) {
                    return true;
                }
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
                if (possibleNext.get(i) == null && eventCollectionEventIterators.get(i).hasNext()) {
                    Event next = null;
                    while (eventCollectionEventIterators.get(i).hasNext()){
                        Event possible = eventCollectionEventIterators.get(i).next();
                        if(isValid.test(possible)){
                            next = possible;
                            break;
                        }
                    }
                    if(next != null)
                        possibleNext.set(i, next);
                }
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
    }
}
