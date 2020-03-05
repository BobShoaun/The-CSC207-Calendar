import java.io.IOException;

public class MemoUI extends UserInterface {
    Memo memo;
    Calendar calendar;

    public MemoUI(Memo memo, Calendar calendar){
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
            int option = getOptionsInput(new String[]{"Edit Title", "Edit Text", "Show Events", "Delete", "Exit"});
            switch (option) {
                case 1:
                    memo.setTitle(this.getStringInput("Enter new title: "));

                case 2:
                    memo.setText(this.getStringInput("Enter new text: "));

                case 3:
                    for(String id : memo.getEvents()){
                        Event event = calendar.getEvent(id);
                        System.out.println(event.getName() + event.getStartDate().toString());
                    }

                case 4:
                    calendar.removeMemo(memo);

                case 5:
                    running = false;
            }
        }
    }
}


