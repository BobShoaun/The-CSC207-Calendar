import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.util.ArrayList;
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
            int option = getOptionsInput(new String[]{"Show Memos", "Show Tags", "Edit Memos", "Edit Tags"});
            switch (option) {
                case 1:
                    List<Memo> memos = calendar.getMemos();
                    for (Memo m: memos) {
                        int i = 0;
                        if ( m.hasEvent(event.getId()) ) {
                            String num = Integer.toString(i);
                            i += 1;
                            System.out.println(num + ". " + m.getTitle() + "\n" + m.getText() + "\n\n");
                        }
                    }
                    break;
                case 2:
                    List<Tag> tags = calendar.tags;
                    for (Tag t: tags) {
                        if ( t.hasEvent(event.getId()) ) {
                            System.out.println(t.getText());
                        }
                    }
                    break;
                case 3:
                    int num = getIntInput("Memo no.: ", 0, memoUIs.size() - 1);
                    MemoUI mui = memoUIs.get(num);
                    mui.show();
                    break;
                case 4:
                    editTag();
                    break;
            }
        }
    }

    private void getMemoUIs() {
        List<Memo> m = calendar.getMemos(event.getId());
        //List<MemoUI> memoUIs = m.stream().map(MemoUI::new).collect(Collectors.toList());;

    }

    private void editTag() {
        List<Tag> tags = calendar.tags;
        String tagName = getStringInput("Tag name: ");
        for (Tag t: tags) {
            if ( t.getText().equals(tagName) )  {
                String newText = getStringInput("New tag text: ");
                t.setText(newText);
            }
        }
    }



}
