package user;

import alert.Alert;
import alert.AlertCollection;
import dates.TimeController;
import event.Event;
import event.EventCollection;
import event.Series;
import exceptions.InvalidDateException;
import exceptions.NoSuchSeriesException;
import gui.AlertController;
import memotag.Memo;
import memotag.Tag;

import java.time.Duration;
import java.util.*;

/**
 * Calendar class which acts as a facade for Alerts, Memos, Tags, and Events
 */
public class Calendar {

    private final String name;
    private final AlertCollectionManager alertCollectionManager;
    private final MemoManager memoManager;
    private final TagManager tagManager;
    private final EventManager eventManager;
    private final TimeController timeController;
    private final DataSaver dataSaver;

    /**
     * Constructor for creating a new calendar with no data
     */
    public Calendar(String name, DataSaver dataSaver) {
        this.name = name;
        this.memoManager = new MemoManager(new ArrayList<>(), dataSaver);
        this.eventManager = new EventManager(new ArrayList<>(), dataSaver);
        timeController = new TimeController();
        this.dataSaver = dataSaver;
        this.alertCollectionManager = new AlertCollectionManager(dataSaver);
        tagManager = new TagManager(new ArrayList<>(), dataSaver);
    }

    /**
     * @return the single regular list of Event
     */
    public EventCollection getManualEventCollection() {
        return eventManager.getManualEventCollection();
    }

    /**
     * @return the list of series
     */
    public List<Series> getSeries() {
        return eventManager.getSeries();
    }

    /**
     * @param name of the series to be searched
     * @return the Series with this name
     */
    public Series getSeries(String name) throws NoSuchSeriesException {
        return eventManager.getSeries(name);
    }

    public String getName() {
        return name;
    }

    /**
     * Constructor for calendar with already existing data
     *
     * @param name             Name of the calendar
     * @param eventCollections List of  event collections for new calendar
     */
    public Calendar(String name,
                    List<EventCollection> eventCollections,
                    List<Memo> memos,
                    List<Tag> tags,
                    DataSaver dataSaver) {
        if (eventCollections == null) {
            throw new NullPointerException();
        }
        this.name = name;
        this.eventManager = new EventManager(eventCollections, dataSaver);
        this.memoManager = new MemoManager(memos, dataSaver);
        timeController = new TimeController();
        this.dataSaver = dataSaver;

        this.alertCollectionManager = new AlertCollectionManager(dataSaver);
        tagManager = new TagManager(tags, dataSaver);
    }

    /**
     * Get all alert collections
     *
     * @return List of all alert collections
     */
    public List<AlertCollection> getAlertCollections() {
        return alertCollectionManager.getAlertCollections();
    }

    /**
     * Get all event collections
     *
     * @return List of all event collections including all series
     */
    public List<EventCollection> getEventCollections() {
        return eventManager.getEventCollections();
    }

    /**
     * @param event the Event to be searched
     * @return the EventCollection that this event belongs to
     */
    public EventCollection getEventCollection(Event event) {
        return eventManager.getEventCollection(event);
    }


    /**
     * Return all events which occur at day of that event
     *
     * @param date Date during which the event should occur
     * @return Unsorted list of all events which occur at that day
     */
    public List<Event> getEvents(GregorianCalendar date) {

        return eventManager.getEvents(date);
    }

    /**
     * Gets all events which either start or end during this time period. The endpoints are INCLUDED
     *
     * @param start Earliest time for an event to end
     * @param end   Latest time for an event to end
     * @return List of all events where start point <= end and end point >= start
     */
    public List<Event> getEvents(GregorianCalendar start, GregorianCalendar end) {
        return eventManager.getEvents(start, end);
    }

    /**
     * Returns an iterator to get future events sorted by start time.
     * [Warning] The iterator will not be updated and become unusable if the number of event collections change
     *
     * @param start The earliest possible start time of the returned events
     * @return Null if no event collections exist, otherwise an Iterator<alert.Event>
     */
    public Iterator<Event> getFutureEvents(Date start) {
        return eventManager.getFutureEvents(start);
    }

    /**
     * Will return the event with the given id.
     *
     * @param id ID of the requested event
     * @return Return the event, if not found return null;
     */
    public Event getEvent(String id) {
        return eventManager.getEvent(id);
    }

    /**
     * Return sorted alerts which occur in [start, end]
     *
     * @param start The inclusive start time
     * @param end   The inclusive end time
     * @return All alerts in sorted order
     */
    public List<Alert> getAlerts(GregorianCalendar start, GregorianCalendar end) {
        return alertCollectionManager.getAlerts(start, end);
    }

    /**
     * Create a new event series
     *
     * @param event the event that is series is modeled upon
     * @throws InvalidDateException If incorrect data is passed in
     */
    public void addEventSeries(Event event, String seriesName) throws InvalidDateException {
        eventManager.addEventSeries(event,seriesName);
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
        eventManager.addEventSeries(name, start, end, difference, baseEvent);
    }


    /**
     * Get the memo attributed with a certain event
     *
     * @param event The event which memos must be linked to
     * @return Memo, null if non is found
     */
    public Memo getMemo(Event event) {
        return memoManager.getMemo(event);
    }

    /**
     * Get all memos stored in this calendar
     *
     * @return An unsorted list of memos
     */
    public List<Memo> getMemos() {
        return memoManager.getMemos();
    }

    /**
     * Gets a memo by its title
     *
     * @param name Title of the memo
     * @return Returns the memo with the corresponding title, if no memo is found returns null
     */
    public Memo getMemo(String name) {
        return memoManager.getMemo(name);
    }

    /**
     * Gets a memo by its title and content
     *
     * @param name Title of the memo
     * @return Returns the memo with the corresponding title and content, if no memo is found returns null
     */
    public Memo getMemo(String name, String content) {
        return memoManager.getMemo(name, content);
    }

    /**
     * Add a new memo
     *
     * @param memo Memo to add
     */
    public void addMemo(Memo memo) {
        memoManager.addMemo(memo);
    }

    /**
     * edits the memo title
     *
     * @param memoName    Name of the memo to edit
     * @param newMemoName New name of the memo
     */
    public void editMemoTitle(String memoName, String newMemoName) {
        memoManager.editMemoTitle(memoName, newMemoName);
    }

    /**
     * edits a memo text
     *
     * @param memoName    Name of the memo to edit
     * @param newMemoText The new text for this memo
     */
    public void editMemoText(String memoName, String newMemoText) {
        memoManager.editMemoText(memoName, newMemoText);
    }

    /**
     * Remove the memo from all memos. Saves memos
     *
     * @param memo Memo to remove
     */
    public void removeMemo(Memo memo) {
        memoManager.removeMemo(memo);
    }

    /**
     * Return all events linked to a memo
     *
     * @param memo The memo to search by
     * @return List of events with memo
     */
    public List<Event> getLinkedEvents(Memo memo) {
        return eventManager.getLinkedEvents(this, memo);
    }

    /**
     * Get the event collection with a certain name
     *
     * @param eventSeriesName Name of the event collection
     * @return Event collection if it exists otherwise null
     */
    public EventCollection getEventCollection(String eventSeriesName) {
        return eventManager.getEventCollection(eventSeriesName);
    }

    /**
     * Get all events with a certain name
     *
     * @param eventName The name of the event to search by
     * @return Iterator with all events with same name
     */
    public Iterator<Event> getEvents(String eventName) {
        return eventManager.getEvents(eventName);
    }

    /**
     * Get all tags
     *
     * @return List of all tags
     */
    public List<Tag> getTags() {
        return tagManager.getTags();
    }

    /**
     * Get tag with a certain name
     *
     * @param tagName Name of the tag
     * @return The corresponding tag, otherwise null
     */
    public Tag getTag(String tagName) {
        return tagManager.getTag(tagName);
    }

    /**
     * Return all tags which are attributed with a certain event
     *
     * @param event The event which tag must be linked to
     * @return Unsorted list of tags
     */
    public List<Tag> getTags(Event event) {
        return tagManager.getTags(event);
    }

    /**
     * Add a new tag
     *
     * @param tag The tag to add
     */
    public void addTag(Tag tag) {
        tagManager.addTag(tag);
    }

    /**
     * remove event from tags
     *
     * @param tag   the text of the tag
     * @param event Event to be removed
     */
    public void removeTag(String tag, Event event) {
        tagManager.removeTag(tag, event);
    }

    /**
     * Remove all old alerts
     */
    public void removeOldAlerts() {
        alertCollectionManager.removeOldAlerts();
    }

    /**
     * Get data saver
     *
     * @return The data saver instance
     */
    public DataSaver getDataSaver() {
        return dataSaver;
    }

    /**
     * Set the current time of the time controller
     *
     * @param gregorianCalendar Value to be set
     */
    public void setTime(GregorianCalendar gregorianCalendar) {
        timeController.setCurrentTime(gregorianCalendar);
    }

    /**
     * Get all postponed events
     *
     * @return List of all postponed events
     */
    public List<Event> getPostponedEvents() {
        return eventManager.getPostponedEvents();
    }

    /**
     * Changes the name of an event
     *
     * @param event Event to change name of
     * @param name  new name
     */
    public void renameEvent(Event event, String name) {
        eventManager.renameEvent(event, name);
    }


    /**
     * Get the current time of the calendar
     *
     * @return The current time
     */
    public GregorianCalendar getTime() {
        return (GregorianCalendar) timeController.getTime().clone();
    }

    /**
     * remove event
     *
     * @param event The event to be removed
     * @throws InvalidDateException Internal error
     */
    public void removeEvent(Event event) throws InvalidDateException {
        eventManager.removeEvent(event);
    }

    /**
     * get all event series's name
     *
     * @return A list containing all the names of all event series in order of internal representation
     */
    public List<String> getEventSeriesNames() {
        return eventManager.getEventSeriesNames();
    }

    /**
     * Get the memo manager
     *
     * @return This calendar's memoManager
     */
    public MemoManager getMemoManager() {
        return memoManager;
    }

    /**
     * Get the event manager
     *
     * @return This calendar's eventManager
     */
    public EventManager getEventManager() {
        return eventManager;
    }

    /**
     * Get the tag manager
     *
     * @return This calendar's tagManager
     */
    public TagManager getTagManager() {
        return tagManager;
    }

    /**
     * Remove an event collection from the event manager
     *
     * @param eventCollection Event collection to remove
     */
    public void removeEventCollection(EventCollection eventCollection) {
        eventManager.removeEventCollection(eventCollection);
    }

    /**
     * Remove all alerts with a specified toString
     *
     * @param alertToString toString of alert to be removed
     */
    public void removeAlert(String alertToString) {
        alertCollectionManager.removeAlert(alertToString);
    }

    /**
     * Get the alert collection manager
     * @return The alert collection manager. Is not null
     */
    public AlertCollectionManager getAlertCollectionManager(){
        return alertCollectionManager;
    }
}
