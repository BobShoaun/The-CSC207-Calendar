import java.util.*;

/**
 * Calendar class
 */
public class Calendar {

    List<EventCollection> eventCollections;
    List<AlertCollection> alertCollections;
    List<MT> memos;
    List<MT> tags;


    /**
     * Constructor for creating a new calendar with no data
     */
    public Calendar () {
        eventCollections = new ArrayList<>();
        alertCollections = new ArrayList<>();
        memos = new ArrayList<>();
        tags = new ArrayList<>();
    }

    /**
     * Constructor for calendar with already existing data
     * @param eventCollections List of  event collections for new calendar
     * @param alertCollections List of alert collections for new calendar
     * @param memos List of memos for new calendar
     * @param tags List of tags for new calendar
     */
    public Calendar(List<EventCollection> eventCollections, List<AlertCollection> alertCollections, List<MT> memos,
                    List<MT> tags) {
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
     * @param date Date during which the event should occur
     * @return A list of events which occur at that time
     */
    public List<Event> getEvents(Date date){
        List<Event> toReturn = new ArrayList<>();

        for (EventCollection eventCollection :
                eventCollections) {
            toReturn.addAll(eventCollection.getEvents(date));
        }

        return toReturn;
    }

    /**
     * Gets all events which either start or end during this time period. The endpoints are INCLUDED
     * @param start Earliest time for an event to end
     * @param end Latest time for an event to end
     * @return List of all events where start point <= end and end point >= start
     */
    public List<Event> getEvents(Date start, Date end){
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
     * @param start The earlist possible start time of the returned events
     * @return Null if no event collections exist, otherwise an Iterator<Event>
     */
    public Iterator<Event> getFutureEvents(Date start){
        if(eventCollections.size() == 0) {
            return null;
        }
        return new EventIterator(start);
    }

    public Event getEvent(String id){
        for (EventCollection eventCollection :
                eventCollections) {
            Event event = eventCollection.getEvent(id);
        }
    }

    /**
     * Event Iterator is used to iterate over the individual event collections to get the next time
     */
    private class EventIterator implements Iterator<Event> {
        Date current;
        List<Iterator<Event>> eventCollectionEventIterators;
        List<Event> possibleNext;

        /**
         * Initialise a new event iterator which iterates overall event collections at once returning events by their
         * start time.
         * This will not observe changes to the number of event collections so a new one must be created after that occurs
         * @param start The earliest possible time for an event
         */
        public EventIterator(Date start){
            current = start;
            eventCollectionEventIterators = new ArrayList<>();
            possibleNext = new ArrayList<>();

            for (EventCollection eventCollection :
                    eventCollections) {
                eventCollectionEventIterators.add(eventCollection.getEventIterator(start));
                possibleNext.add(null);
            }
        }

        /**
         * Returns true if any of the indiviudal event collection event iterators still has an item
         * @return If there are further events in the future
         */
        @Override
        public boolean hasNext() {
            // Checks if any event is still cached
            for (Event event :
                    possibleNext) {
                if (event != null)){
                    return true;
                }
            }

            // Checks if an additional event can be gotten from an iterator
            for (Iterator<Event> iterator :
                    eventCollectionEventIterators) {
                if (iterator.hasNext()){
                    return true;
                }
            }
            return false;
        }

        /**
         * Return the next event in any of the collections sorted by start time
         * @return The next event. This will never be null
         */
        @Override
        public Event next() {
            //Update the possible next list to include values from all event collection iterators which still have events
            for (int i = 0; i < possibleNext.size(); i++) {
                if(possibleNext.get(i) == null && eventCollectionEventIterators.get(i).hasNext()){
                    possibleNext.set(i, eventCollectionEventIterators.get(i).next());
                }
            }

            // Select the earliest event
            int index = -1;
            Event first = null;
            for (Event event :
                    possibleNext) {
                if (event != null){
                    if(first == null || first.getStartDate().after(event.getStartDate())){
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
