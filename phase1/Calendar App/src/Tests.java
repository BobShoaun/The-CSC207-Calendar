import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Tests {

    static void testAlert() {
        Alert a = new Alert("asdf123", new GregorianCalendar());
        Date d = new Date(2020, Calendar.APRIL, 6, 12, 30, 30);
        assert a.getTime().getTime().equals(d);
        assert a.getEventId().equals("asdf123");

        Alert b = new Alert("id2", "1583471755081");
        assert a.getTime().equals(b.getTime());
    }

    public static void main(String[] args) {
        testAlert();
    }

}
