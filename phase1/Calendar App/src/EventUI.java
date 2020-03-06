import exceptions.InvalidDateException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Terminal interface for Event.
 */

public class EventUI extends UserInterface {

    private Event event;
    private Calendar calendar;

    List<MemoUI> memoUIs = new ArrayList<>();

    public EventUI(Event event, Calendar calendar) {
        this.event = event;
        this.calendar = calendar;
    }

    public Event getEvent() { return event; }

    @Override
    public void display() {
        System.out.println(event.toString());
    }

    @Override
    public void show() {
        boolean running = true;
        getMemoUIs();
        while (running) {
            display();
            int option = getOptionsInput(new String[]{"Exit",
                    "Event Duration", "Edit Event",
                    "Show Alerts", "Edit Alert",
                    "Show Memos", "Edit Memos",
                    "Show Tags", "Edit Tags"});
            switch (option) {
                case 0: // Exit
                    running = false;
                    break;
                case 1: // Event duration
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
                    formatter.format(event.getDuration().getTime());
                    System.out.println(event.getName() + " lasts for " + formatter);
                    break;
                case 2: // Edit event
                    editEvent();
                    break;
                case 3: // Show alerts
                    List<AlertCollection> alertCollections = calendar.alertCollections;
                    for (AlertCollection ac: alertCollections) {
                        if (ac.getEventId().equals(event.getId())) {
                            System.out.println(ac.toString());
                        }
                    }
                    break;
                case 4: // Edit alert
                    editAlert();
                    break;
                case 5: // Show memos
                    List<Memo> memos = calendar.getMemos();
                    StringBuilder result = new StringBuilder();
                    int i = 0;
                    for (Memo m: memos) {
                        if ( m.hasEvent(event.getId()) ) {
                            String num = Integer.toString(i);
                            result.append("[").append(num).append("]").append(m.getTitle()).append("\n").append(m.getText()).append("\n\n");
                            i += 1;
                        }
                    }
                    System.out.println(result);
                    break;
                case 6: // Edit memo
                    int num = getIntInput("Memo no.: ", 0, memoUIs.size() - 1);
                    MemoUI mui = memoUIs.get(num);
                    mui.show();
                    break;
                case 7: // Show tags
                    List<Tag> tags = calendar.tags;
                    for (Tag t: tags) {
                        if ( t.hasEvent(event.getId()) ) {
                            System.out.println(t.getText());
                        }
                    }
                    break;
                case 8: // Edit tag
                    editTag();
                    break;
            }
        }
    }

    private void editEvent() {
        boolean editing = true;
        while (editing) {
            int options = getOptionsInput(new String[]{"Exit", "Change name", "Change start time", "Change end time"});
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
            }
        }
    }


    private void editAlert() {
        for (AlertCollection ac : calendar.alertCollections) {
            if (ac.getEventId().equals(event.getId())) {
                AlertUI alertUI = new AlertUI(ac);
                alertUI.show();
            } else {
                System.out.println("No existing alerts.");
            }
        }
    }

    private void getMemoUIs() {
        List<Memo> m = calendar.getMemos(event.getId());
        for (Memo memo: m) {
            memoUIs.add(new MemoUI(memo, calendar));
        }
    }

    private void editTag() {
        List<Tag> tags = calendar.tags;
        String tagName = getStringInput("Enter tag name: ");
        for (Tag t: tags) {
            if ( t.getText().equals(tagName) )  {
                String newText = getStringInput("Enter new tag text: ");
                t.setText(newText);
            }
        }
    }



}
