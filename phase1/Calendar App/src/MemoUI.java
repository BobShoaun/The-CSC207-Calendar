import java.io.IOException;

public class MemoUI extends UserInterface {
    Memo memo;

    public MemoUI(Memo memo){
        this.memo = memo;
    }

    @Override
    public void display(){
         System.out.println(memo.getText() + "\n\n" + memo.getText());
    }

    @Override
    public void show() {
        boolean running = true;
        while (running) {
            int option = getOptionsInput(new String[]{"Edit Title", "Edit Text", "Delete", "Exit"});
            switch (option) {
                case 1:

                case 2:

                case 3:

                case 4:

            }
        }
    }
}


