import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class Tests {

    static void testAlert() {
        Alert a = new Alert("asdf123", new GregorianCalendar());
        Date d = new Date(2020, Calendar.APRIL, 6, 12, 30, 30);
        assert a.getTime().getTime().equals(d);
        assert a.getEventId().equals("asdf123");

        Alert b = new Alert("id2", "1583471755081");
        assert a.getTime().equals(b.getTime());
    }

    static void testAlertCollection() throws Exception {
        Event e = new Event("test", "Go Shopping",
                new GregorianCalendar(2020, Calendar.MARCH, 6, 11, 0),
                new GregorianCalendar(2020, Calendar.MARCH, 6, 12, 0));
        DataSaver ds = new DataSaver("tests");
        AlertCollection ac = new AlertCollection(e, ds);
        ac.addAlert(new GregorianCalendar(2020, Calendar.MARCH, 6, 10, 0));
        ac.addAlert(new GregorianCalendar(2020, Calendar.FEBRUARY, 1), Duration.ofDays(1));
        List<Alert> get = ac.getAlerts(new GregorianCalendar(2020, Calendar.FEBRUARY, 28),
                new GregorianCalendar(2020, Calendar.MARCH, 6, 11, 0));
        assert get.size() == 9;
    }

    public static void main(String[] args) throws Exception {
        testAlert();
        testAlertCollection();
    }

}
