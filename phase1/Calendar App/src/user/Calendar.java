package user;

import entities.*;
import exceptions.InvalidDateException;
import exceptions.PeriodAlreadyExistsException;
import mt.Memo;
import mt.Tag;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * user.Calendar class
 */
public class Calendar {

    List<EventCollection> eventCollections;
    List<AlertCollection> alertCollections;
    List<Memo> memos;
    List<Tag> tags;
    private DataSaver dataSaver;

    /**
     * Constructor for creating a new calendar with no data
     */
    public Calendar(DataSaver dataSaver) {
        this.dataSaver = dataSaver;
        eventCollections = new ArrayList<>();
        alertCollections = new ArrayList<>();
        load();
        if(eventCollections.stream().filter(eC -> eC.getName().equals("")).findAny().orElse(null) == null)
        {
            eventCollections.add(new EventCollection("", new ArrayList<>(), dataSaver));
        }
    }

    /**
     * Constructor for calendar with already existing data
     *
     * @param eventCollections List of  event collections for new calendar
     * @param alertCollections List of alert collections for new calendar
     */
    public Calendar(List<EventCollection> eventCollections, List<AlertCollection> alertCollections) {
        if (eventCollections == null || alertCollections == null) {
            throw new NullPointerException();
        }
        this.eventCollections = eventCollections;
        this.alertCollections = alertCollections;
        load();
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
     * Add an event to the correct event collection
     *
     * @param event    alert.Event to add
     * @param seriesId Series to add event to
     */
    public void createEvent(Event event, String seriesId) throws IllegalArgumentException, IOException {
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
        Event event = getEvent(eventId);
        if (event == null) {
            throw new IllegalArgumentException();
        }
        for (AlertCollection alertCollection : alertCollections) {
            if (alertCollection.getEventId().equals(eventId)) {
                alertCollection.addAlert(start, period);
                return;
            }
        }

        AlertCollection alertCollection = new AlertCollection(event, new DataSaver(""));
        alertCollection.addAlert(start, period);
        alertCollections.add(alertCollection);
    }

    public void createEventSeries(String eventSeriesName, ArrayList<String> eventIds) throws IOException {
        List<Event> events = eventIds.stream().map(this::getEvent).collect(Collectors.toList());
        for (Event e : events) {
            removeFromSeries(e.getId());
        }
        eventCollections.add(new EventCollection(eventSeriesName, events, dataSaver));
    }

    /**
     * Move an event to a series from another series. If the event is already part of that series nothing changes
     *
     * @param eventId    The id of the event to move
     * @param seriesName The name of the new series to add it to
     */
    public void addToSeries(String eventId, String seriesName) throws IllegalArgumentException, IOException {
        EventCollection from = null;
        EventCollection eventCollection = null;
        Event event = null;
        for (EventCollection eC :
                eventCollections) {
            if (eC.getName().equals(seriesName)) {
                eventCollection = eC;
            }
            if (eC.getEvent(eventId) != null) {
                from = eC;
                event = eC.getEvent(eventId);
            }
        }
        if(eventCollection == null || event == null){
            throw new IllegalArgumentException();
        }
        try {
            from.removeEvent(event);
        } catch (InvalidDateException e) {
            throw new IllegalArgumentException();
        }
        eventCollection.addEvent(event);
    }

    /**
     * Remove an event from a series and put in the collection series with no name
     *
     * @param eventId The event to move
     * @throws IllegalArgumentException If the event collection or the event can not be found
     */
    public void removeFromSeries(String eventId) throws IllegalArgumentException, IOException {
        EventCollection from = null;
        EventCollection to = eventCollections.stream().filter(p -> p.getName().equals("")).findAny().orElseThrow(null);
        Event event = null;
        for (EventCollection eventCollection :
                eventCollections) {
            event = eventCollection.getEvent(eventId);
            if (event != null) {
                from = eventCollection;
                break;
            }
        }
        if(event == null){
            throw new IllegalArgumentException();
        }
        try {
            from.removeEvent(event);
        } catch (InvalidDateException e) {
            // Date is invalid
            throw new IllegalArgumentException();
        }
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
     * Make an event within an existing series into a new repeating event either within the same series or within a new one
     * @param eventId Id of the event
     * @param end No event will be created after this time. Can be null if there is no end time
     * @param difference The time difference between two created events
     * @throws IllegalArgumentException Will throw when no event collection was found
     */
    public void makeEventToSeries(String eventId, Date end, Date difference, String seriesName) throws IllegalArgumentException, InvalidDateException, IOException {
        for (EventCollection eventCollection :
                eventCollections) {
            if (eventCollection.getEvent(eventId) != null) {
                if(!eventCollection.getName().equals(seriesName)){
                    Event event =  eventCollection.getEvent(eventId);
                    eventCollection.removeEvent(event);
                    EventCollection newEventCollection = new EventCollection(seriesName, new ArrayList<>(), dataSaver);
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
     *
     * @param eventId alert.Event the tag is added to
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
                save();
                return;
            }
        }
        throw new IllegalArgumentException();
    }

    /**
     * Remove tag to an event
     *
     * @param eventId alert.Event the tag is removed from
     * @param tagName Name of the tag to be removed
     * @throws IllegalArgumentException If the event or the tag cannot be found
     */
    public void removeTagFromEvent(String eventId, String tagName) throws IllegalArgumentException {
        Tag tag = tags.stream().filter(t -> t.getText().equals(tagName)).findAny().orElseThrow(null);
        for (EventCollection eventCollection :
                eventCollections) {
            if (eventCollection.getEvent(eventId) != null) {
                eventCollection.removeTag(eventId, tag);
                save();
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
    public void addMemo(String title, String text) throws IllegalArgumentException {
        if(memos.stream().filter(mt -> mt.getText().equals(text)).anyMatch(mt -> true)){
            throw new IllegalArgumentException();
        }
        memos.add(new Memo(title, text));
        save();
    }

    /**
     * Link new event to a memo
     * @param memoTitle The title of the memo to add the event to. The memo must already exist
     * @param eventId The id of the event. It must exist
     * @throws IllegalArgumentException if the event or the memo does not exist
     */
    public void linkMemo(String memoTitle, String eventId) throws IllegalArgumentException {
        Memo memo = memos.stream().filter(m -> m.getTitle().equals(memoTitle)).findAny().orElseThrow(null);
        if(getEvent(eventId) == null){
            throw new IllegalArgumentException();
        }
        memo.addEvent(eventId);
        save();
    }

    /**
     * Remove link between event and memo
     * @param memoText The memo. It must exist
     * @param eventId The id of the event. It must exist
     * @throws IllegalArgumentException if the event or the memo does not exist
     */
    public void unlinkMemo(String memoText, String eventId) throws IllegalArgumentException {
        Memo memo = memos.stream().filter(m -> m.getText().equals(memoText)).findAny().orElseThrow(null);
        if(getEvent(eventId) == null){
            throw new IllegalArgumentException();
        }
        memo.removeEvent(eventId);
        if(memo.getEvents().size() == 0){
            memos.remove(memo);
        }
        save();
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

    private void save(){
        // save memos
        StringBuilder memoData = new StringBuilder();
        for (Memo memo :
                memos) {
            StringBuilder ids = new StringBuilder();
            for (String id :
                    memo.getEvents()) {
                ids.append(id).append("|");
            }
            memoData.append(memo.getText()).append("§").append(memo.getTitle()).append("§").append(ids.toString()).append(String.format("%n"));
        }
        try {
            dataSaver.saveToFile("memos.txt", memoData.toString());
        } catch (IOException ignored) {
            ignored.printStackTrace();
        }

        // save tags
        StringBuilder tagsData = new StringBuilder();
        for (Tag tag :
                tags) {
            StringBuilder ids = new StringBuilder();
            for (String id :
                    tag.getEvents()) {
                ids.append(id).append("|");
            }
            memoData.append(tag.getText()).append("§").append(ids.toString()).append(String.format("%n"));
        }
        try {
            dataSaver.saveToFile("tags.txt", memoData.toString());
        } catch (IOException ignored) {
            ignored.printStackTrace();
        }
    }

    /**
     *
     */
    public void load() {
        //load memos
        try {
            memos = new ArrayList<>();
            Scanner scanner = dataSaver.loadScannerFromFile("memos.txt");
            while (scanner.hasNext()) {
                String memoData = scanner.nextLine();
                String[] parts = memoData.split("[§]+");
                //Split ids
                List<String> idStrings = new ArrayList<>(Arrays.asList(parts[2].split("[|]+")));
                memos.add(new Memo(parts[0], parts[1], idStrings));
            }
        } catch (FileNotFoundException e) {

        }
        //load tags
        try {
            tags = new ArrayList<>();
            Scanner scanner = dataSaver.loadScannerFromFile("tags.txt");
            while (scanner.hasNext()){
                String tagData = scanner.nextLine();
                String[] parts = tagData.split("[§]+");
                //Split ids
                List<String> idStrings = new ArrayList<>(Arrays.asList(parts[1].split("[|]+")));
                tags.add(new Tag(parts[0], idStrings));
            }
        } catch (FileNotFoundException e) {

        }
        //Load existing event collection series
        File[] files = dataSaver.getFilesInDirectory("/events");
        if(files != null){
            for (File file :
                    files) {
                String name = file.getName();
                name = name.replaceAll(".txt", "");
                try {
                    eventCollections.add(new EventCollection(name, dataSaver));
                } catch (InvalidDateException e) {
                    System.out.println("Failed to load events: " + name);
                }
            }
        }
        //Load existing alert collection series
        files = dataSaver.getFilesInDirectory("/alerts/");
        if(files != null){
            for (File file :
                    files) {
                String name = file.getName();
                name = name.replaceAll(".txt", "");
                alertCollections.add(new AlertCollection(name, dataSaver));
            }
        }
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

    /**
     * Try and add an alert collection for an event
     * @param eventId Id of the evemt
     * @return True if a new alert collection was created; False if an alert collection already exists
     */
    public boolean addAlertCollection(String eventId) {
        for (AlertCollection alertCollection :
                alertCollections) {
            if (eventId.equals(alertCollection.getEventId())){
                return false;
            }
        }
        alertCollections.add(new AlertCollection(getEvent(eventId), dataSaver));
        return true;
    }

    /**
     * Return the corresponding alert collection to an event
     * @param eventId Id of event
     * @return Alert collection if it exists, otherwise null
     */
    public AlertCollection getAlertCollection(String eventId) {
        for (AlertCollection alertCollection :
                alertCollections) {
            if (alertCollection.getEventId().equals(eventId)){
                return alertCollection;
            }
        }
        return null;
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
                Iterator<Event> iterator = eventCollectionEventIterators.get(i);
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
        return (GregorianCalendar)GregorianCalendar.getInstance();
    }

    public void editMemoTitle(String memoName, String newMemoName){
        Memo memo = memos.stream().filter(m -> m.getTitle().equals(memoName)).findAny().orElseThrow(null);
        memo.setTitle(newMemoName);
        save();
    }

    public void editMemoText(String memoName, String newMemoText){
        Memo memo = memos.stream().filter(m -> m.getTitle().equals(memoName)).findAny().orElseThrow(null);
        memo.setText(newMemoText);
        save();
    }

    /**
     * Remove the memo from all memos. Saves memos
     * @param memo Memo to remove
     */
    public void removeMemo(Memo memo){
        memos.remove(memo);
        save();
    }

    /**
     * Gets a memo by its title
     * @param name Title of the memo
     * @return Returns the memo with the corresponding title, if no memo is found returns null
     */
    public Memo getMemo(String name){
        return memos.stream().filter(m -> m.getTitle().equals(name)).findAny().orElse(null);
    }

    public void removeEvent(Event event) throws InvalidDateException, IOException {
        eventCollections.stream().filter(eC -> eC.getEvent(event.getId()) != null).findAny().orElseThrow(null).removeEvent(event);
    }

    public List<String> getEventSeriesNames(){
        return eventCollections.stream().map(EventCollection::getName).filter(f -> !f.equals("")).collect(Collectors.toList());
    }
}
