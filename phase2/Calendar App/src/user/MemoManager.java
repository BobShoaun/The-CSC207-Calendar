package user;

import event.Event;
import memotag.Memo;

import java.util.List;

/**
 * Manages Memos for a Calendar
 */
public class MemoManager {

    private final List<Memo> memos;
    private final DataSaver dataSaver;

    /**
     * Initialize this MemoManager with a list of memos.
     *
     * @param memos     Memo List
     * @param dataSaver For saving to files
     */
    public MemoManager(List<Memo> memos, DataSaver dataSaver) {
        this.memos = memos;
        this.dataSaver = dataSaver;
    }

    /**
     * Get the memo attributed with a certain event
     *
     * @param event The event which memos must be linked to
     * @return Memo, null if non is found
     */
    public Memo getMemo(Event event) {
        return memos.stream().filter(m -> m.hasEvent(event)).findFirst().orElse(null);
    }

    /**
     * Get all memos stored in this calendar
     *
     * @return An unsorted list of memos
     */
    public List<Memo> getMemos() {
        return memos;
    }

    /**
     * Gets a memo by its title
     *
     * @param name Title of the memo
     * @return Returns the memo with the corresponding title, if no memo is found returns null
     */
    public Memo getMemo(String name) {
        return memos.stream().filter(m -> m.getTitle().equals(name)).findAny().orElse(null);
    }

    /**
     * Gets a memo by its title and content
     *
     * @param name Title of the memo
     * @return Returns the memo with the corresponding title and content, if no memo is found returns null
     */
    public Memo getMemo(String name, String content) {
        return memos.stream().filter(m -> m.getTitle().equals(name) && m.getText().equals(content)).findAny().orElse(null);
    }

    /**
     * Add a new memo
     *
     * @param memo Memo to add
     */
    public void addMemo(Memo memo) {
        memos.add(memo);
        dataSaver.saveMemos(this);
    }

    /**
     * edits the memo title
     *
     * @param memoName    Name of the memo to edit
     * @param newMemoName New name of the memo
     */
    public void editMemoTitle(String memoName, String newMemoName) {
        Memo memo = memos.stream().filter(m -> m.getTitle().equals(memoName)).findAny().orElseThrow(null);
        memo.setTitle(newMemoName);
        dataSaver.saveMemos(this);
    }

    /**
     * edits a memo text
     *
     * @param memoName    Name of the memo to edit
     * @param newMemoText The new text for this memo
     */
    public void editMemoText(String memoName, String newMemoText) {
        Memo memo = memos.stream().filter(m -> m.getTitle().equals(memoName)).findAny().orElseThrow(null);
        memo.setText(newMemoText);
        dataSaver.saveMemos(this);
    }

    /**
     * Remove the memo from all memos. Saves memos
     *
     * @param memo Memo to remove
     */
    public void removeMemo(Memo memo) {
        memos.remove(memo);
        dataSaver.saveMemos(this);
    }
}