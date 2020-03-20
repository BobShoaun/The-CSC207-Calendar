package consoleui;

import exceptions.PasswordMismatchException;
import exceptions.UsernameTakenException;
import user.UserManager;

import java.io.IOException;

/**
 * consoleui.UserManagerUI, the starting point of the program
 */
public class UserManagerUI extends UserInterface {

    private UserManager userManager;

    /**
     * Constructor for UserManagerUI
     * @param userManager the userManager to display
     */
    public UserManagerUI(UserManager userManager) {
        this.userManager = userManager;
    }

    /**
     * Display graphics
     */
    @Override
    public void display() {
        System.out.println("\n" +
                "   ██████╗ █████╗ ██╗     ███████╗███╗   ██╗██████╗  █████╗ ██████╗ \n" +
                "  ██╔════╝██╔══██╗██║     ██╔════╝████╗  ██║██╔══██╗██╔══██╗██╔══██╗\n" +
                "  ██║     ███████║██║     █████╗  ██╔██╗ ██║██║  ██║███████║██████╔╝\n" +
                "  ██║     ██╔══██║██║     ██╔══╝  ██║╚██╗██║██║  ██║██╔══██║██╔══██╗\n" +
                "  ╚██████╗██║  ██║███████╗███████╗██║ ╚████║██████╔╝██║  ██║██║  ██║\n" +
                "   ╚═════╝╚═╝  ╚═╝╚══════╝╚══════╝╚═╝  ╚═══╝╚═════╝ ╚═╝  ╚═╝╚═╝  ╚═╝\n");
    }

    /**
     * Show UI
     */
    @Override
    public void show() {
        displayLine();
        display();
        boolean running = true;
        while (running) {
            int option = getOptionsInput(new String[]{"Exit", "Login", "Register"});
            switch (option) {
                case 0:
                    running = false;
                    try {
                        userManager.saveUsers();
                    } catch (IOException ee) {
                        System.out.println("Failed to save users!");
                    }
                    break;
                case 1:
                    showLoginMenu();
                    break;
                case 2:
                    showRegisterMenu();
                    break;
            }
        }
    }

    private void showLoginMenu() {
        displayLine();
        String username, password;
        boolean firstTry = true;
        do {
            if (!firstTry)
                System.out.println("Invalid username and/or password, try again.");
            username = getWordInput("Enter username: ");
            password = getWordInput("Enter password: ");
            firstTry = false;
        } while (!userManager.loginUser(username, password));
        displayLine();
        showCalendarManagerUI();
    }

    private void showRegisterMenu () {
        displayLine();
        String username = getWordInput("Enter username: ");
        String password = getWordInput("Enter password: ");
        String confirmPassword = getWordInput("Confirm password: ");
        try {
            userManager.registerUser(username, password, confirmPassword);
            System.out.println("User registered successfully...");
            userManager.loginUser(username, password);
            showCalendarManagerUI();
        } catch (UsernameTakenException e) {
            System.out.println("Username already taken!");
            showRegisterMenu();
        } catch (PasswordMismatchException e) {
            System.out.println("Password mismatch!");
            showRegisterMenu();
        } catch (IOException ee) {
            System.out.println("Failed to create user:" + ee.toString());
        }
    }

//    private void showCalendar() {
//        CalendarUI calendarUI = new CalendarUI(userManager.getCurrentUser(), userManager.getCurrentUser().getCalendar());
//        calendarUI.show();
//    }

    private void showCalendarManagerUI() {
        CalendarManagerUI calendarManagerUI = new CalendarManagerUI(userManager.getCurrentUser());
        calendarManagerUI.show();
    }

    /**
     * Main Method
     * @param args
     */
    public static void main (String[] args) {
        UserManager userManager = new UserManager();
        try {
            userManager.loadUsers();
        } catch(IOException ee){
            System.out.print("Unhandled exception: " + ee.toString());
        }
        new UserManagerUI(userManager).show();
    }
}