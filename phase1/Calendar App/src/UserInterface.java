import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;

public abstract class UserInterface {

    private Scanner scanner = new Scanner(System.in);

    public abstract void display();

    public abstract void showUI();

    public String getStringInput() {
        return scanner.nextLine();
    }

    public int getIntInput() {
        return scanner.nextInt();
    }

    public String getDurationInput() {
        return scanner.nextLine();
    }

    public GregorianCalendar getDateInput () {
        // DD/MM/YYYY HH:MM:SS
        System.out.println("Please enter a date in the format DD/MM/YYYY HH:MM:SS :");
        GregorianCalendar calendar = new GregorianCalendar();
        String dateString = scanner.nextLine();
        try {
            Date date = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").parse(dateString);
            calendar.setTime(date);
        } catch (ParseException e) {
            System.out.println("Please re-enter a valid date: ");
            getDateInput();
        }
        return calendar;
    }



}


