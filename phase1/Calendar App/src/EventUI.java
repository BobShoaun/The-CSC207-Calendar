/**
 * Terminal interface for Event.
 */
public class EventUI extends UserInterface{

    private Event event;

    public EventUI(Event event) {

        this.event = event;
    }

    @Override
    public void display() {

    }

    @Override
    public void show() {

    }

    public Event getEvent(){
        return event;
    }
}
