import java.io.IOException;

public class MemoUI extends UserInterface {
    Memo memo;
    Calendar calendar;

    public MemoUI(Memo memo, Calendar calendar){
        this.memo = memo;
    }

    @Override
    public void display(){
         System.out.println(memo.getTitle() + "\n\n" + memo.getText());
    }

    @Override
    public void show() {
        boolean running = true;
        while (running) {
            int option = getOptionsInput(new String[]{"Edit Title", "Edit Text", "Delete", "Exit"});
            switch (option) {
                case 1:
                    memo.setTitle(this.getStringInput("Enter new title: "));

                case 2:
                    memo.setText(this.getStringInput("Enter new text: "));

                case 3:
                    calendar.removeMemo(memo);

                case 4:
                    running = false;
            }
        }
    }
}


