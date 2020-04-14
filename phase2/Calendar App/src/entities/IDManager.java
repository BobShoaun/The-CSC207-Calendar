package entities;

import exceptions.InvalidDateException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;

public class IDManager {
    public static String generateEventId(String eventName, GregorianCalendar eventStartTime)  {
        String id = eventStartTime.getTime().toString() + "%" + eventName;
        id = id.replaceAll(" ", "%");
        id = id.replaceAll(":", "%");
        return id;
    }

    public static GregorianCalendar parseEventId(String eventId) {
        GregorianCalendar eventTime = new GregorianCalendar();
        StringBuilder time = new StringBuilder();
        try {
            String[] times = eventId.split("%");
            String[] times2 = new String[times.length - 1];
            System.arraycopy(times, 1, times2, 0, times.length - 1);
            for (String s : times2) {
                time.append(s).append(" ");
            }
        } catch (StringIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd kk mm ss z yyyy", Locale.ENGLISH);
        try {
            eventTime.setTime(df.parse(time.toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return eventTime;
    }
}
