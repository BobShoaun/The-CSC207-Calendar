import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class EventCollection implements Serializable
{
    private ArrayList<Event> events = new ArrayList<>();
    private String name = null;

    public String getName(){return name;}

    public Event getEvent(String id){ throw new NotImplementedException(); };
    public List<Event> getEvents(Date date) {throw new NotImplementedException(); }
    public List<Event> getEvents(Date start, Date end) {throw new NotImplementedException();}
    public Iterator<Event> getEventIterator(Date start) {throw new NotImplementedException();}
    public void addEvent(Event event){throw new NotImplementedException();}
}
