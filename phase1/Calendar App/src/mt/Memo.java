package mt;

import java.util.ArrayList;
import java.util.List;

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
     * Get the memo's title
     *
     * @return Title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Add an event to the memo
     *
     * @param eventId ID of the event being added
     */
    public void addEvent(String eventId) {
        ids.add(eventId);
    }

    /**
     * Remove an event from the memo
     *
     * @param eventId ID of the event being removed
     */
    public void removeEvent(String eventId) {
        ids.remove(eventId);
    }

    /**
     * Find the event if it is in this memo
     *
     * @param eventId Event ID to be searched for
     * @return True iff the memo is attached to the event
     */
    public boolean hasEvent(String eventId) {
        return ids.contains(eventId);
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
