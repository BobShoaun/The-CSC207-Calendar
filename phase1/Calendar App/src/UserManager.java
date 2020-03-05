import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * UserManager class
 * @author Ng Bob Shoaun
 */
public class UserManager extends TextFileSerializer {

    private List<User> users;
    private User currentUser;

    public UserManager () {
        users = new ArrayList<> ();
        currentUser = null;
    }

    public void loadUsers () {
        Scanner scanner = loadScannerFromFile("Resources/users.txt");
        while (scanner.hasNext())
            users.add(new User(scanner.nextLine()));
    }

    public void saveUsers() {
        List<String> parsedUsers = new ArrayList<>();
        for (User user : users)
            parsedUsers.add(user.parse());
        saveToFile("Resources/users.txt", parsedUsers);
    }

    public void displayUsers () {
        for (User user : users)
            System.out.println(user);
    }


    public boolean loginUser (String username, String password) {
        for (User user : users) {
            if (user.authenticate(username, password)) {
                currentUser = user;
                return true;
            }
        }
        return false;
    }

    public boolean registerUser (String username, String password, String confirmPassword)
            throws UsernameTakenException, PasswordMismatchException {
        if (password != confirmPassword)
            throw new PasswordMismatchException();

        for (User user : users)
            if (user.getName().equals(username))
                throw new UsernameTakenException ();

        users.add(new User(username, password, new Calendar()));
        return true;
    }

}
