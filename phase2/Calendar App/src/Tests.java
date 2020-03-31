import dates.CalendarGenerator;
import entities.Alert;
import entities.AlertCollection;
import entities.Event;
import exceptions.*;
import user.DataSaver;
import user.UserManager;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

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


    static void testECSave() throws InvalidDateException, IOException {
        String name = "testseries";
        Event event = new Event("test", "Go Shopping",
                new GregorianCalendar(2020, Calendar.MARCH, 6, 11, 0),
                new GregorianCalendar(2020, Calendar.MARCH, 6, 12, 0));

//        Event e2 = new Event("hjdkal", "whatever", new GregorianCalendar(2020, Calendar.MARCH, 6, 11, 0),
//                new GregorianCalendar(2020, Calendar.MARCH, 6, 12, 0));
//        List<Event> eve = new ArrayList<>();
//        eve.add(event);
//        eve.add(e2);
//        DataSaver ds = new DataSaver("tests");
//        EventCollection EC = new EventCollection(name, eve, ds);
//        EC.save();
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

//        DataSaver ds = new DataSaver("tests");
//        EventCollection EC = new EventCollection(name, eve, ds);
//        EC.load("testseries");
//
//        List<Event> result = new ArrayList<>();
//        for (Event e : EC) {
//            result.add(e);
//        }
//
//        if (result.size() != 2) throw new AssertionError("Bad loading");
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

    static void testEventCollectionUI() {
//        Event event = new Event("test", "Go Shopping",
//                new GregorianCalendar(2020, Calendar.MARCH, 6, 11, 0),
//                new GregorianCalendar(2020, Calendar.MARCH, 6, 12, 0));
//
//        Event e2 = new Event("hjdkal", "whatever", new GregorianCalendar(2020, Calendar.MARCH, 6, 11, 0),
//                new GregorianCalendar(2020, Calendar.MARCH, 6, 12, 0));
//        List<Event> eve = new ArrayList<>();
////        eve.add(event);
////        eve.add(e2);
//        DataSaver saver = new DataSaver("tests");
//        EventCollection regular = new EventCollection("", eve, saver);
//        user.Calendar cal = new user.Calendar("test", saver);
//        EventCollectionUI ui = new EventCollectionUI(regular, cal);

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
        DataSaver saver = new DataSaver("tests");
//        EventCollection coll = new EventCollection("test", eve, saver);
//        coll.removeEvent(e2);
//
//        List<Event> result = new ArrayList<>();
//        for (Event e : coll) {
//            result.add(e);
//        }
//        if (result.size() != 1) throw new AssertionError("Error while removing");
//        if (result.contains(e2)) throw new AssertionError("Error while removing");

    }

    static void testEventGetDuration() throws InvalidDateException {
        Event event = new Event("test", "Go Shopping",
                new GregorianCalendar(2020, Calendar.MARCH, 6, 11, 0),
                new GregorianCalendar(2020, Calendar.MARCH, 6, 12, 0));
        long millis = event.getDuration();
        String dur = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), // The change is in this line
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        System.out.println(event.getName() + " lasts for " + dur);
    }

    private static void testUserManager () throws IOException, UsernameTakenException {
        // delete testUserName user from the users directory before testing
        UserManager userManager = new UserManager();
        userManager.loadUsers();
        try {
            userManager.registerUser("testUserName", "testPassword", "testPassword");
        } catch (PasswordMismatchException p) {
            throw new AssertionError("Password checking has bugs!");
        } catch (InvalidUsernameException e) {
            e.printStackTrace();
        } catch (InvalidPasswordException e) {
            e.printStackTrace();
        }
        assert userManager.loginUser("testUserName", "testPassword");
        assert userManager.getCurrentUser() != null;
        assert userManager.getCurrentUser().getName() == "testUserName";
        userManager.logoutCurrentUser();
        userManager.saveUsers();
    }

//    private static void testUser() {
//        User user = new User("test", "123");
//        assert user.authenticate("test", "123");
//        user.setLastLoginTime(new GregorianCalendar());
//        String userString = user.parse();
//        User user2 = new User(userString);
//        assert user2.getName() == user.getName();
//        assert user2.getLastLoginTime() == user.getLastLoginTime();
//        assert user2.isFirstLogin() == user.isFirstLogin();
////        assert user2.getCalendar() == user.getCalendar();
//        assert user2.parse() == user.parse();
//    }

    public static void main(String[] args) throws Exception {
//        testCalendarGenerator();
//        testAlert();
//        testAlertCollection();
//        testECSave();
//        testLoad();
//        testEventsGenerator();
//        testEventCollectionUI();
//        testRemove();
//        testEventGetDuration();
//        testUserManager();
//        testUser();
    }


}
