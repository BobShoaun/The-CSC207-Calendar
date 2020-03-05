import java.util.List;
import java.util.ArrayList;

public class Memo {

    private String title;
    private String text;
    private ArrayList<String> ids = new ArrayList<>();

    public Memo(String title, String text) {
        this.text = text;
        this.title = title;
    }

    public void setTitle(String newTitle) {
        title = newTitle;
    }

    public void setText(String newText) {
        text = newText;
    }

    public String getText() {
        return text;
    }

    public String getTitle(){
        return title;
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
