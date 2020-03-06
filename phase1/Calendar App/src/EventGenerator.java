import exceptions.InvalidDateException;
import java.time.Duration;
import java.util.*;

public class EventGenerator {
    private Event baseEvent;
    private CalendarGenerator calGen;

    /**
     * EventGenerator for finite repeating events events
     * @param baseEvent event to be repeated
     * @param start start of repeating events
     * @param end end of repeating events
     * @param Duration time between occurrence of repeating events
     */
    public EventGenerator(Event baseEvent, Date start, Date end, List<Duration> Duration) throws InvalidDateException
    {
        this.baseEvent = baseEvent;
        this.calGen = new CalendarGenerator(dateToGC(start), Duration, dateToGC(end));
    }

    public Event getBaseEvent()
    {
        return baseEvent;
    }

    public void setCalGen(CalendarGenerator calGen)
    {
        this.calGen = calGen;
    }

    public CalendarGenerator getCalGen()
    {
        return calGen;
    }

    /**
     *
     * @return
     * @throws InvalidDateException
     */
    public List<Event> generateEvents() throws InvalidDateException
    {
        List<Event> ret = new ArrayList<>();
        for (GregorianCalendar GC : calGen)
        {
            String id = baseEvent.getName()+baseEvent.getStartDate().getTime();
            Event event = new Event(id, baseEvent.getName(),GC,addTime(GC,baseEvent.getDuration()));
            ret.add(event);
        }
        return ret;
    }

    private GregorianCalendar addTime(GregorianCalendar begin, GregorianCalendar time)
    {
        //Adding the time to offset each event
        GregorianCalendar newGC = new GregorianCalendar();
        long dur = begin.getTimeInMillis()+time.getTimeInMillis();
        Date d2 = new Date(dur);
        newGC.setGregorianChange(d2);
        return newGC;
    }
    private GregorianCalendar dateToGC(Date date) {
        if(date==null){return null;}
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return calendar;
    }
}
