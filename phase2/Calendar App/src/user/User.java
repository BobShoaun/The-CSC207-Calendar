package user;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * User class representing a User for the calendar
 *
 * @author Ng Bob Shoaun
 */
public class User implements StringParsable {

    private String name;
    private String password;
    private Calendar calendar;
    private GregorianCalendar lastLoginTime;
    private boolean firstLogin;

    /**
     * getter for the user's name
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * getter for the user's calendar
     * @return calendar
     */
    public Calendar getCalendar() {
        return calendar;
    }

    /**
     * getter for firstLogin, whether if its the user's first time logging in
     * @return firstLogin
     */
    public boolean getFirstLogin() {
        return firstLogin;
    }

    /**
     * Getter for last login time
     * @return lastLoginTime
     */
    public GregorianCalendar getLastLoginTime() {
        return lastLoginTime;
    }

    /**
     * setter for last login time
     * @param time new last login time
     */
    public void setLastLoginTime (GregorianCalendar time) {
        this.lastLoginTime = time;
    }

    /**
     * Constructor for User
     * @param name the user's name
     * @param password the user's password
     */
    public User(String name, String password) {
        this.name = name;
        this.password = password;
        this.calendar = new Calendar(new DataSaver(name));
        firstLogin = true;
        lastLoginTime = calendar.getTime();
    }

    /**
     * Constructor for User
     * @param string string containing the user's data.
     */
    public User (String string) {
        firstLogin = false;
        unparse(string);
    }

    /**
     * Check if the name and password matches this user's
     * @param name name to check
     * @param password password to check
     * @return whether the name and password are an exact match
     */
    public boolean authenticate (String name, String password) {
        return this.name.equals(name) && this.password.equals(password);
    }

    /**
     * Unparse a string's data into this user's data.
     * @param string string containing the user's data.
     */
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
        }
    }

    /**
     * Creates a string representation of this user
     * @return string representation
     */
    @Override
    public String parse() {
        return toString();
    }

    /**
     * Creates a string representation of this user
     * @return string representation
     */
    @Override
    public String toString () {
        if(lastLoginTime == null)
            return name + " " + password;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        String dateFormatted = sdf.format(lastLoginTime.getTime());
        return name + " " + password + " " + dateFormatted;
    }


}
