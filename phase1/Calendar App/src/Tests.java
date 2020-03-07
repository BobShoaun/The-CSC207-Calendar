import alert.Alert;
import alert.AlertCollection;
import consoleui.EventCollectionUI;
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
        if (!a.getTime().getTime().equals(d)) throw new AssertionError("Alert");
        if (!a.getEventId().equals("asdf123")) throw new AssertionError("Alert");

        Alert b = new Alert("id2", "1583515830000");
        if (!a.getTime().equals(b.getTime())) throw new AssertionError("Alert");
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

    static void testEventsGenerator() throws InvalidDateException {
        Event e = new Event("test", "Go Shopping",
                new GregorianCalendar(120, Calendar.MARCH, 6, 11, 0),
                new GregorianCalendar(120, Calendar.MARCH, 6, 12, 0));


        Date date = new Date(120, Calendar.MAY, 12);
        Date date2 = new Date(120, Calendar.MAY, 20);
        List<Duration> durs = new ArrayList<>();
        durs.add(Duration.ofDays(1));
        EventGenerator eg = new EventGenerator(e, date, date2, durs);

        List<Event> events = new ArrayList<>(eg.generateEvents());
        if (events.size() != 9) throw new AssertionError("EventGenerator problem");
    }

    static void testECSave() throws InvalidDateException, IOException {
        String name = "testseries";
        Event event = new Event("test", "Go Shopping",
                new GregorianCalendar(2020, Calendar.MARCH, 6, 11, 0),
                new GregorianCalendar(2020, Calendar.MARCH, 6, 12, 0));

        Event e2 = new Event("hjdkal", "whatever", new GregorianCalendar(2020, Calendar.MARCH, 6, 11, 0),
                new GregorianCalendar(2020, Calendar.MARCH, 6, 12, 0));
        List<Event> eve = new ArrayList<>();
        eve.add(event);
        eve.add(e2);
        DataSaver ds = new DataSaver("tests");
        EventCollection EC = new EventCollection(name, eve, ds);
        EC.save();
    }
    static void testLoad() throws InvalidDateException {
        String name = "LETS TEST";
        Event event = new Event("test", "Go Shopping",
                new GregorianCalendar(2020, Calendar.MARCH, 6, 11, 0),
                new GregorianCalendar(2020, Calendar.MARCH, 6, 12, 0));

        Event e2 = new Event("hjdkal", "whatever", new GregorianCalendar(2020, Calendar.MARCH, 6, 11, 0),
                new GregorianCalendar(2020, Calendar.MARCH, 6, 12, 0));
        List<Event> eve = new ArrayList<>();
        eve.add(event);
        eve.add(e2);

        DataSaver ds = new DataSaver("tests");
        EventCollection EC = new EventCollection(name, eve, ds);
        EC.load("testseries");

        List<Event> result = new ArrayList<>();
        for (Event e : EC) {
            result.add(e);
        }

        if (result.size() != 2) throw new AssertionError("Bad loading");
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
        if (29 != size) throw new AssertionError("CG Issue");
    }

    static void testEventCollectionUI() throws InvalidDateException {
//        Event event = new Event("test", "Go Shopping",
//                new GregorianCalendar(2020, Calendar.MARCH, 6, 11, 0),
//                new GregorianCalendar(2020, Calendar.MARCH, 6, 12, 0));
//
//        Event e2 = new Event("hjdkal", "whatever", new GregorianCalendar(2020, Calendar.MARCH, 6, 11, 0),
//                new GregorianCalendar(2020, Calendar.MARCH, 6, 12, 0));
        List<Event> eve = new ArrayList<>();
//        eve.add(event);
//        eve.add(e2);
        DataSaver saver = new DataSaver("testUser");
        EventCollection regular = new EventCollection("", eve, saver);
        user.Calendar cal = new user.Calendar(saver);
        EventCollectionUI ui = new EventCollectionUI(regular, cal);

    }

    static void testRemove() throws InvalidDateException, IOException {
        Event event = new Event("test", "Go Shopping",
                new GregorianCalendar(2020, Calendar.MARCH, 6, 11, 0),
                new GregorianCalendar(2020, Calendar.MARCH, 6, 12, 0));

        Event e2 = new Event("hjdkal", "whatever", new GregorianCalendar(2020, Calendar.MARCH, 6, 11, 0),
                new GregorianCalendar(2020, Calendar.MARCH, 6, 12, 0));
        List<Event> eve = new ArrayList<>();
        eve.add(event);
        eve.add(e2);
        DataSaver saver = new DataSaver("testUser");
        EventCollection coll = new EventCollection("test", eve, saver);
        coll.removeEvent(e2);

        List<Event> result = new ArrayList<>();
        for (Event e : coll) {
            result.add(e);
        }
        if (result.size() != 1) throw new AssertionError("Error while removing");
        if (result.contains(e2)) throw new AssertionError("Error while removing");

    }

    public static void main(String[] args) throws Exception {
        testCalendarGenerator();
        testAlert();
        testAlertCollection();
        testECSave();
        testLoad();
        testEventsGenerator();
        testEventCollectionUI();
        testRemove();
    }


}
