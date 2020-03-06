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

                case 1:
                    memo.setTitle(this.getStringInput("Enter new title: "));

                case 2:
                    memo.setText(this.getStringInput("Enter new text: "));

                case 3:
                    for(String id : memo.getEvents()){
                        System.out.println(calendar.getEvent(id).toString());
                    }

                case 4:
                    calendar.removeMemo(memo);
            }
        }
    }
}


