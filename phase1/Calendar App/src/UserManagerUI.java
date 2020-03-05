import exceptions.PasswordMismatchException;
import exceptions.UsernameTakenException;

/**
 * UserManagerUI, the starting point of the program
 */
public class UserManagerUI extends UserInterface {

    public static void main (String[] args) {
        new UserManagerUI().show();
    }

    @Override
    public void display() {
        System.out.println("\n" +
                " ██████╗ █████╗ ██╗     ███████╗███╗   ██╗██████╗  █████╗ ██████╗ \n" +
                "██╔════╝██╔══██╗██║     ██╔════╝████╗  ██║██╔══██╗██╔══██╗██╔══██╗\n" +
                "██║     ███████║██║     █████╗  ██╔██╗ ██║██║  ██║███████║██████╔╝\n" +
                "██║     ██╔══██║██║     ██╔══╝  ██║╚██╗██║██║  ██║██╔══██║██╔══██╗\n" +
                "╚██████╗██║  ██║███████╗███████╗██║ ╚████║██████╔╝██║  ██║██║  ██║\n" +
                " ╚═════╝╚═╝  ╚═╝╚══════╝╚══════╝╚═╝  ╚═══╝╚═════╝ ╚═╝  ╚═╝╚═╝  ╚═╝\n");
    }

    @Override
    public void show() {
        display();

        UserManager um = new UserManager();
        um.loadUsers();
        um.displayUsers();
        try {
            um.registerUser("Cat", "meow123", "meow123");
        } catch (UsernameTakenException e) {
            System.out.println("Username already taken!");
        } catch (PasswordMismatchException e) {
            System.out.println("Password mismatch!");
        }
        um.displayUsers();
        um.saveUsers();
    }
}
