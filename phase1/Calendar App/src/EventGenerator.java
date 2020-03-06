import exceptions.InvalidDateException;

import java.security.InvalidParameterException;
import java.time.Duration;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.Calendar;

public class EventGenerator {
    private Event baseEvent;
    private CalendarGenerator calGen;
    private Duration dur;

    /**
     * EventGenerator for finite repeating events events
     * @param baseEvent event to be repeated
     * @param start start of repeating events
     * @param end end of repeating events
     * @param frequency time between occurrence of repeating events
     */
    public EventGenerator(Event baseEvent, Date start, Date end, Date frequency) throws InvalidDateException
    {
        this.baseEvent = baseEvent;
        List<Duration> durList = new ArrayList<>();
        Duration dur = Duration.between((Temporal)frequency, (Temporal)start);
        Date curr = start;
        while(curr.compareTo(end)<=0){
            Date nextDate = (Date)dur.addTo((Temporal)curr);
            durList.add(dur);
            curr=nextDate;
        }
        this.calGen = new CalendarGenerator(dateToGC(start), durList, dateToGC(end));
    }


//    public List<Event> generateInfiniteEvents() throws InvalidDateException
//    {// baseEvents and end is null
//            List<Event> ret = new ArrayList<>();
//                //gives the first 15 sets of event repetition
//                for (Event e:baseEvents)
//                {
//                    ret.addAll(getRepeatingEvents(e));
//                }
//            return ret;
//    }

    public List<Event> generateFiniteEvents() throws InvalidDateException
    {
        List<Event> ret = new ArrayList<>();

        for (GregorianCalendar GC : calGen) {
            String id = baseEvent.getName()+GC.getTime();
            String name = baseEvent.getName();
            GregorianCalendar curr = (GregorianCalendar) GC.clone();
            GC.add(Calendar.MILLISECOND,(int)this.dur.toMillis());
            Event e = new Event(id, name, curr, GC);
        }
        return null;
    }


//    private Event getNextEvent(Date curr) throws InvalidDateException
//    {
//        String eventName = baseEvent.getName();
//        String id = eventName+curr;
//        Date currNext = addTime(curr, frequency);
//        return new Event(id, eventName, dateToGC(curr), dateToGC(currNext));
//    }

//    private List<Event> getRepeatingEvents(Event e) throws InvalidDateException
//    {
//        List<Event> ret = new ArrayList<>();
//        ret.add(e);
//        Date curr = e.getStartDate().getTime();
//        for (int i = 0; i <15 ; i++) {
//            Event nextEvent = getNextEvent(curr);
//            ret.add(nextEvent);
//            curr=nextEvent.getEndDate().getTime();
//        }
//        return ret;
//    }
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
