import java.util.List;
import java.util.ArrayList;

public class Tag {
    private String text;
    private ArrayList<String> ids = new ArrayList<>();

    public Tag(String text) {
        this.text = text;
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
