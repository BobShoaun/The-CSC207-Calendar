package consoleui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;

/**
 * User interface class representing an abstract user interface
 *
 * @author Ng Bob Shoaun
 */
public abstract class UserInterface {

    private Scanner scanner = new Scanner(System.in);

    /**
     * Displays graphical information from this user interface
     */
    public abstract void display();

    /**
     * Activates this user interface.
     */
    public abstract void show();

    /**
     * Sources and validates the user's string input
     *
     * @param prompt message to prompt the user on what to input
     * @return validated string input
     */
    protected String getStringInput(String prompt) {
        return getStringInput(prompt, new ArrayList<>());
    }

    /**
     * Sources and validates the user's string input
     *
     * @param prompt      message to prompt the user on what to input
     * @param bannedWords words that will not be validated.
     * @return validated string input
     */
    protected String getStringInput(String prompt, List<String> bannedWords) {
        System.out.print(prompt);
        String input;
        boolean first = true;
        do {
            if (!first)
                System.out.println("This input is not valid/has already been chosen");
            input = scanner.nextLine();
            first = false;
        } while (bannedWords.contains(input));
        return input;
    }

    /**
     * Sources and validates the user's word input, only one word allowed
     *
     * @param prompt message to prompt user on what to input
     * @return validated word, no spaces allowed
     */
    protected String getWordInput(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine();
        if (input.contains(" "))
            return getWordInput("Only one word allowed, try again: ");
        return input;
    }

    /**
     * Sources and validates the user's integer input
     *
     * @param prompt message to prompt the user on what to input
     * @param min:   minimum acceptable int, inclusive
     * @param max:   maximum acceptable int, inclusive
     * @return validated int
     */
    protected int getIntInput(String prompt, int min, int max) {
        System.out.print(prompt);
        try {
            int input = Integer.parseInt(scanner.nextLine());
            if (input < min || max < input)
                throw new Exception("Number out of Range");
            return input;
        } catch (Exception e) {
            return getIntInput("Invalid number, try again: ", min, max);
        }
    }

    /**
     * Sources and validates user input of a duration in the form:
     * <amount> <type> where amount is a number and type is { w, d, h, m, s } corresponding to
     * Week, day, hour, minute, seconds
     *
     * @param prompt message to prompt the user on what to input
     * @return Duration object created based on the user's input
     */
    protected Duration getDurationInput(String prompt) {
        System.out.print(prompt + " Format: Number [w/d/h/m/s]");
        String[] split = scanner.nextLine().split("\\s+"); // split text by whitespaces
        int amount;
        try {
            amount = Integer.parseInt(split[0]);
        } catch (NumberFormatException n) {
            return getDurationInput("Invalid duration, try again: ");
        }
        switch (split[1]) {
            case "w":
                return Duration.ofDays(7 * amount);
            case "d":
                return Duration.ofDays(amount);
            case "h":
                return Duration.ofHours(amount);
            case "m":
                return Duration.ofMinutes(amount);
            case "s":
                return Duration.ofSeconds(amount);
        }
        return getDurationInput("Please re-enter a valid duration <amount> <{w,d,h,m,s}>: ");
    }

    /**
     * Get the date input, not allowing null values.
     *
     * @param prompt Prompt from user
     * @return The GregorianCalendar with the time
     */
    protected GregorianCalendar getDateInput(String prompt) {
        return getDateInput(prompt, false);
    }

    /**
     * Sources and validates user input of a date
     * in the form DD/MM/YYYY HH:MM
     *
     * @param prompt    message to prompt the user on what to input
     * @param allowNull whether a null date is acceptable as an output
     * @return validated input as a GregorianCalendar object
     */
    protected GregorianCalendar getDateInput(String prompt, boolean allowNull) {
        // DD/MM/YYYY HH:MM:SS
        System.out.print(prompt + " (DD/MM/YYYY HH:MM) ");
        GregorianCalendar calendar = new GregorianCalendar();
        String dateString = scanner.nextLine();
        try {
            Date date = new SimpleDateFormat("dd/MM/yyyy hh:mm").parse(dateString);
            calendar.setTime(date);
        } catch (ParseException e) {
            if (allowNull && dateString.equals(""))
                return null;
            return getDateInput("Please re-enter a valid date: ", allowNull);
        }
        return calendar;
    }

    /**
     * Draw a line to the console.
     */
    protected void displayLine() {
        for (int i = 0; i < 70; i++)
            System.out.print("=");
        System.out.println();
    }

    /**
     * Helper method for displaying a list of options and handling input for them.
     *
     * @param options The names of the menu options
     * @return The number of the chosen option
     */
    protected int getOptionsInput(String[] options) {
        displayLine();
        for (int i = 0; i < options.length; i++)
            System.out.println("[" + i + "] " + options[i]);
        displayLine();
        return getIntInput("Choose an option: ", 0, options.length - 1);
    }


}