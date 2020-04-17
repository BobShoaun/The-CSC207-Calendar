package user;

import event.Event;
import memotag.Tag;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages a Calendar's tags
 */
public class TagManager {

    private final List<Tag> tags;
    private final DataSaver dataSaver;

    /**
     * Initializes the TagManager with a List of Tags
     *
     * @param tags      Tags list
     * @param dataSaver For saving to files
     */
    public TagManager(List<Tag> tags, DataSaver dataSaver) {
        this.tags = tags;
        this.dataSaver = dataSaver;
    }

    /**
     * Get all tags
     *
     * @return List of all tags
     */
    public List<Tag> getTags() {
        return tags;
    }

    /**
     * Get tag with a certain name
     *
     * @param tagName Name of the tag
     * @return The corresponding tag, otherwise null
     */
    public Tag getTag(String tagName) {
        return tags.stream().filter(t -> t.getText().equals(tagName)).findAny().orElse(null);
    }

    /**
     * Return all tags which are attributed with a certain event
     *
     * @param event The event which tag must be linked to
     * @return Unsorted list of tags
     */
    public List<Tag> getTags(Event event) {
        return tags.stream().filter(m -> m.hasEvent(event)).collect(Collectors.toList());
    }

    /**
     * Add a new tag
     *
     * @param tag The tag to add
     */
    public void addTag(Tag tag) {
        tags.add(tag);
        dataSaver.saveTags(this);
    }

    /**
     * remove event from tags
     *
     * @param tag   the text of the tag
     * @param event Event to be removed
     */
    public void removeTag(String tag, Event event) {
        for (Tag t : tags) {
            if (t.getText().equals(tag)) {
                t.removeEvent(event);
            }
        }
    }
}