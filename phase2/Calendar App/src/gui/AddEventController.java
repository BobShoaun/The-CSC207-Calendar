package gui;

import event.Event;
import event.EventCollection;
import event.IDManager;
import event.Series;
import exceptions.InvalidDateException;
import exceptions.InvalidTimeInputException;
import exceptions.NoSuchSeriesException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import memotag.Memo;
import memotag.Tag;
import user.Calendar;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

/**
 * GUI controller for adding new events.
 */
public class AddEventController extends GraphicalUserInterface implements Initializable {

    @FXML
    protected TextField nameField;
    @FXML
    protected DatePicker startDate;
    @FXML
    protected TextField startTime;
    @FXML
    protected DatePicker endDate;
    @FXML
    protected TextField endTime;
    @FXML
    protected TextField tagsField;
    @FXML
    protected TextField memosField;
    @FXML
    protected TextArea memoTextArea;
    @FXML
    protected TextField seriesField;
    @FXML
    protected Label seriesErrorLabel;
    @FXML
    protected Label dateTimeErrorLabel;

    protected Calendar calendar;
    protected EventCollection eventCollection;

    protected String name;
    protected String seriesName;
    protected String memoTitle;
    protected String memoContent;
    protected String[] tags;
    protected GregorianCalendar start;
    protected GregorianCalendar end;
    protected CalendarController calendarUIController;

    /**
     * Set the Calendar to which the Events belong.
     *
     * @param calendar Calendar to set
     */
    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    /**
     * Set the CalendarController controller class (for updating ListViews)
     *
     * @param c CalendarController controller
     */
    public void setCalendarUIController(CalendarController c) {
        this.calendarUIController = c;
    }

    /**
     * Display the details of the event.
     *
     * @param event Event for which
     */
    public void showEventDetails(Event event) {
        nameField.setText(event.getName());
        EventCollection collection = calendar.getEventCollection(event);
        if (collection instanceof Series) {
            seriesField.setText(((Series) collection).getName());
        } else {
            seriesField.setText("Default");
        }
        if (!event.isPostponed()) {
            startDate.setValue(gregorianCalendarToLocalDate(event.getStartDate()));
            endDate.setValue(gregorianCalendarToLocalDate(event.getEndDate()));
            startTime.setText(getTime(event.getStartDate()));
            endTime.setText(getTime(event.getEndDate()));
        }

        memotag.Memo memo = calendar.getMemo(event);
        List<Tag> tags = calendar.getTags(event);
        if (memo != null) {
            memosField.setText(memo.getTitle());
            memoTextArea.setText(memo.getText());
        }
        List<String> tagsText = new ArrayList<>();
        for (Tag t : tags) {
            tagsText.add(t.getText());
        }
        tagsField.setText(String.join(",", tagsText));
    }

    /**
     * Saves the event to Calendar according to user input
     */
    @FXML
    private void handleDone() {
        setLabelInvisible();
        try {
            getUserInput();
            Event newEvent = createEvent(name, start, end);
            if (seriesName.equals("") || seriesName.equals("Default")) {
                //Create event
                if (newEvent != null) {
                    addEvent(newEvent);
                    addTags(newEvent.getId());
                    editMemo(newEvent, "");
                }
            } else {
                //Add event into existing series
                Series s = calendar.getSeries(seriesName);
                s.addEvent(newEvent);
            }
            save();
            closeGUI();
        } catch (InvalidDateException e) {
            dateTimeErrorLabel.setText("Invalid Date");
            dateTimeErrorLabel.setVisible(true);
        } catch (NoSuchSeriesException e) {
            try {
                //Create a new series
                System.out.println("here");
                getUserInput();
                Event newEvent = createEvent(name, start, end);
                calendar.addEventSeries(newEvent, seriesName);
                closeGUI();
                save();
            } catch (InvalidDateException invalidDateException) {
                dateTimeErrorLabel.setText("Invalid Date");
                dateTimeErrorLabel.setVisible(true);
            }
        }
    }


    @FXML
    private void handleRepeatEvent() {
        try {
            getUserInput();
            setLabelInvisible();
            if(seriesName.equals("")||seriesName.equals("Default")){
                seriesName = name;
            }
            try {
                calendar.getSeries(seriesName);
            } catch (NoSuchSeriesException e) {
                calendar.addEventSeries(seriesName, null, null, null, null);
            }
            RepeatingEventController controller = showGUI("repeatingEvent.fxml");
            Event newEvent = createEvent(name, start, end);
            controller.setDetails(newEvent, calendar,seriesName);
            save();

        } catch (InvalidDateException e) {
            dateTimeErrorLabel.setText("Invalid Date");
            dateTimeErrorLabel.setVisible(true);
        }
    }


    /**
     * Set fields to the values in the GUI input.
     *
     * @throws InvalidDateException if user has entered bad dates.
     */
    protected void getUserInput() throws InvalidDateException {
        name = nameField.getText();
        memoTitle = memosField.getText();
        memoContent = memoTextArea.getText();
        seriesName = seriesField.getText();
        tags = tagsField.getText().split(",");
        for (int i = 0; i < tags.length; i++) {
            tags[i] = tags[i].trim();
        }
        if (startDate.getValue() == null || endDate.getValue() == null) {
            throw new InvalidDateException();
        }
        start = GregorianCalendar.from(startDate.getValue().atStartOfDay(ZoneId.systemDefault()));
        end = GregorianCalendar.from(endDate.getValue().atStartOfDay(ZoneId.systemDefault()));
        try {
            setTime(start, end);
        } catch (InvalidTimeInputException e) {
            dateTimeErrorLabel.setText("Invalid Time");
            dateTimeErrorLabel.setVisible(true);
        }
    }

    /**
     * Create the Event and its related info
     *
     * @param name  of the event
     * @param start start time of the event
     * @param end   end time of the event
     */
    protected Event createEvent(String name, GregorianCalendar start, GregorianCalendar end) {
        try {
            getUserInput();
            String id = IDManager.generateEventId(name, start);
            setTime(start, end);
            Event newEvent = new Event(name, start, end);
            return newEvent;
        } catch (InvalidDateException ex) {
            dateTimeErrorLabel.setText("Invalid Date");
            dateTimeErrorLabel.setVisible(true);

        } catch (InvalidTimeInputException ex) {
            dateTimeErrorLabel.setText("Invalid Time");
            dateTimeErrorLabel.setVisible(true);
        }
        return null;
    }

    /**
     * Add the new event to Calendar
     *
     * @param newEvent be to added
     */
    private void addEvent(Event newEvent) {
        try {
            isEventCollectionChanged(eventCollection);
            eventCollection.addEvent(newEvent);
        } catch (NoSuchSeriesException e) {
            seriesErrorLabel.setVisible(true);
        }
    }

    /**
     * Edit the memo for this Event.
     *
     * @param event        Event to edit the Memo for
     * @param oldMemoTitle The title of the original memo
     */
    protected void editMemo(Event event, String oldMemoTitle) {
        String newMemoTitle = memosField.getText();
        if (calendar.getMemo(newMemoTitle) != null) { //The changed memo already exists, so we move the event to the new one
            if (newMemoTitle.equals("")) {
                if (calendar.getMemo(oldMemoTitle) != null)
                    calendar.getMemo(oldMemoTitle).removeEvent(event);
                return;
            } else if (calendar.getMemo(newMemoTitle) != null) { //The changed memo already exists, so we move the event to the new one
                calendar.getMemo(newMemoTitle).addEvent(event);
                if (!memoTextArea.getText().equals("")) { //We only change the memo text if it does not exist before
                    calendar.getMemo(newMemoTitle).setText(memoTextArea.getText());
                }
                if (calendar.getMemo(oldMemoTitle) != null) {
                    calendar.getMemo(oldMemoTitle).removeEvent(event);
                }
            } else {
                if (calendar.getMemo(oldMemoTitle) != null) {  //The new memo does not already exist so we change the old one
                    calendar.getMemo(oldMemoTitle).setTitle(newMemoTitle);
                    calendar.getMemo(newMemoTitle).setText(memoTextArea.getText());
                } else { //Or we create a new one
                    calendar.addMemo(new Memo(newMemoTitle, memoTextArea.getText()));
                    calendar.getMemo(newMemoTitle).addEvent(event);
                }
            }
        }
        else{
            if(!newMemoTitle.equals("")){
                calendar.addMemo(new Memo(newMemoTitle, memoTextArea.getText()));
                calendar.getMemo(newMemoTitle).addEvent(event);
            }
        }
    }

    //TODO: use tag UI instead

    /**
     * add the event to the tags
     *
     * @param id of the events
     */
    protected void addTags(String id) {
        if (!tagsField.getText().equals("")) {
            for (String text : tags) {
                Tag tag = calendar.getTag(text);
                if (tag == null) {
                    tag = new Tag(text);
                    calendar.addTag(tag);
                }
                eventCollection.addTag(id, tag);
            }
        }
    }


    /**
     * get the time from text field
     *
     * @param input string representation of input time
     * @return a list of 2 int (Hour and minute)
     * @throws InvalidTimeInputException Invalid time input from user
     */
    private List<Integer> getTime(String input) throws InvalidTimeInputException {
        List<Integer> ret = new ArrayList<>();
        Scanner sc = new Scanner(input).useDelimiter(":");
        while (sc.hasNextInt()) {
            ret.add(sc.nextInt());
        }
        if (ret.size() != 2 || ret.get(0) < 0 || 23 < ret.get(0) || ret.get(1) < 0 || 59 < ret.get(1)) {
            throw new InvalidTimeInputException();
        }
        return ret;
    }

    /**
     * add the hour and minute to start and end
     *
     * @param start time of the event
     * @param end   time of the event
     * @throws InvalidTimeInputException Invalid time input from user
     */
    private void setTime(GregorianCalendar start, GregorianCalendar end) throws InvalidTimeInputException {
        start.set(java.util.Calendar.HOUR_OF_DAY, getTime(startTime.getText()).get(0));
        start.set(java.util.Calendar.MINUTE, getTime(startTime.getText()).get(1));
        end.set(java.util.Calendar.HOUR_OF_DAY, getTime(endTime.getText()).get(0));
        end.set(java.util.Calendar.MINUTE, getTime(endTime.getText()).get(1));
    }

    private LocalDate gregorianCalendarToLocalDate(GregorianCalendar GC) {
        return LocalDate.of(GC.get(GregorianCalendar.YEAR), GC.get(GregorianCalendar.MONTH) + 1, GC.get(GregorianCalendar.DATE));
    }

    private String getTime(GregorianCalendar GC) {
        String pattern = "HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(GC.getTime());
    }

    /**
     * Returns True iff the event collection has changed.
     *
     * @param oldEC Old Event Collection to monitor
     * @return True iff the event collection has changed.
     * @throws NoSuchSeriesException if the Series does not exist
     */
    protected boolean isEventCollectionChanged(EventCollection oldEC) throws NoSuchSeriesException {

        String seriesName = seriesField.getText();
        if (seriesName.equals("") || seriesName.equals("Default")) {
            eventCollection = calendar.getManualEventCollection();
        } else {
            eventCollection = calendar.getSeries(seriesName);
        }
        if (oldEC == null) {
            oldEC = calendar.getManualEventCollection();
        }
        return !oldEC.equals(eventCollection);
    }

    /**
     * Initialize the GUI to have an invisible label.
     *
     * @param location  URL
     * @param resources ResourceBundle
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setLabelInvisible();
    }

    /**
     * Set the error labels to be invisible.
     */
    protected void setLabelInvisible() {
        seriesErrorLabel.setVisible(false);
        dateTimeErrorLabel.setVisible(false);
    }

    /**
     * Save the Event to file.
     */

    protected void save() {
        calendar.getDataSaver().saveCalendar(calendar);
        calendarUIController.updateDisplays();
    }
}
