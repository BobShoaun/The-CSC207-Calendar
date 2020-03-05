import java.util.*;
import java.util.Calendar;

public class EventGenerator {
    private Event baseEvent;
    private Date start;
    private Date end;
    private Date frequency;

    public EventGenerator(Event baseEvent, Date start, Date end, Date frequency) {
        this.baseEvent = baseEvent;
        this.start = start;
        this.end = end;
        this.frequency = frequency;
    }

    public List<Event> generateEvents()
    {
        String eventName = baseEvent.getName();
        List<Event> ret = new ArrayList<>();
        Date curr = this.start;
        while(curr.compareTo(this.end)<=0){
            Date currNext = addTime(curr, frequency);
            Event e = new Event("", eventName, dateToGC(curr), dateToGC(currNext));
            ret.add(e);
            curr=currNext;
        }
        return ret;
    }

    private Date addTime(Date begin, Date time)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(begin);
        //Adding the time to offset each event
        c.add(Calendar.MILLISECOND, 1);
        return c.getTime();
    }
    private GregorianCalendar dateToGC(Date date) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return calendar;
    }
}
