import exceptions.InvalidDateException;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Observable;

/**
 * Event class
 */
public class Event extends Observable{

    private String id;
    private String name;
    private GregorianCalendar startDate;
    private GregorianCalendar endDate;

    /**
     * Constructor for a new Event
     * @param id id of the Event
     * @param name name of the Event
     * @param startDate start time of the Event
     * @param endDate end time of the Event
     */
    public Event (String id, String name, GregorianCalendar startDate, GregorianCalendar endDate)
            throws InvalidDateException{
        this.id = id;
        this.name = name;
        if ( startDate.after(endDate) ) {
            throw new InvalidDateException();
        } else {
            this.startDate = startDate;
            this.endDate = endDate;
        }
    }

    /**
     * Return the name of the Event
     * @return the name of the Event
     */
    public String getName() {
        return name;
    }

    /**
     * Return the id of the Event
     * @return the id of the Event
     */
    public String getId() {
        return id;
    }

    /**
     * Return the start time of the Event
     * @return the startDate of the Event
     */
    public GregorianCalendar getStartDate() { return startDate; }

    /**
     * Return the end time of the Event
     * @return the endDate of the Event
     */
    public GregorianCalendar getEndDate() {return endDate;}

    /**
     * Set the name of the Event
     * @param newName the new name of the Event
     */
    public void setName(String newName) { this.name = newName; }

    /**
     * Set the start time of the Event
     * @param newStart the new startDate of the Event
     */
    public void setStartDate(GregorianCalendar newStart) throws InvalidDateException {
       if ( newStart.after(endDate) ) {
           throw new InvalidDateException();
       } else {
           this.startDate = newStart;
           setChanged();
           notifyObservers(this.getDuration());
       }
    }

    /**
     * Set the end time of the Event
     * @param newEnd the new endDate of the Event
     */
    public void setEndDate(GregorianCalendar newEnd) throws InvalidDateException {
        if ( newEnd.before(startDate) ) {
            throw new InvalidDateException();
        } else {
            this.endDate = newEnd;
            setChanged();
            notifyObservers(this.getDuration());
        }
    }

    /**
     * Return the duration of the Event in Date
     * @return duration of the Event
     */
    public GregorianCalendar getDuration() {
        GregorianCalendar newGC = new GregorianCalendar();
        if (startDate.equals(endDate)) {
            Date d1 = new Date(0);
            newGC.setGregorianChange(d1);
        } else {
            long dur = startDate.getTime().getTime() - endDate.getTime().getTime();
            Date d2 = new Date(dur);
            newGC.setGregorianChange(d2);
        }
        return newGC;
    }

    public String toString() {
        String start = startDate.getTime().toString();
        String end = endDate.getTime().toString();
        return name + " from " + start + " to " + end;
    }

}