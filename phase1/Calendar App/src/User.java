import java.io.Serializable;

/**
 * User class
 * @author Ng Bob Shoaun
 */
public class User implements TextFileSerializable<User> {

    private String name;
    private String password;
    private Calendar calendar;

    public String getName () { return name; }

    public User (String name, String password, Calendar calendar) {
        this.name = name;
        this.password = password;
        this.calendar = calendar;
    }

    public boolean authenticate (String name, String password) {
        return this.name == name && this.password == password;
    }

    @Override
    public User deserialize(String text) {

    }

    @Override
    public String serialize(User obj) {

    }
}
