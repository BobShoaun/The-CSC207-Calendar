import exceptions.InvalidDateException;

import java.security.InvalidParameterException;
import java.util.*;
import java.util.Calendar;

public class EventGenerator {
    private Event baseEvent;
    private Date start;
    private Date end;
    private Date frequency;
    private CalendarGenerator calGen;
    private List<Event> baseEvents;

    /**
     * EventGenerator for finite repeating events events
     * @param baseEvent event to be repeated
     * @param start start of repeating events
     * @param end end of repeating events
     * @param frequency time between occurrence of repeating events
     */
    public EventGenerator(Event baseEvent, Date start, Date end, Date frequency) {
        this.baseEvent = baseEvent;
        this.start = start;
        this.end = end;
        this.frequency = frequency;
    }

    /**
     * EventGenerator for infinite repeating events events
     * @param baseEvents events to be repeated
     * @param start start of repeating events
     * @param frequency time between occurrence of repeating events
     */
    public EventGenerator(List<Event> baseEvents, Date start, Date frequency) {
        this.baseEvents = baseEvents;
        this.start = start;
        this.frequency = frequency;
    }


    public List<Event> generateInfiniteEvents() throws InvalidDateException
    {// baseEvents and end is null
            List<Event> ret = new ArrayList<>();
            for (int i = 0; i <15 ; i++) {
                //gives the first 15 sets of repetition
                Date curr = this.start;
                for (Event e:baseEvents)
                {
                    Event nextEvent = getNextEvent(curr);
                    ret.add(nextEvent);
                    curr=nextEvent.getEndDate().getTime();
                }
            }
            return ret;
    }

    public List<Event> generateFiniteEvents() throws InvalidDateException
    {
        List<Event> ret = new ArrayList<>();
        Date curr = this.start;
        while(curr.compareTo(this.end)<=0){
            Event nextEvent = getNextEvent(curr);
            ret.add(nextEvent);
            curr=nextEvent.getEndDate().getTime();
        }
        return ret;
    }

    private Event getNextEvent(Date curr) throws InvalidDateException
    {
        String eventName = baseEvent.getName();
        String id = eventName+curr;
        Date currNext = addTime(curr, frequency);
        return new Event(id, eventName, dateToGC(curr), dateToGC(currNext));
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
