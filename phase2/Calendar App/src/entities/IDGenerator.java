package entities;

import java.util.GregorianCalendar;

public class IDGenerator {
    public static String generateEventId(String eventName, GregorianCalendar eventStartTime) {
        String id = eventStartTime.getTime().toString() + "%" + eventName;
        id = id.replaceAll(" ", "%");
        id = id.replaceAll(":", "%");
        return id;
    }
}
