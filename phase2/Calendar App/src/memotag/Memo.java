package memotag;

import event.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * A class representing a Memo
 */
public class Memo {

    private String title;
    private String text;
    private List<String> ids;

    /**
     * Initialize a memo.
     *
     * @param title The title of the memo.
     * @param text  The contents of the memo
     */
    public Memo(String title, String text) {
        this.text = text;
        this.title = title;
        ids = new ArrayList<>();
    }

    /**
     * Initialize a memo with event ids
     *
     * @param title The title of the memo.
     * @param text  The contents of the memo
     * @param ids   The ids of the events attached to the memo
     */
    public Memo(String title, String text, List<String> ids) {
        this.text = text;
        this.title = title;
        this.ids = ids;
    }

    /**
     * Set the memo title
     *
     * @param newTitle New title
     */
    public void setTitle(String newTitle) {
        title = newTitle;
    }

    /**
     * Set the memo text
     *
     * @param newText New text
     */
    public void setText(String newText) {
        text = newText;
    }

    /**
     * Get the memo's text
     *
     * @return Memo contents
     */
    public String getText() {
        return text;
    }

    /**
     * getter for title
     *
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     * Add an event to the memo
     *
     * @param event event being added
     */
    public void addEvent(Event event) {
        ids.add(event.getId());
    }

    /**
     * Remove an event from the memo
     *
     * @param event event being removed
     */
    public void removeEvent(Event event) {
        ids.remove(event.getId());
    }

    /**
     * Find the event if it is in this memo
     *
     * @param event Event to be searched for
     * @return True iff the memo is attached to the event
     */
    public boolean hasEvent(Event event) {
        return ids.contains(event.getId());
    }

    /**
     * Get the list of events attached to the memo
     *
     * @return The list of event IDs
     */
    public List<String> getEvents() {
        return ids;
    }
}
