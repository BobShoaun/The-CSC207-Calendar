package consoleui;

import mt.Memo;
import user.Calendar;

public class MemoUI extends UserInterface {
    Memo memo;
    Calendar calendar;

    public MemoUI(Memo memo, Calendar calendar) {
        this.memo = memo;
        this.calendar = calendar;
    }

    @Override
    public void display(){
        System.out.println(memo.getTitle() + "\n\n" + memo.getText());
    }

    @Override
    public void show() {
        boolean running = true;
        while (running) {
            int option = getOptionsInput(new String[]{"Exit", "Edit Title", "Edit Text", "Show Events", "Delete"});
            switch (option) {
                case 0:
                    running = false;
                    break;
                case 1:
                    calendar.editMemoTitle(memo.getTitle(), this.getStringInput("Enter new title: "));
                    break;
                case 2:
                    calendar.editMemoText(memo.getTitle(), this.getStringInput("Enter new text: "));
                    break;
                case 3:
                    for(String id : memo.getEvents()){
                        System.out.println(calendar.getEvent(id).toString());
                    }
                    break;
                case 4:
                    calendar.removeMemo(memo);
                    System.out.println("Memo deleted!");
                    return;
            }
        }
    }
}


