import exceptions.InvalidDateException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

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
        while (running) {
            int option = getOptionsInput(new String[]{"Exit", "Show Duration" ,"Edit Event", "Show Alerts", "Edit Alert", "Show Memos", "Edit Memos", "Show Tags", "Edit Tags"});
            switch (option) {
                case 0:
                    running = false;
                    break;
                case 1:
                    System.out.println("This event lasts for ");
                    break;
                case 2:
                    editEvent();
                    break;
                case 3:
                    List<AlertCollection> alertCollections = calendar.alertCollections;
                    for (AlertCollection ac: alertCollections) {
                        if (ac.getEventId().equals(event.getId())) {
                            System.out.println(ac.toString());
                        }
                    }
                    break;
                case 4:
                    break;
                case 5:
                    List<Memo> memos = calendar.getMemos();
                    StringBuilder result = new StringBuilder();
                    for (Memo m: memos) {
                        int i = 0;
                        if ( m.hasEvent(event.getId()) ) {
                            String num = Integer.toString(i);
                            result.append("[" + num + "]" + m.getTitle() + "\n" + m.getText() + "\n\n");
                            i += 1;
                        }
                    }
                    System.out.println(result);
                    break;
                case 6:
                    int num = getIntInput("Memo no.: ", 0, memoUIs.size() - 1);
                    MemoUI mui = memoUIs.get(num);
                    mui.show();
                    break;
                case 7:
                    List<Tag> tags = calendar.tags;
                    for (Tag t: tags) {
                        if ( t.hasEvent(event.getId()) ) {
                            System.out.println(t.getText());
                        }
                    }
                    break;
                case 8:
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
                case 3:
                    try {
                        GregorianCalendar end = getDateInput("Enter new end time: ");
                        event.setEndDate(end);
                    } catch (InvalidDateException e) {
                        System.out.println("Invalid time input. End time cannot be before current start time.");
                        editEvent();
                    }
            }
        }
    }

    private void getMemoUIs() {
        List<Memo> m = calendar.getMemos(event.getId());
        //List<MemoUI> memoUIs = m.stream().map(MemoUI::new).collect(Collectors.toList());;

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
