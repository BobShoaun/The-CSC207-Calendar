package user;

import javax.naming.InvalidNameException;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * User class representing a User for the calendar
 *
 * @author Ng Bob Shoaun
 */
public class User {

    private final String name;
    private String password;
    private final List<Calendar> calendars;
    private GregorianCalendar lastLoginTime;
    private final boolean firstLogin;
    private final DataSaver dataSaver;
    private boolean darkTheme;

    /**
     * getter for the user's preference of a dark theme or not
     *
     * @return True iff theme is dark
     */
    public boolean getDarkTheme() {
        return darkTheme;
    }

    /**
     * sets the user's preference of dark theme
     *
     * @param darkTheme True if the theme should be dark
     */
    public void setDarkTheme(boolean darkTheme) {
        this.darkTheme = darkTheme;
    }

    /**
     * getter for the user's name
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * getter for one of the user's calendar
     *
     * @param index of the calendar
     * @return calendar
     */
    public Calendar getCalendar(int index) {
        return calendars.get(index);
    }

    /**
     * Add a new Calendar to the User
     *
     * @param calendarName Name of new Calendar
     * @throws InvalidNameException if the name is invalid
     */
    public void addCalendar(String calendarName) throws InvalidNameException {
        if (calendarName.equals(" ") | calendarName.equals(""))
            throw new InvalidNameException();
        this.calendars.add(new Calendar(calendarName, new DataSaver("./users/" + this.name + "/" + calendarName)));
        saveCalendars();
    }

    /**
     * Remove a calendar from the user
     *
     * @param index of calendar to remove
     */
    public void removeCalendar(int index) {
        try {
            dataSaver.deleteDirectory(this.calendars.get(index).getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.calendars.remove(index);
    }

    /**
     * Gets a calendar by name
     *
     * @param name the name of the calendar you're looking for
     * @return the calendar with the title of name or null if one doesn't exist
     */
    public Calendar getCalendar(String name) {
        for (Calendar calendar : calendars) {
            if (calendar.getName().equals(name)) {
                return calendar;
            }
        }
        return null;
    }

    /**
     * Get all of user's calendars
     *
     * @return List of Calendars
     */
    public List<Calendar> getCalendars() {
        return this.calendars;
    }

    /**
     * getter for firstLogin, whether if its the user's first time logging in
     *
     * @return firstLogin
     */
    public boolean isFirstLogin() {
        return firstLogin;
    }

    /**
     * Getter for last login time
     *
     * @return lastLoginTime
     */
    public GregorianCalendar getLastLoginTime() {
        return lastLoginTime;
    }

    /**
     * setter for last login time
     *
     * @param time new last login time
     */
    public void setLastLoginTime(GregorianCalendar time) {
        this.lastLoginTime = time;
    }

    /**
     * Logout this user, and saves all necessary information
     *
     * @throws IOException if there is an IO error
     */
    public void logout() throws IOException {
        setLastLoginTime((GregorianCalendar) GregorianCalendar.getInstance());
        save();
    }

    /**
     * Constructor for User
     *
     * @param name     the user's name
     * @param password the user's password
     */
    public User(String name, String password) {
        this.name = name;
        this.password = password;
        this.calendars = new ArrayList<>();
        this.darkTheme = true;
        firstLogin = true;
        lastLoginTime = (GregorianCalendar) GregorianCalendar.getInstance();
        dataSaver = new DataSaver("./users/" + name);
        try {
            addCalendar("Default");
        } catch (InvalidNameException e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructor for User
     *
     * @param credentials string containing the user's data.
     */
    public User(String credentials) {
        firstLogin = false;

        String[] split = credentials.split(";");
        this.name = split[0];
        this.password = split[1];
        this.darkTheme = Boolean.parseBoolean(split[2]);
        try {
            Date date = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").parse(split[3]);
            this.lastLoginTime = new GregorianCalendar();
            this.lastLoginTime.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {
            this.lastLoginTime = (GregorianCalendar) GregorianCalendar.getInstance();
        }

        this.calendars = new ArrayList<>();
        dataSaver = new DataSaver("./users/" + name);
        loadCalendars();
    }

    /**
     * Check if the name and password matches this user's
     *
     * @param name     name to check
     * @param password password to check
     * @return whether the name and password are an exact match
     */
    public boolean authenticate(String name, String password) {
        return this.name.equals(name) && this.password.equals(password);
    }

    /**
     * Creates a string representation of this user
     *
     * @return string representation
     */
    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        String dateFormatted = sdf.format(lastLoginTime.getTime());
        return name + ";" + password + ";" + darkTheme + ";" + dateFormatted;
    }

    private void loadCalendars() {
        File[] files = dataSaver.getFilesInDirectory("");
        for (File file : files) {
            if (file.isFile()) // don't want credentials.txt
                continue;
            this.calendars.add(dataSaver.loadCalendar(file.getName()));
        }
    }

    private void saveCalendars() {
        for (Calendar calendar : calendars)
            dataSaver.makeDirectory(calendar.getName());
    }

    /**
     * Save the user's credentials and information into disc
     *
     * @throws IOException if there was an error saving the user credentials.
     */
    public void save() throws IOException {
        System.out.println("Saving user: " + toString());
        dataSaver.saveToFile("credentials.txt", toString());
    }

}
