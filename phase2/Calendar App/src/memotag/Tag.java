package memotag;

import event.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * A class representing a tag.
 */
public class Tag {
    private String text;
    private List<String> ids;

    /**
     * constructor for a tag
     *
     * @param text Tag contents
     */
    public Tag(String text) {
        this.text = text;
        ids = new ArrayList<>();
    }

    /**
     * constructor for a tag
     *
     * @param text Tag contents
     * @param ids  IDs of events linked to this tag
     */
    public Tag(String text, List<String> ids) {
        this.text = text;
        this.ids = ids;
    }

    /**
     * getter for tag text
     *
     * @return Tag text
     */
    public String getText() {
        return text;
    }

    /**
     * setter for tag text
     *
     * @param newText New Text for the Tag
     */
    public void setText(String newText) {
        this.text = newText;
    }

    /**
     * adds an event
     *
     * @param eventId of the event
     */
    public void addEvent(String eventId) {
        ids.add(eventId);
    }

    /**
     * removes an event
     *
     * @param event the event to remove
     */
    public void removeEvent(Event event) {
        ids.remove(event.getId());
    }

    /**
     * checks if this tag is applied to an event
     *
     * @param event Event to check
     * @return True iff the tag has this event linked to it
     */
    public boolean hasEvent(Event event) {
        return ids.contains(event.getId());
    }

    /**
     * getter for event ids
     *
     * @return Event IDs for linked Events
     */
    public List<String> getEvents() {
        return ids;
    }
}
