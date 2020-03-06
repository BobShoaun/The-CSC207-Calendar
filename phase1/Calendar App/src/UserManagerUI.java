import exceptions.PasswordMismatchException;
import exceptions.UsernameTakenException;

import java.io.IOException;

/**
 * UserManagerUI, the starting point of the program
 */
public class UserManagerUI extends UserInterface {

    private UserManager userManager;

    public UserManagerUI(UserManager userManager) {
        this.userManager = userManager;
    }

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

    @Override
    public void show() {
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
        String username, password;
        boolean firstTry = true;
        do {
            if (!firstTry)
                System.out.println("Invalid username and/or password, try again.");
            username = getWordInput("Enter username: ");
            password = getWordInput("Enter password: ");
            firstTry = false;
        } while (!userManager.loginUser(username, password));
        if(!userManager.getCurrentUser().firstLogin) {
            System.out.println("Welcome back, " + username + "! You last login was on: " + userManager.getCurrentUser().getLastLoginTime().getTime());
        } else {
            System.out.println("Welcome " + username + ", take a look at your brand new calendar!");
        }
        CalendarUI calendarUI = new CalendarUI(userManager.getCurrentUser(), userManager.getCurrentUser().getCalendar());
        calendarUI.show();
    }

    private void showRegisterMenu () {
        String username = getWordInput("Enter username: ");
        String password = getWordInput("Enter password: ");
        String confirmPassword = getWordInput("Confirm password: ");
        try {
            userManager.registerUser(username, password, confirmPassword);
            System.out.println("User registered successfully...");
            userManager.loginUser(username, password);
            CalendarUI calendarUI = new CalendarUI(userManager.getCurrentUser(), userManager.getCurrentUser().getCalendar());
            calendarUI.show();
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