import exceptions.PasswordMismatchException;
import exceptions.UsernameTakenException;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * UserManager class
 * @author Ng Bob Shoaun
 */
public class UserManager extends TextFileSerializer {

    private ArrayList<User> users;
    private User currentUser;

    public UserManager () {
        users = new ArrayList<> ();
        currentUser = null;
    }

    public void loadUsers () {
        Scanner scanner = loadScannerFromFile("resources/users.txt");
        while (scanner.hasNext())
            users.add(new User(scanner.nextLine()));
    }

    public void saveUsers() {
        List<String> parsedUsers = new ArrayList<>();
        for (User user : users)
            parsedUsers.add(user.parse());
        saveToFile("resources/users.txt", parsedUsers);
    }

    public void displayUsers() {
        for (User user : users)
            System.out.println(user);
    }

//    public static void main (String[] args) {
//        System.out.println("\n" +
//                " ██████╗ █████╗ ██╗     ███████╗███╗   ██╗██████╗  █████╗ ██████╗ \n" +
//                "██╔════╝██╔══██╗██║     ██╔════╝████╗  ██║██╔══██╗██╔══██╗██╔══██╗\n" +
//                "██║     ███████║██║     █████╗  ██╔██╗ ██║██║  ██║███████║██████╔╝\n" +
//                "██║     ██╔══██║██║     ██╔══╝  ██║╚██╗██║██║  ██║██╔══██║██╔══██╗\n" +
//                "╚██████╗██║  ██║███████╗███████╗██║ ╚████║██████╔╝██║  ██║██║  ██║\n" +
//                " ╚═════╝╚═╝  ╚═╝╚══════╝╚══════╝╚═╝  ╚═══╝╚═════╝ ╚═╝  ╚═╝╚═╝  ╚═╝\n");
//
//        UserManager um = new UserManager();
//        um.loadUsers();
//        try {
//            um.registerUser("Cat", "meow123", "meow123");
//        } catch (UsernameTakenException e) {
//            System.out.println("Username already taken!");
//        } catch (PasswordMismatchException e) {
//            System.out.println("Password mismatch!");
//        }
//        um.displayUsers();
//        um.saveUsers();
//    }

    public boolean loginUser(String username, String password) {
        for (User user : users) {
            if (user.authenticate(username, password)) {
                currentUser = user;
                return true;
            }
        }
        return false;
    }

    public void registerUser(String username, String password, String confirmPassword)
            throws UsernameTakenException, PasswordMismatchException {
        if (!password.equals(confirmPassword))
            throw new PasswordMismatchException();

        for (User user : users)
            if (user.getName().equals(username))
                throw new UsernameTakenException();

        users.add(new User(username, password, new Calendar()));
        //TODO: this should also create a user directory in resources/
    }

}
