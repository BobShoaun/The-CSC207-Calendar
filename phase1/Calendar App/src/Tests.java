import alert.Alert;
import alert.AlertCollection;
import dates.CalendarGenerator;
import dates.EventGenerator;
import event.Event;
import event.EventCollection;
import exceptions.InvalidDateException;
import user.DataSaver;

import java.io.IOException;
import java.time.Duration;
import java.util.*;

public class Tests {
    static void testAlert() {
        Alert a = new Alert("asdf123", new GregorianCalendar());
        Date d = new Date(120, Calendar.MARCH, 6, 12, 30, 30);
        a.setTime(d);
        if (!a.getTime().getTime().equals(d)) throw new AssertionError();
        if (!a.getEventId().equals("asdf123")) throw new AssertionError();

        Alert b = new Alert("id2", "1583515830000");
        if (!a.getTime().equals(b.getTime())) throw new AssertionError();
    }

    static void testAlertCollection() throws Exception {
        Event e = new Event("test", "Go Shopping",
                new GregorianCalendar(120, Calendar.MARCH, 6, 11, 0),
                new GregorianCalendar(120, Calendar.MARCH, 6, 12, 0));
        DataSaver ds = new DataSaver("tests");
        AlertCollection ac = new AlertCollection(e, ds);
        ac.addAlert(new GregorianCalendar(120, Calendar.MARCH, 6, 10, 0));
        ac.addAlert(new GregorianCalendar(120, Calendar.FEBRUARY, 1), Duration.ofDays(1));
        List<Alert> get = ac.getAlerts(new GregorianCalendar(120, Calendar.FEBRUARY, 28),
                new GregorianCalendar(120, Calendar.MARCH, 6, 11, 0));
        if (get.size() != 9) throw new AssertionError();
    }

    static void testEventsGenerator() throws InvalidDateException, IOException {
        Event e = new Event("test", "Go Shopping",
                new GregorianCalendar(120, Calendar.MARCH, 6, 11, 0),
                new GregorianCalendar(120, Calendar.MARCH, 6, 12, 0));


        Date date = new Date(120, Calendar.MAY, 12);
        Date date2 = new Date(120, Calendar.DECEMBER, 11);
        List<Duration> durs = new ArrayList<>();
        durs.add(Duration.ofDays(1));
        EventGenerator eg = new EventGenerator(e, date, date2, durs);
        for (Event event : eg.generateEvents()) {
            System.out.println(event);
        }
    }

    static void testECSave() throws InvalidDateException, IOException {
        String name = "testseries";
        Event event = new Event("test", "Go Shopping",
                new GregorianCalendar(120, Calendar.MARCH, 6, 11, 0),
                new GregorianCalendar(120, Calendar.MARCH, 6, 12, 0));

        Event e2 = new Event("hjdkal", "whatever", new GregorianCalendar(120, Calendar.MARCH, 6, 11, 0),
                new GregorianCalendar(120, Calendar.MARCH, 6, 12, 0));
        List<Event> eve = new ArrayList<>();
        eve.add(event);
        eve.add(e2);
        for (Event e : eve) {
            System.out.println(e);
        }
        DataSaver ds = new DataSaver("testPath");
        EventCollection EC = new EventCollection(name, eve, ds);
        EC.save();
    }
    static void testLoad() throws InvalidDateException {
        String name = "LETS TEST";
        Event event = new Event("test", "Go Shopping",
                new GregorianCalendar(120, Calendar.MARCH, 6, 11, 0),
                new GregorianCalendar(120, Calendar.MARCH, 6, 12, 0));

        Event e2 = new Event("hjdkal", "whatever", new GregorianCalendar(120, Calendar.MARCH, 6, 11, 0),
                new GregorianCalendar(120, Calendar.MARCH, 6, 12, 0));
        List<Event> eve = new ArrayList<>();
        eve.add(event);
        eve.add(e2);
//        for (Event e:eve) {
//            System.out.println(e);
//        }
        DataSaver ds = new DataSaver("");
        EventCollection EC = new EventCollection(name, eve, ds);
        EC.load("testseries");
        for (Event e : EC) {
            System.out.println(e);
        }
    }

    static void testCalendarGenerator() {
        CalendarGenerator cg = new CalendarGenerator(new GregorianCalendar(120, Calendar.FEBRUARY, 1),
                Arrays.asList(Duration.ofDays(1)),
                new GregorianCalendar(120, Calendar.FEBRUARY, 29));
        List<GregorianCalendar> dates = new ArrayList<>();
        for (GregorianCalendar date : cg) {
            dates.add(date);
        }
        int size = dates.size();
        if (29 != size) throw new AssertionError();
    }

    public static void main(String[] args) throws Exception {
//        testCalendarGenerator();
        testAlert();
        testAlertCollection();
        testEventsGenerator();
        testLoad();
        testECSave();
        testEventsGenerator();
    }

}
