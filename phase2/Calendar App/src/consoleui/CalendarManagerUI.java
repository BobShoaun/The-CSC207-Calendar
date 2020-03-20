package consoleui;

import user.Calendar;
import user.User;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class CalendarManagerUI extends UserInterface {

    private User user;

    public CalendarManagerUI (User user) {
        this.user = user;
    }

    @Override
    public void display() {

        if (user.isFirstLogin())
            System.out.println("Welcome " + user.getName() + "!!");
        else
            System.out.println("Welcome back, " + user.getName() + "! Your last login was on: " + user.getLastLoginTime().getTime());
    }

    @Override
    public void show() {
        displayLine();
        display();
        while (true) {
            int option = getOptionsInput(new String[]{"Logout", "List calendars", "Add calendar", "Remove calendar"});
            switch (option) {
                case 0:
                    user.setLastLoginTime((GregorianCalendar) GregorianCalendar.getInstance());
                    return;
                case 1:
                    listCalendars();
                    break;
                case 2:
                    addCalendar();
                    break;
                case 3:
                    removeCalendar();
                    break;
            }
        }
    }

    private void listCalendars() {
        displayLine();

        List<String> options = new ArrayList<>();
        options.add("Back");
        for (Calendar calendar : user.getCalendars())
            options.add(calendar.getName());

        if (user.getCalendars().size() == 0)
            System.out.println("You have no calendars to access.");
        else
            System.out.println("Select a calendar to access:");

        int option = getOptionsInput(options.toArray(new String[0]));
        if (option == 0)
            return;
        showCalendarUI(option - 1);
    }

    private void showCalendarUI (int index) {
        CalendarUI calendarUI = new CalendarUI(user, user.getCalendar(index));
        calendarUI.show();
    }

    private void addCalendar() {
        displayLine();
        String calendarName = getStringInput("Enter calendar name: ");
        user.addCalendar(calendarName);
        System.out.println("Calendar added successfully.");
    }

    private void removeCalendar() {
        displayLine();

        List<String> options = new ArrayList<>();
        options.add("Back");
        for (Calendar calendar : user.getCalendars())
            options.add(calendar.getName());

        if (user.getCalendars().size() == 0)
            System.out.println("You have no calendars to remove.");
        else
            System.out.println("Select a calendar to remove:");

        int option = getOptionsInput(options.toArray(new String[0]));
        if (option == 0)
            return;
        user.removeCalendar(option - 1);
        System.out.println("Calendar removed successfully.");
    }

}