
/**
 * User class
 * @author Ng Bob Shoaun
 */
public class User implements StringParsable {

    private String name;
    private String password;
    private String path;
    private Calendar calendar;

    public String getName () { return name; }

    public String getPath () { return path; }

    public Calendar getCalendar () { return calendar; }

    public User (String name, String password, Calendar calendar) {
        this.name = name;
        this.password = password;
        this.calendar = calendar;
    }

    public User (String string, String path) {
        unparse(string);
        this.path = path;
    }

    public boolean authenticate (String name, String password) {
        return this.name.equals(name) && this.password.equals(password);
    }

    @Override
    public void unparse(String string) {
        String[] split = string.split("\\s+"); // split text by whitespaces
        this.name = split[0];
        this.password = split[1];
        this.calendar = new Calendar();
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
