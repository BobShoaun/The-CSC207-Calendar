package entities;

import java.util.GregorianCalendar;

public class IDGenerator {
    public String generateEventId(String eventName, GregorianCalendar eventStartTime) {
        String id = eventStartTime.toString() + "%" + eventName;
        return id.replaceAll(" ", "%");
    }
}
