package mt;

import entities.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * A class representing a tag
 */
public class Tag {
    private String text;
    private List<String> ids;

    /**
     * constructor for a tag
     * @param text
     */
    public Tag(String text) {
        this.text = text;
        ids = new ArrayList<>();
    }

    /**
     * constructor for a tag
     * @param text
     * @param ids
     */
    public Tag(String text, List<String> ids) {
        this.text = text;
        this.ids = ids;
    }

    /**
     * getter for tag text
     * @return
     */
    public String getText() {
        return text;
    }

    /**
     * setter for tag text
     * @param newText
     */
    public void setText(String newText) { this.text = newText; }

    /**
     * adds an event
     * @param eventId of the event
     */
    public void addEvent(String eventId) {
        ids.add(eventId);
    }

    /**
     * removes an event
     * @param event the event to remove
     */
    public void removeEvent(Event event) {
        ids.remove(event.getId());
    }

    /**
     * checks if this tag is applied to an event
     * @param event
     * @return
     */
    public boolean hasEvent(Event event) {
        return ids.contains(event.getId());
    }

    /**
     * getter for event ids
     * @return
     */
    public List<String> getEvents() {
        return ids;
    }
}
