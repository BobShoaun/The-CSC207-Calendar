
import exceptions.NullLastLoginException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
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
    private GregorianCalendar lastLoginTime;
    boolean firstLogin = false;

    public String getName () { return name; }

    public Calendar getCalendar () { return calendar; }

    public User (String name, String password) {
        this.name = name;
        this.password = password;
        dataSaver = new DataSaver(name);
        this.calendar = new Calendar(dataSaver);
        firstLogin = true;
    }

    public User (String string) {
        firstLogin = false;
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
        try {
            Date date = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").parse(split[2] + " " + split[3]);
            this.lastLoginTime = new GregorianCalendar();
            this.lastLoginTime.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {
            return;
        }
    }

    @Override
    public String parse() {
        return toString();
    }

    @Override
    public String toString () {
        if(lastLoginTime == null)
            return name + " " + password;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        String dateFormatted = sdf.format(lastLoginTime.getTime());
        return name + " " + password + " " + dateFormatted;
    }

    public GregorianCalendar getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime (GregorianCalendar time) {
        this.lastLoginTime = time;
    }

}
