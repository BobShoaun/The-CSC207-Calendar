package event;

import exceptions.InvalidDateException;

import java.util.GregorianCalendar;
import java.util.Observable;

/**
 * Event class representing an Event
 */
public class Event extends Observable implements Cloneable, Comparable<Event> {

    private String id;
    private String name;
    private GregorianCalendar startDate;
    private GregorianCalendar endDate;
    private boolean postponed;

    /**
     * Get postponed status for this event
     *
     * @return True iff this Event is postponed
     */
    public boolean isPostponed() {
        return postponed;
    }

    /**
     * Set postponed status for this Event
     *
     * @param postponed Boolean for postponedness
     */
    public void setPostponed(boolean postponed) {
        this.postponed = postponed;
    }

    /**
     * Constructor for a new Event
     *
     * @param name      name of the Event
     * @param startDate start time of the Event
     * @param endDate   end time of the Event
     */
    public Event(String name, GregorianCalendar startDate, GregorianCalendar endDate)
            throws InvalidDateException {
        this.id = IDManager.generateEventId(name, startDate);
        this.name = name;
        this.postponed = false;
        if (startDate.after(endDate)) {
            throw new InvalidDateException();
        } else {
            this.startDate = startDate;
            this.endDate = endDate;
        }
    }

    /**
     * Return the name of the Event
     *
     * @return the name of the Event
     */
    public String getName() {
        return name;
    }

    /**
     * Return the id of the Event
     *
     * @return the id of the Event
     */
    public String getId() {
        return id;
    }

    /**
     * Return the start time of the Event
     *
     * @return the startDate of the Event
     */
    public GregorianCalendar getStartDate() {
        return startDate;
    }

    /**
     * Return the end time of the Event
     *
     * @return the endDate of the Event
     */
    public GregorianCalendar getEndDate() {
        return endDate;
    }

    /**
     * Set the name of the Event
     *
     * @param newName the new name of the Event
     */
    public void setName(String newName) {
        name = newName;
        id = IDManager.generateEventId(name, startDate);
        setChanged();
        notifyObservers(null);
    }

    /**
     * Set the start time of the Event
     *
     * @param newStart the new startDate of the Event
     */
    public void setStartDate(GregorianCalendar newStart) throws InvalidDateException {
        if (newStart.after(endDate)) {
            throw new InvalidDateException();
        } else {
            id = IDManager.generateEventId(name, startDate);
            startDate = newStart;
            setChanged();
            notifyObservers(startDate);
        }
    }

    /**
     * Set the end time of the Event
     *
     * @param newEnd the new endDate of the Event
     */
    public void setEndDate(GregorianCalendar newEnd) throws InvalidDateException {
        if (newEnd.before(startDate)) {
            throw new InvalidDateException();
        } else {
            this.endDate = newEnd;
            setChanged();
            notifyObservers(null);
        }
    }

    /**
     * Shift the event to the new start time and notify Observer.
     *
     * @param shifted the new startDate of the Event
     */
    public void shiftEvent(GregorianCalendar shifted) {
        long diff = shifted.getTimeInMillis() - startDate.getTimeInMillis();
        long newStartMillis = startDate.getTimeInMillis() + diff;
        long newEndMillis = endDate.getTimeInMillis() + diff;
        startDate.setTimeInMillis(newStartMillis);
        endDate.setTimeInMillis(newEndMillis);
        setChanged();
        notifyObservers(this.startDate);
    }

    /**
     * Return the duration of the Event in Date
     *
     * @return duration of the Event
     */
    public long getDuration() {
        if (startDate.equals(endDate)) {
            return 0;
        } else {
            return endDate.getTime().getTime() - startDate.getTime().getTime();
        }
    }

    /**
     * Return the String representation of this Event
     *
     * @return the String representation of the Event
     */
    public String toString() {
        String start = startDate.getTime().toString();
        String end = endDate.getTime().toString();
        return name + " from " + start + " to " + end;
    }

    /**
     * Return the String representation of this Event with Date as milliseconds
     *
     * @return the String representation of this Event
     */
    public String getString() {
        return id + "\n" +
                name + "\n" +
                startDate.getTimeInMillis() + "\n" +
                endDate.getTimeInMillis();
    }

    /**
     * @param compareEvent the Event to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     */
    @Override
    public int compareTo(Event compareEvent) {
        long time = compareEvent.getStartDate().getTimeInMillis();
        return (int) (this.getStartDate().getTimeInMillis() - time);
    }

    /**
     * Return a clone of this event
     *
     * @return a clone of this event
     */
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}