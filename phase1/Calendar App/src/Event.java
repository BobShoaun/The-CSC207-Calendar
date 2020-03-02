import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Date;

/**
 * Event class
 */
public class Event {

    private String id;
    private String name;
    private String description;
    private Date startDate;
    private Date endDate;

    public Event (String id, String name, Date startDate, Date endDate) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getName () {
        return name;
    }

    public Date getStartDate() {return startDate; }

    public boolean hasTag(MT tag) {
        throw new NotImplementedException();
    }
}
