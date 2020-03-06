
import java.util.GregorianCalendar;

/**
 * User class
 * @author Ng Bob Shoaun
 */
public class User implements StringParsable {

    private String name;
    private String password;
    private Calendar calendar;
    private DataSaver dataSaver;

    public String getName () { return name; }

    public Calendar getCalendar () { return calendar; }

    public User (String name, String password) {
        this.name = name;
        this.password = password;
        dataSaver = new DataSaver(name);
        this.calendar = new Calendar(dataSaver);
    }

    public User (String string) {
        unparse(string);
    }

    public boolean authenticate (String name, String password) {
        return this.name.equals(name) && this.password.equals(password);
    }

    @Override
    public void unparse(String string) {
        String[] split = string.split("\\s+"); // split text by whitespaces
        this.name = split[0];
        this.password = split[1];
        this.calendar = new Calendar(new DataSaver(name));
    }

    @Override
    public String parse() {
        return toString();
    }

    @Override
    public String toString () {
        return name + " " + password;
    }

    public GregorianCalendar getLastLoginTime(){
        GregorianCalendar gregorianCalendar= new GregorianCalendar();
        gregorianCalendar.set(2000, 1, 1, 1, 0);
        return gregorianCalendar;
    }

    public void updateLastLoginTime(){throw new UnsupportedOperationException();}
}
