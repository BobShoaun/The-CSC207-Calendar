import java.util.List;
import java.util.ArrayList;

public class Tag {
    private String text;
    private ArrayList<String> ids;

    public Tag(String text) {
        this.text = text;
        ids = new ArrayList<>();
    }

    public Tag(String text, ArrayList<String> ids) {
        this.text = text;
        this.ids = ids;
    }

    public Tag(String text, List<String> eventIds) {

    }

    public String getText() {
        return text;
    }

    public void addEvent(String eventId) {
        ids.add(eventId);
    }

    public void removeEvent(String eventId) {
        ids.remove(eventId);
    }

    public boolean hasEvent(String eventId) {
        return ids.contains(eventId);
    }

    public List<String> getEvents() {
        return ids;
    }
}
