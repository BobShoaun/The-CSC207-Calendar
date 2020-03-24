package user;

import entities.Event;

import javax.xml.crypto.Data;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User class representing a User for the calendar
 *
 * @author Ng Bob Shoaun
 */
public class User implements StringParsable {

    private String name;
    private String password;
    private List<Calendar> calendars;
    private GregorianCalendar lastLoginTime;
    private boolean firstLogin;
    private DataSaver dataSaver;
    private EventSharer eventSharer;

    /**
     * getter for the user's name
     * @return name
     */
    public String getName() {
        return name;
    }


    public Calendar getCalendar(int index) {
        return calendars.get(index);
    }

    public void addCalendar (String calendarName) {
        this.calendars.add(new Calendar(calendarName, new DataSaver("./users/" + this.name + "/" + calendarName)));
        saveCalendars();
    }

    public void removeCalendar (int index) {
        try {
            dataSaver.deleteDirectory(this.calendars.get(index).getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.calendars.remove(index);
    }

    /**
     * Gets a calendar by name
     * @param name the name of the calendar you're looking for
     * @return the calendar with the title of name or null if one doesn't exist
     */
    public Calendar getCalendar(String name){
        for (Calendar calendar : calendars){
            if (calendar.getName().equals(name)){
                return calendar;
            }
        }
        return null;
    }

    public List<Calendar> getCalendars () { return this.calendars; }

    /**
     * getter for firstLogin, whether if its the user's first time logging in
     * @return firstLogin
     */
    public boolean isFirstLogin() {
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
     * @param eventSharer the eventSharer
     */
    public User(String name, String password, EventSharer eventSharer) {
        this.eventSharer = eventSharer;
        this.name = name;
        this.password = password;
        this.calendars = new ArrayList<>();
        firstLogin = true;
        lastLoginTime = (GregorianCalendar) GregorianCalendar.getInstance();
        dataSaver = new DataSaver("./users/" + name);
    }

    /**
     * Constructor for User
     * @param credentials string containing the user's data.
     */
    public User (String credentials) {
        firstLogin = false;
        unparse(credentials);
        dataSaver = new DataSaver("./users/" + name);
        loadCalendars();
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
        String[] split = string.split(";"); // split text by whitespaces
        this.name = split[0];
        this.password = split[1];
        this.calendars = new ArrayList<>();
        try {
            Date date = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").parse(split[2]);
            this.lastLoginTime = new GregorianCalendar();
            this.lastLoginTime.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {
            this.lastLoginTime = (GregorianCalendar) GregorianCalendar.getInstance();
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
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        String dateFormatted = sdf.format(lastLoginTime.getTime());
        return name + ";" + password + ";" + dateFormatted;
    }

    private void loadCalendars() {
        File[] files = dataSaver.getFilesInDirectory("");
        for (File file : files) {
            if (file.isFile()) // dont want credentials.txt
                continue;
            this.addCalendar(file.getName());
        }
    }

    private void saveCalendars() {
        for (Calendar calendar : calendars)
            dataSaver.makeDirectory(calendar.getName());
    }
}
