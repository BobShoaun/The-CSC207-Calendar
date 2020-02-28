import java.time.LocalDate;

/**
 * Event class
 */
public class Event {

    private int id;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;

    public Event (int id, String name, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getName () {
        return name;
    }

}
