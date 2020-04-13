package entities;

import java.util.GregorianCalendar;

public class IDGenerator {
    public static String generateEventId(String eventName, GregorianCalendar eventStartTime) {
        String id = eventStartTime.toString() + "%" + eventName;
        return id.replaceAll(" ", "%");
    }
}
