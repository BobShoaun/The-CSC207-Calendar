package user;

import event.EventSharer;
import exceptions.InvalidPasswordException;
import exceptions.InvalidUsernameException;
import exceptions.UsernameTakenException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * UserManager class handles user authentication and registration
 *
 * @author Ng Bob Shoaun
 */
public class UserManager {

    private List<User> users;
    private User currentUser;
    private final DataSaver dataSaver;
    private final EventSharer eventSharer;

    /**
     * Getter for current logged in user
     *
     * @return current logged in user
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Constructor for UserManager
     */
    public UserManager() {
        dataSaver = new DataSaver("./users/");
        users = new ArrayList<>();
        currentUser = null;
        eventSharer = new EventSharer(this);
    }

    /**
     * Get the EventSharer
     *
     * @return EventSharer
     */
    public EventSharer getEventSharer() {
        return eventSharer;
    }

    /**
     * Loads all users from the file system into memory
     *
     * @throws IOException if users could not be loaded
     */
    public void loadUsers() throws IOException {
        File[] files = dataSaver.getFilesInDirectory("");
        for (File file : files) {
            String userCredentials = dataSaver.loadStringFromFile(file.getName() + "/credentials.txt");
            users.add(new User(userCredentials));
        }
    }

    /**
     * Saves all users from memory into the file system
     *
     * @throws IOException if user credentials could not be saved
     */
    public void saveUsers() throws IOException {
        for (User user : users)
            user.save();
    }

    /**
     * Saves a user from memory into the file system
     *
     * @param user to save
     * @throws IOException if the user could not be saved
     */
    public void saveUser(User user) throws IOException {
        user.save();
    }

    /**
     * Logs in a user
     *
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
     * Register a new user
     *
     * @param username new username
     * @param password new password
     * @throws UsernameTakenException if username has been taken
     * @throws IOException            if user could not be saved
     */
    public void registerUser(String username, String password)
            throws UsernameTakenException, InvalidUsernameException, InvalidPasswordException, IOException {
        if (!username.matches("^[a-zA-Z0-9._-]{3,}$"))
            throw new InvalidUsernameException();

        if (!password.matches("^[^;]+$"))
            throw new InvalidPasswordException();

        for (User user : users)
            if (user.getName().toLowerCase().equals(username.toLowerCase()))
                throw new UsernameTakenException();

        User newUser = new User(username, password);
        users.add(newUser);
        saveUser(newUser);
    }

    /**
     * Gets a user by username
     *
     * @param username the username of the user you're looking for
     * @return the user with the name of username or null if one doesn't exist
     */
    public User getUser(String username) {
        for (User user : users) {
            if (user.getName().equals(username)) {
                return user;
            }
        }
        return null;
    }
}