import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Terminal interface for Event.
 */

public class EventUI extends UserInterface {

    private Event event;

    public EventUI(Event event) { this.event = event; }

    public Event getEvent() {
        throw new NotImplementedException();
    }

    @Override
    public void display() {
        //System.out.println();
        throw new NotImplementedException();
    }

    @Override
    public void show() {
        throw new NotImplementedException();
    }

}
