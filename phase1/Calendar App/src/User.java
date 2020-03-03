import java.io.File;
import java.io.Serializable;
import java.util.Scanner;

/**
 * User class
 * @author Ng Bob Shoaun
 */
public class User implements StringParsable {

    private String name;
    private String password;
    private Calendar calendar;

    public String getName () { return name; }

    public User (String name, String password, Calendar calendar) {
        this.name = name;
        this.password = password;
        this.calendar = calendar;
    }

    public User (String string) {
        unparse(string);
    }

    public boolean authenticate (String name, String password) {
        return this.name == name && this.password == password;
    }

    @Override
    public void unparse(String string) {
        String[] split = string.split("\\s+"); // split text by whitespaces
        this.name = split[0];
        this.password = split[1];
    }

    @Override
    public String parse() {
        return toString();
    }

    @Override
    public String toString () {
        return name + " " + password;
    }

}
