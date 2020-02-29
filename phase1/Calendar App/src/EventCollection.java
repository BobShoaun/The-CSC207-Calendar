import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EventCollection implements Serializable
{
    private ArrayList<Event> events = new ArrayList<>();
    private String name = null;
}
