package entities;

import java.util.GregorianCalendar;

public class IDGenerator {
    public static String generateEventId(String eventName, GregorianCalendar eventStartTime) {
        String id = eventStartTime.getTime().toString() + "%" + eventName;
        id = id.replaceAll(" ", "%");
        System.out.println("ID:" + id);
        return id;
    }
}
