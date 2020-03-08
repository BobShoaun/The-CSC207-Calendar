package mt;

import java.util.ArrayList;
import java.util.List;

/**
 * a class representing a memo
 */
public class Memo {

    private String title;
    private String text;
    private List<String> ids;

    /**
     * constructor for Memo
     * @param title of the memo
     * @param text content of the memo
     */
    public Memo(String title, String text) {
        this.text = text;
        this.title = title;
        ids = new ArrayList<>();
    }

    /**
     * constructor for Memo
     * @param title of the memo
     * @param text content of the memo
     * @param ids
     */
    public Memo(String title, String text, List<String> ids) {
        this.text = text;
        this.title = title;
        this.ids = ids;
    }

    /**
     * setter for title
     * @param newTitle
     */
    public void setTitle(String newTitle) {
        title = newTitle;
    }

    /**
     * setter for text content
     * @param newText
     */
    public void setText(String newText) {
        text = newText;
    }

    /**
     * getter for text
     * @return
     */
    public String getText() {
        return text;
    }

    /**
     * getter for title
     * @return
     */
    public String getTitle(){
        return title;
    }

    /**
     * adds an event to the Memo
     * @param eventId of event to remove
     */
    public void addEvent(String eventId) {
        ids.add(eventId);
    }

    /**
     * removes an event
     * @param eventId of event to remove
     */
    public void removeEvent(String eventId) {
        ids.remove(eventId);
    }

    /**
     * check if event is in memo
     * @param eventId
     * @return
     */
    public boolean hasEvent(String eventId) {
        return ids.contains(eventId);
    }

    /**
     * getter for events ids
     * @return
     */
    public List<String> getEvents() {
        return ids;
    }
}
