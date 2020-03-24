package user;

import entities.*;
import exceptions.InvalidDateException;
import mt.Memo;
import mt.Tag;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * user.Calendar class
 */
public class Calendar {

    private String name;
    private List<EventCollection> eventCollections;
    private List<AlertCollection> alertCollections;
    private List<Memo> memos;
    private List<Tag> tags;
    private DataSaver dataSaver;
    private TimeController timeController;

    /**
     * Constructor for creating a new calendar with no data
     */
    public Calendar(String name, DataSaver dataSaver) {
        this.name = name;
        this.dataSaver = dataSaver;
        eventCollections = new ArrayList<>();
        alertCollections = new ArrayList<>();
        memos = new ArrayList<>();
        tags = new ArrayList<>();
        eventCollections.add(new EventCollection("", new ArrayList<>(), dataSaver));
        timeController = new TimeController();
        try {
            addEventSeries("Shared");
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public String getName () { return name; }

    /**
     * Constructor for calendar with already existing data
     *
     * @param name Name of the calendar
     * @param eventCollections List of  event collections for new calendar
     * @param alertCollections List of alert collections for new calendar
     */
    public Calendar(String name, List<EventCollection> eventCollections, List<AlertCollection> alertCollections,
                    List<Memo> memos, List<Tag> tags, DataSaver dataSaver) {
        if (eventCollections == null || alertCollections == null) {
            throw new NullPointerException();
        }
        this.name = name;
        this.eventCollections = eventCollections;
        this.alertCollections = alertCollections;
        this.tags = tags;
        this.memos = memos;
        timeController = new TimeController();
        this.dataSaver = dataSaver;
    }

    public List<AlertCollection> getAlertCollections() {
        return alertCollections;
    }

    public List<EventCollection> getEventCollections() {
        return eventCollections;
    }

    public List<Tag> getTags() {
        return tags;
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
     * Create a new event series
     * @param eventSeriesName name of the new series
     * @throws IOException If saving error occurs
     */
    public void addEventSeries(String eventSeriesName) throws IOException {
        eventCollections.add(new EventCollection(eventSeriesName, new ArrayList<>(), dataSaver));
    }


    /**
     * Add an event series either to an existing series or it creates a new series
     * @param name Name of the event collection it is added to
     * @param start Time of the first repeating event
     * @param end No event will occur after this time. Can be null if event will go on for ever
     * @param difference The time difference between two created events
     * @param baseEvent The event on which the other events will be based
     */
    public void addEventSeries(String name, Date start, Date end, Date difference, Event baseEvent) throws InvalidDateException, IOException {
        for (EventCollection eventCollection :
                eventCollections) {
            if (eventCollection.getName().equals(name)) {
                eventCollection.addRepeatingEvent(baseEvent, start, end, difference);
                return;
            }
        }
        EventCollection eventCollection = new EventCollection(name, new ArrayList<>(), dataSaver);
        eventCollection.addRepeatingEvent(baseEvent, start, end, difference);
        eventCollections.add(eventCollection);
    }


    /**
     * Return all memos which are attributed with a certain event
     *
     * @param eventId The event which memos must be linked to
     * @return Unsorted list of memos
     */
    public List<Memo> getMemos(String eventId) {
        return memos.stream().filter(m -> m.hasEvent(eventId)).collect(Collectors.toList());
    }

    /**
     * Get all memos stored in this calendar
     *
     * @return An unsorted list of memos
     */
    public List<Memo> getMemos() {
        return memos;
    }

    /**
     * Return all events linked to a memo
     *
     * @param memo The memo to search by
     * @return List of events with memo
     */
    public List<Event> getLinkedEvents(Memo memo) {
        return memo.getEvents().stream().map(this::getEvent).collect(Collectors.toList());
    }

    public EventCollection getEventCollection(String eventSeriesName) {
        return eventCollections.stream().filter(eC -> eC.getName().equals(eventSeriesName)).findAny().orElse(null);
    }

    public Iterator<Event> getEvents(String eventName) {
        return new EventIterator(new Date(0), (Event e) -> e.getName().equals(eventName));
    }

    public Tag getTag(String tag) {
        return tags.stream().filter(t -> t.getText().equals(tag)).findAny().orElse(null);
    }

    public void removeOldAlerts() {
        for (AlertCollection aC :
                alertCollections) {
            aC.removeOldAlerts();
        }
    }

    /**
     * alert.Event Iterator is used to iterate over the individual event collections to get the next time
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

            // Checks if an additional event can be gotten from an iterator,
            for (int i = 0; i < eventCollectionEventIterators.size(); i++) {
                findNextInIterator(i);
                if(possibleNext.get(i) != null)
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

        private void findNextInIterator(int i) {
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
    }

    public GregorianCalendar getTime(){
        return timeController.getTime();
    }

    /**
     * edits the memo title
     * @param memoName Name of the memo to edit
     * @param newMemoName New name of the memo
     */
    public void editMemoTitle(String memoName, String newMemoName){
        Memo memo = memos.stream().filter(m -> m.getTitle().equals(memoName)).findAny().orElseThrow(null);
        memo.setTitle(newMemoName);
        dataSaver.SaveCalendar(this);
    }

    /**
     * edits a memo text
     * @param memoName Name of the memo to edit
     * @param newMemoText The new text for this memo
     */
    public void editMemoText(String memoName, String newMemoText){
        Memo memo = memos.stream().filter(m -> m.getTitle().equals(memoName)).findAny().orElseThrow(null);
        memo.setText(newMemoText);
        dataSaver.SaveCalendar(this);
    }

    /**
     * Remove the memo from all memos. Saves memos
     * @param memo Memo to remove
     */
    public void removeMemo(Memo memo){
        memos.remove(memo);
        dataSaver.SaveCalendar(this);
    }

    /**
     * Gets a memo by its title
     * @param name Title of the memo
     * @return Returns the memo with the corresponding title, if no memo is found returns null
     */
    public Memo getMemo(String name){
        return memos.stream().filter(m -> m.getTitle().equals(name)).findAny().orElse(null);
    }

    /**
     * remove event
     * @param event The event to be removed
     * @throws InvalidDateException Internal error
     * @throws IOException Internal error when saving
     */
    public void removeEvent(Event event) throws InvalidDateException, IOException {
        eventCollections.stream().filter(eC -> eC.getEvent(event.getId()) != null).findAny().orElseThrow(null).removeEvent(event);
    }

    /**
     * get all event series's name
     * @return A list containing all the names of all event series in order of internal representation
     */
    public List<String> getEventSeriesNames(){
        return eventCollections.stream().map(EventCollection::getName).filter(f -> !f.equals("")).collect(Collectors.toList());
    }
}
