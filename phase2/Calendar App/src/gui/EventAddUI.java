package gui;

import dates.CalendarGenerator;
import event.Event;
import event.EventCollection;
import event.IDManager;
import event.Series;
import exceptions.InvalidDateException;
import exceptions.InvalidTimeInputException;
import exceptions.NoSuchSeriesException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import memotag.Memo;
import memotag.Tag;
import user.Calendar;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

/**
 * GUI controller for adding new events.
 */
public class EventAddUI extends GraphicalUserInterface implements Initializable {

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
    protected String memoTitle;
    protected String memoContent;
    protected String[] tags;
    protected GregorianCalendar start;
    protected GregorianCalendar end;
    protected CalendarUI calendarUIController;

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public void setCalendarUIController(CalendarUI c) {
        this.calendarUIController = c;
    }

    public void showEventDetails(Event event) {
        nameField.setText(event.getName());
        EventCollection collection = calendar.getEventCollection(event);
        if (collection instanceof Series){
            seriesField.setText(((Series) collection).getName());
        }
        else {
            seriesField.setText("Default");
        }
        if (!event.isPostponed()) {
            startDate.setValue(gregorianCalendarToLocalDate(event.getStartDate()));
            endDate.setValue(gregorianCalendarToLocalDate(event.getEndDate()));
            startTime.setText(getTime(event.getStartDate()));
            endTime.setText(getTime(event.getEndDate()));
        }
        System.out.println(event.getStartDate().getTime());
        System.out.println(event.getEndDate().getTime());


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
        System.out.println("Done clicked");
        setLabelInvisible();
        try {
            getUserInput();
            Event newEvent = createEvent(name, start, end);
            if (newEvent != null) {
                addEvent(newEvent);
                addTags(newEvent.getId());
                editMemo(newEvent, "");
                calendarUIController.updateDisplayedEvents();
                calendarUIController.updateDisplayedSeries();
                calendarUIController.updateDisplayedSubSeries();
                save();
                closeGUI();
            }
        } catch (InvalidDateException e) {
            dateTimeErrorLabel.setText("Invalid Date");
            dateTimeErrorLabel.setVisible(true);
        }
    }


    @FXML
    private void handleRepeatEvent() {
        System.out.println("Repeat Series clicked");
        try {
            getUserInput();
            SeriesUI controller = showGUI("SeriesUI.fxml");
            Event newEvent = createEvent(name, start, end);
            controller.setDetails(newEvent, calendar);
        } catch (InvalidDateException e) {
            dateTimeErrorLabel.setText("Invalid Date");
            dateTimeErrorLabel.setVisible(true);
        }
    }

    @FXML
    private void handleCreateSeries(){
        System.out.println("Create new series");
        try {
            getUserInput();
            Event newEvent = createEvent(name,start,end);
            calendar.addEventSeries(newEvent);
            calendarUIController.updateDisplayedEvents();
            calendarUIController.updateDisplayedSeries();
            calendarUIController.updateDisplayedSubSeries();
            closeGUI();
        } catch (InvalidDateException e) {
            dateTimeErrorLabel.setText("Invalid Date");
            dateTimeErrorLabel.setVisible(true);
        }
    }


    protected void getUserInput() throws InvalidDateException {
        name = nameField.getText();
        memoTitle = memosField.getText();
        memoContent = memoTextArea.getText();
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
            System.out.println("Event created:" + newEvent);
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


    protected void editMemo(Event event, String oldMemoTitle) {
        String newMemoTitle = memosField.getText();
        if (calendar.getMemo(newMemoTitle) != null) { //The changed memo already exists, so we move the event to the new one
            if (newMemoTitle.equals("")) {
                if (calendar.getMemo(oldMemoTitle) != null)
                    calendar.getMemo(oldMemoTitle).removeEvent(event);
                return;
            }
            if (calendar.getMemo(newMemoTitle) != null) { //The changed memo already exists, so we move the event to the new one
                calendar.getMemo(newMemoTitle).addEvent(event);
                if(!memoTextArea.getText().equals("")){ //We only change the memo text if it does not exist before
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

    protected boolean isEventCollectionChanged(EventCollection oldEC) throws NoSuchSeriesException {

        String seriesName = seriesField.getText();
        if (seriesName.equals("") || seriesName.equals("Default")) {
            eventCollection = calendar.getSingleEventCollection();
        } else {
            eventCollection = calendar.getSeries(seriesName);
        }
        if(oldEC==null){oldEC=calendar.getSingleEventCollection();}
        return !oldEC.equals(eventCollection);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setLabelInvisible();
    }

    protected void setLabelInvisible() {
        seriesErrorLabel.setVisible(false);
        dateTimeErrorLabel.setVisible(false);
    }

    protected void save() {
        calendar.getDataSaver().saveCalendar(calendar);
    }
}
