import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import javax.lang.model.element.NestingKind;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Observable;
import java.util.Observer;

/**
 * Event class
 */
public class Event extends Observable{

    private String id;
    private String name;
    private String description;
    private GregorianCalendar startDate;
    private GregorianCalendar endDate;

    /**
     * Constructor for a new Event
     * @param id id of the Event
     * @param name name of the Event
     * @param startDate start time of the Event
     * @param endDate end time of the Event
     */
    public Event(String id, String name, GregorianCalendar startDate, GregorianCalendar endDate) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
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
    public void setStartDate(GregorianCalendar newStart) {
        this.startDate = newStart;
        setChanged();
        notifyObservers(newStart);
    }

    /**
     * Set the end time of the Event
     * @param newEnd the new endDate of the Event
     */
    public void setEndDate(GregorianCalendar newEnd) {
        this.endDate = newEnd;
        setChanged();
        notifyObservers(newEnd);
    }

    /**
     * Return the duration of the Event in Date
     * @return duration of the Event
     */
    public GregorianCalendar getDuration() {
        //long dur = startDate.getTime() - endDate.getTime();
        //return new Date(dur);
        //if (startDate.equals(endDate)) {
        //    return new GregorianCalendar();
        //} else {
            //long dur = startDate.getInstance().getTime().getTime() - endDate.getInstance().getTime().getTime();

        //}
        throw new NotImplementedException();
    }



}
