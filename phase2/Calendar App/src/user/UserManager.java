package user;

import exceptions.PasswordMismatchException;
import exceptions.UsernameTakenException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * UserManager class handles user authentication and registration
 *
 * @author Ng Bob Shoaun
 */
public class UserManager extends DataSaver {

    private List<User> users;
    private User currentUser;

    /**
     *
     * @return current logged in user
     */
    public User getCurrentUser () { return currentUser; }

    /**
     * Constructor for UserManager
     */
    public UserManager () {
        super("");
        users = new ArrayList<> ();
        currentUser = null;
    }

    /**
     * Loads all users from the file system into memory
     * @throws IOException
     */
    public void loadUsers() throws IOException {
        File[] files = getFilesInDirectory("");
        for (File file : files) {
            String userCredentials = loadStringFromFile(file.getPath() + "/credentials.txt");
            try {
                users.add(new User(userCredentials));
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Serializes all users from memory into the file system
     * @throws IOException
     */
    public void saveUsers() throws IOException {
        for (User user : users)
            saveToFile("" + user.getName() + "/credentials.txt", user.parse());
    }

    /**
     * Serializes a user from memory into the file system
     * @param user to save
     * @throws IOException
     */
    public void saveUser(User user) throws IOException {
        saveToFile(user.getName() + "/credentials.txt", user.parse());
    }

    /**
     * Prints out all registered users
     */
    public void displayUsers() {
        for (User user : users)
            System.out.println(user);
    }

    /**
     * Logs in a user
     * @param username username of the user
     * @param password password of the user
     * @return true if the user has successfully log in, false otherwise
     */
    public boolean loginUser(String username, String password) {
        for (User user : users) {
            if (user.authenticate(username, password)) {
                currentUser = user;
                return true;
            }
        }
        return false;
    }

    /**
     * Logout the current user
     */
    public void logoutCurrentUser () {
        if (currentUser == null)
            return;
        currentUser.setLastLoginTime(new GregorianCalendar()); // by default it is the current time
    }

    /**
     * Register a new user
     * @param username new username
     * @param password new password
     * @param confirmPassword new password again
     * @throws UsernameTakenException
     * @throws PasswordMismatchException
     * @throws IOException
     */
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