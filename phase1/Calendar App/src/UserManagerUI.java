/**
 * UserManagerUI, the starting point of the program
 */
public class UserManagerUI {

    public static void main (String[] args) {
        System.out.println("Hello calendar");
        UserManager um = new UserManager();
        um.loadUsers();
        um.displayUsers();
        try {
            um.registerUser("Dog", "woofwoofwoof", "woofwoofwoof");
        } catch (Exception e) {
            e.printStackTrace();
        }
        um.displayUsers();
        um.saveUsers();
    }

}
