import exceptions.PasswordMismatchException;
import exceptions.UsernameTakenException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * UserManager class
 * @author Ng Bob Shoaun
 */
public class UserManager extends DataSaver {

    private List<User> users;
    private User currentUser;

    public User getCurrentUser () { return currentUser; }

    public UserManager () {
        super("", false);
        users = new ArrayList<> ();
        currentUser = null;
    }

    public void loadUsers() throws IOException {
        File[] files = getFilesInDirectory("users/");
        for (File file : files) {
            String userCredentials = loadStringFromFile(file.getPath() + "/credentials.txt");
            users.add(new User(userCredentials));
        }
    }

    public void saveUsers() throws IOException {
        for (User user : users)
            saveToFile("users/" + user.getName() + "/credentials.txt", user.parse());
    }

    public void saveUser(User user) throws IOException {
        saveToFile("users/" + user.getName() + "/credentials.txt", user.parse());
    }

    public void displayUsers() {
        for (User user : users)
            System.out.println(user);
    }

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
            throws UsernameTakenException, PasswordMismatchException, IOException {
        if (!password.equals(confirmPassword))
            throw new PasswordMismatchException();

        for (User user : users)
            if (user.getName().toLowerCase().equals(username.toLowerCase()))
                throw new UsernameTakenException();

        User newUser = new User(username, password);
        users.add(newUser);
        saveUser(newUser);
    }

}