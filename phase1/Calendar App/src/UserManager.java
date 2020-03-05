import exceptions.PasswordMismatchException;
import exceptions.UsernameTakenException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * UserManager class
 * @author Ng Bob Shoaun
 */
public class UserManager extends TextFileSerializer {

    private List<User> users;
    private User currentUser;

    public User getCurrentUser () { return currentUser; }

    public UserManager () {
        users = new ArrayList<> ();
        currentUser = null;
    }

    public void loadUsers() {
        File[] files = getFilesInDirectory("users/");
        for (File file : files) {
            String userCredentials = loadStringFromFile(file.getPath() + "/credentials.txt");
            users.add(new User(userCredentials, file.getPath()));
        }
    }

    public void saveUsers() {
        for (User user : users)
            saveToFile("users/" + user.getName() + "/credentials.txt", user.parse());
    }

    public void saveUser(User user) {
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
            throws UsernameTakenException, PasswordMismatchException {
        if (!password.equals(confirmPassword))
            throw new PasswordMismatchException();

        for (User user : users)
            if (user.getName().toLowerCase().equals(username.toLowerCase()))
                throw new UsernameTakenException();

        User newUser = new User(username, password, new Calendar());
        users.add(newUser);
        saveUser(newUser);
    }

}