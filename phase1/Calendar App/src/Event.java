import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.lang.model.element.NestingKind;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

/**
 * Event class
 */
public class Event implements Observer{

    private String id;
    private String name;
    private String description;
    private Date startDate;
    private Date endDate;

    /**
     * Constructor for a new Event
     * @param id id of the Event
     * @param name name of the Event
     * @param startDate start time of the Event
     * @param endDate end time of the Event
     */
    public Event (String id, String name, Date startDate, Date endDate) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Return the name of the Event
     * @return the name of the Event
     */
    public String getName () {
        return name;
    }

    /**
     * Return the start time of the Event
     * @return the startDate of the Event
     */
    public Date getStartDate() {return startDate; }

    /**
     * Set the name of the Event
     * @param newName the new name of the Event
     */
    public void setName(String newName) { this.name = newName; }

    /**
     * Set the description of the Event
     * @param d the Event description
     */
    public void setDescription(String d) { this.description = d; }

    /**
     * Set the start time of the Event
     * @param newStart the new startDate of the Event
     */
    public void setStartDate(Date newStart) { this.startDate = newStart; }

    /**
     * Set the end time of the Event
     * @param newEnd the new endDate of the Event
     */
    public void setEndDate(Date newEnd) { this.endDate = newEnd; }

    /**
     * Return the duration of the Event in Date
     * @return duration of the Event
     */
    public Date getDuration() {
        long dur = startDate.getTime() - endDate.getTime();
        return new Date(dur);
    }

    /**
     * Updates from the observable AlertCollection
     * @param o the observable AlertCollection
     * @param arg
     */
    public void update(Observable o, Object arg) {
        throw new NotImplementedException();
    }

    public String getId() {
        return id;
    }
}
