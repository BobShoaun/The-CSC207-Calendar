import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;

/**
 * User interface class representing an abstract user interface
 * @author Ng Bob Shoaun
 */
public abstract class UserInterface {

    private Scanner scanner = new Scanner(System.in);

    public abstract void display();

    public abstract void show();

    protected String getStringInput(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine();
        return input;
    }

    /**
     *
     * @param prompt: message to prompt user on what to input
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
     *
     * @param min: minimum acceptable int, inclusive
     * @param max: maximum acceptable int, inclusive
     * @return validated int
     */
    protected int getIntInput(String prompt, int min, int max) {
        System.out.print(prompt);
        try {
            int input = Integer.parseInt(scanner.nextLine());
            if (input < min || max < input)
                throw new Exception ("Number out of Range");
            return input;
        } catch (Exception e) {
            return getIntInput("Invalid number, try again: ", min, max);
        }
    }

    protected String getDurationInput() {
        return scanner.nextLine();
    }

    protected GregorianCalendar getDateInput (String prompt) {
        // DD/MM/YYYY HH:MM:SS
        System.out.print(prompt);
        GregorianCalendar calendar = new GregorianCalendar();
        String dateString = scanner.nextLine();
        try {
            Date date = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").parse(dateString);
            calendar.setTime(date);
        } catch (ParseException e) {
            return getDateInput("Please re-enter a valid date: ");
        }
        return calendar;
    }

    protected void displayLine (int length) {
        for (int i = 0; i < length; i++)
            System.out.print('=');
        System.out.println();
    }

    /**
     * Helper method for displaying a list of options and handling input for them.
     * @param options
     * @return
     */
    protected int getOptionsInput (String[] options) {
        displayLine(70);
        for (int i = 0; i < options.length; i++)
            System.out.println(i + 1 + ") " + options[i]);
        displayLine(70);
        return getIntInput("Choose an option: ", 1, options.length);
    }

}