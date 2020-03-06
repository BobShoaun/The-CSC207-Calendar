import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;

/**
 * User interface class representing an abstract user interface
 * @author Ng Bob Shoaun
 */
public abstract class UserInterface {

    private Scanner scanner = new Scanner(System.in);

    public abstract void display();

    public abstract void show();

    protected String getStringInput(String prompt) {
        return getStringInput(prompt, new ArrayList<>());
    }

    protected String getStringInput(String prompt, List<String> bannedWords) {
        System.out.print(prompt);
        String input;
        boolean first = true;
        do{
            if(!first){
                System.out.println("This input is not valid/has already been chosen");
            }
            input = scanner.nextLine();
            first = false;
        } while(bannedWords.contains(input));
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
                throw new Exception("Number out of Range");
            return input;
        } catch (Exception e) {
            return getIntInput("Invalid number, try again: ", min, max);
        }
    }

    protected Duration getDurationInput(String prompt) {
        // TODO: complete
        return null;
    }

    protected GregorianCalendar getDateInput(String prompt) {
        return getDateInput(prompt, false);
    }

    protected GregorianCalendar getDateInput(String prompt, boolean allowNull) {
        // DD/MM/YYYY HH:MM:SS
        System.out.print(prompt + " (DD/MM/YYYY HH:MM:SS)");
        GregorianCalendar calendar = new GregorianCalendar();
        String dateString = scanner.nextLine();
        try {
            Date date = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").parse(dateString);
            calendar.setTime(date);
        } catch (ParseException e) {
            if(allowNull && dateString.equals(""))
                return null;
            return getDateInput("Please re-enter a valid date: (DD/MM/YYYY HH:MM:SS)");
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
            System.out.println("[ " + i + " ] " + options[i]);
        displayLine(70);
        return getIntInput("Choose an option: ", 0, options.length);
    }


}