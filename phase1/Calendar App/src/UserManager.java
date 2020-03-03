import java.util.ArrayList;

/**
 * UserManager class
 * @author Ng Bob Shoaun
 */
public class UserManager {

    private ArrayList<User> users;
    private User currentUser;

    public UserManager () {
        users = new ArrayList<> ();
        currentUser = null;
    }

    private void loadUsers () {

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
            if (user.getName() == username)
                throw new UsernameTakenException ();

        users.add(new User(username, password, new Calendar()));
        return true;
    }

}
