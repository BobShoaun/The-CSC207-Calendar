import org.apache.commons.lang3.time.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EventRepetition
{
    private Event baseEvent;
    private Date start;
    private Date end;
    private Date frequency;

    public EventRepetition(Event baseEvent, Date start, Date end, Date frequency)
    {
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
            Event e = new Event("", eventName, curr, currNext);
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
}
