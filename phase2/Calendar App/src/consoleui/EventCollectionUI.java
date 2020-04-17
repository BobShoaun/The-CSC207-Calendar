package consoleui;

import event.Event;
import event.EventCollection;
import event.Series;
import exceptions.InvalidDateException;
import user.Calendar;

import java.time.Duration;
import java.util.GregorianCalendar;

/**
 * Terminal interface for EventCollections
 */
public class EventCollectionUI extends UserInterface {
    private EventCollection events;
    private user.Calendar cal;

    public EventCollectionUI(EventCollection eventCollection, Calendar cal) {
        this.events = eventCollection;
        this.cal = cal;
    }

    /**
     * Display the contents of this EventCollection.
     */
    @Override
    public void display() {
        System.out.println(this.events);
    }

    /**
     * Start the UI with menu options.
     */
    @Override
    public void show() {
        display();
        int option = getOptionsInput(new String[]{"Exit",
                "Display in Time frame",
                "Add events to this series manually",
                "Create an repeating series "});
//                "Edit an Event in Series",});
        switch (option) {
            case 0:
                return;
            case 1:
                displayTimeFrame();
                break;
            case 2:
                if (this.events instanceof Series) {
//                String name = getStringInput("name of the series:");
                    EventCollection regularEvents = cal.getEventCollection("");

                    int option1 = getOptionsInput(regularEvents.getEventOptions());
                    String eventId = regularEvents.getEvents().get(option1 - 1).getId();

                    while (true) {
                        //keep asking for events to select till exit at 0
//                            cal.addToSeries(eventId, this.events.getName());
                        option1 = getOptionsInput(regularEvents.getEventOptions());
                        if (option1 == 0) {
                            break;
                        }
                        eventId = regularEvents.getEvents().get(option1 - 1).getId();
                    }
                    //get ECollections find the regular one, display it for choice and add that using addToSeries to a given Series name...
                    break;
                }
            case 3:
                createRepeatingSeries();
                break;
        }
    }

    private void createRepeatingSeries() {
        String seriesName = getStringInput("Series Name: ");
        String eventName = getStringInput("Base Event Name: ");
        GregorianCalendar start1 = getDateInput("Event Start Date: ");
        GregorianCalendar end1 = getDateInput("Event End Date: ");
        Duration next = getDurationInput("How frequent is the repetition? ");
        GregorianCalendar endSeries = getDateInput("The end date for this series: ");
        try {
            Event base = new Event(eventName, start1, end1);
            cal.addEventSeries(seriesName, start1, endSeries, next, base);
        } catch (InvalidDateException e) {
            e.printStackTrace();
        }
    }

    private void displayTimeFrame() {
        GregorianCalendar start = getDateInput("Select a Start Time frame");
        GregorianCalendar end = getDateInput("Select a End Time Frame");
        for (Event e : events.getEvents(start, end)) {
            System.out.println(e.toString());
        }
    }
}
