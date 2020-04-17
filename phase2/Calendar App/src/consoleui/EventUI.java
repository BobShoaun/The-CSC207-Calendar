package consoleui;

import alert.AlertCollection;
import event.Event;
import exceptions.InvalidDateException;
import memotag.Memo;
import memotag.Tag;
import user.Calendar;
import user.DataSaver;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Terminal interface for Event.
 */

public class EventUI extends UserInterface {

    private Event event;
    private Calendar calendar;
    private DataSaver datasaver;

    List<MemoUI> memoUIs = new ArrayList<>();

    /**
     * Create a new EventUI
     *
     * @param event    The event
     * @param calendar Calendar object attached to the event
     * @param ds       The datasaver representing the user filepath
     */
    public EventUI(Event event, Calendar calendar, DataSaver ds) {
        this.event = event;
        this.calendar = calendar;
        this.datasaver = ds;
    }

    /**
     * Get the event associated with the UI
     *
     * @return the event
     */
    public Event getEvent() {
        return event;
    }

    /**
     * Display the contents of this event.
     */
    @Override
    public void display() {
        System.out.println(event.toString());
    }

    /**
     * Start the user interface.
     */
    @Override
    public void show() {
        boolean running = true;
        getMemoUI();
        while (running) {
            display();
            int option = getOptionsInput(new String[]{"Exit",
                    "Event Duration", "Edit Event",
                    "Show Alerts", "Manage alerts",
                    "Show Memos", "Edit a memo",
                    "Show Tags", "Edit a tag"});
            switch (option) {
                case 0: // Exit
                    running = false;
                    break;
                case 1: // Event duration
                    showDuration();
                    break;
                case 2:
                    editEvent();
                    break;
                case 3:
                    showAlerts();
                    break;
                case 4:
                    editAlert();
                    break;
                case 5:
                    showMemos();
                    break;
                case 6:
                    editMemo();
                    break;
                case 7:
                    showTags();
                    break;
                case 8:
                    editTag();
                    break;
            }
        }
    }

    private void showDuration() {
        long millis = event.getDuration();
        String dur = String.format("%02d:%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toDays(millis),
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), // The change is in this line
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        System.out.println(event.getName() + " lasts for " + dur);
    }

    private void showTags() {
        List<Tag> tags = calendar.getTags();
        for (Tag t : tags) {
            if (t.hasEvent(event)) {
                System.out.println(t.getText());
            }
        }
    }

    private void editMemo() {
        if (memoUIs.size() == 0) {
            System.out.println("No memos found.");
            return;
        }
        showMemos();
        int num = getIntInput("Memo no.: ", 0, memoUIs.size() - 1);
        MemoUI mui = memoUIs.get(num);
        mui.show();
    }

    private void showMemos() {
        List<Memo> memos = calendar.getMemos();
        StringBuilder result = new StringBuilder();
        int i = 0;
        for (Memo m : memos) {
            if (m.hasEvent(event)) {
                String num = Integer.toString(i);
                result.append("[").append(num).append("]").append(m.getTitle()).append("\n").append(m.getText()).append("\n\n");
                i += 1;
            }
        }
        System.out.println(result);
    }

    private void showAlerts() {
        List<AlertCollection> alertCollections = calendar.getAlertCollections();
        for (AlertCollection ac : alertCollections) {
            if (ac.getEventId().equals(event.getId())) {
                System.out.println(ac.toString());
            }
        }
    }

    private void editEvent() {
        boolean editing = true;
        while (editing) {
            int options = getOptionsInput(new String[]{"Exit", "Change name", "Change start time", "Change end time", "Shift event"});
            switch (options) {
                case 0:
                    editing = false;
                    break;
                case 1:
                    String name = getStringInput("Enter new event name: ");
                    event.setName(name);
                    break;
                case 2:
                    try {
                        GregorianCalendar start = getDateInput("Enter new start time: ");
                        event.setStartDate(start);
                    } catch (InvalidDateException e) {
                        System.out.println("Invalid time input. Start time cannot be after current end time.");
                        editEvent();
                    }
                    break;
                case 3:
                    try {
                        GregorianCalendar end = getDateInput("Enter new end time: ");
                        event.setEndDate(end);
                    } catch (InvalidDateException e) {
                        System.out.println("Invalid time input. End time cannot be before current start time.");
                        editEvent();
                    }
                    break;
                case 4:
                    GregorianCalendar shifted = getDateInput("Enter new start time: ");
                    event.shiftEvent(shifted);
                    break;
            }
        }
    }


    private void editAlert() {
        if (calendar.getAlertCollections().size() == 0) {
            AlertUI alertUI = new AlertUI(new AlertCollection(event, this.datasaver));
            alertUI.show();
        } else {
            for (AlertCollection ac : calendar.getAlertCollections()) {
                if (ac.getEventId().equals(event.getId())) {
                    AlertUI alertUI = new AlertUI(ac);
                    alertUI.show();
                } else
                    System.out.println("No alerts found.");
            }
        }
    }


    private void getMemoUI() {
        Memo memo = calendar.getMemo(event);
        memoUIs.add(new MemoUI(memo, calendar));
    }

    private void editTag() {
        List<Tag> tags = calendar.getTags();
        String tagName = getStringInput("Enter tag name: ");
        for (Tag t : tags) {
            if (t.getText().equals(tagName)) {
                String newText = getStringInput("Enter new tag text: ");
                t.setText(newText);
            }
        }
    }


}
