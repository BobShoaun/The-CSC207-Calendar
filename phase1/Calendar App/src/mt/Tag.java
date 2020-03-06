package mt;

import java.util.ArrayList;
import java.util.List;

public class Tag {
    private String text;
    private List<String> ids;

    public Tag(String text) {
        this.text = text;
        ids = new ArrayList<>();
    }

    public Tag(String text, List<String> ids) {
        this.text = text;
        this.ids = ids;
    }

    public String getText() {
        return text;
    }

    public void setText(String newText) { this.text = newText; }

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
