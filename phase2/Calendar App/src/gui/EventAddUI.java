package gui;

import entities.Event;
import entities.EventCollection;
import entities.IDManager;
import exceptions.InvalidDateException;
import exceptions.InvalidTimeInputException;
import exceptions.NoSuchSeriesException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import mt.Memo;
import mt.Tag;
import user.Calendar;

import java.net.URL;
import java.text.SimpleDateFormat;
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
    @FXML
    protected Button doneButton;

    protected Calendar calendar;
    protected EventCollection eventCollection;

    protected String name;
    protected String memoTitle;
    protected String memoContent;
    protected String[] tags;
    protected GregorianCalendar start;
    protected GregorianCalendar end;
    protected gui.Calendar calendarController;

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    protected void setCalendarController(gui.Calendar c) {
        this.calendarController = c;
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
                closeGUI();
                calendarController.updateDisplayedEvents();
                save();
            }
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
        if (startDate.getValue() == null || endDate.getValue() == null) {
            throw new InvalidDateException();
        }
        start = GregorianCalendar.from(startDate.getValue().atStartOfDay(ZoneId.systemDefault()));
        end = GregorianCalendar.from(endDate.getValue().atStartOfDay(ZoneId.systemDefault()));

    }


    /**
     * Create the Event and its related info
     *
     * @param name  of the event
     * @param start start time of the event
     * @param end   end time of the event
     *              //     * @param tags        that are associated with this event
     *              //     * @param memoTitle   the title of event memo
     *              //     * @param memoContent the content of event memo
     */
//    protected Event createEvent(String name, GregorianCalendar start, GregorianCalendar end, String[] tags, String memoTitle, String memoContent) {
    protected Event createEvent(String name, GregorianCalendar start, GregorianCalendar end) {
        try {
            String id = IDManager.generateEventId(name, start);
            setTime(start, end);
            entities.Event newEvent = new entities.Event(name, start, end);
            System.out.println("Event created:" + newEvent);
            return newEvent;
//            if(!memoTitle.equals("")){addMemo(memoTitle, memoContent, id);}
//            if(tags.length>1|| !tags[0].equals("")){addTags(tags, id);}
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
    private void addEvent(entities.Event newEvent) {
        getEventCollection();
        eventCollection.addEvent(newEvent);
    }

    public void getEventCollection() {
        String SeriesName = seriesField.getText();
        if (SeriesName.equals("") || SeriesName.equals("Default")) {
            eventCollection = calendar.getSingleEventCollection();
        } else {
            try {
                eventCollection = calendar.getSeries(SeriesName);
            } catch (NoSuchSeriesException ex) {
                seriesErrorLabel.setVisible(true);
            }
        }
    }
    //TODO: use memoUI instead

    /**
     * add the memo to the event with this id
     *
     * @param memoTitle   the title of event memo
     * @param memoContent the content of event memo
     * @param id          of the event
     */
    protected void addMemo(String memoTitle, String memoContent, String id) {
        Memo memo = calendar.getMemo(memoTitle, memoContent);
        if (memo == null) {
            calendar.addMemo(memoTitle, memoContent);
            memo = calendar.getMemo(memoTitle, memoContent);
        }
        eventCollection.addMemo(id, memo);
    }

    //TODO: use tag UI instead

    /**
     * add the event to the tags
     *
     * @param tags collection of tags that event belongs to
     * @param id   of the event
     */
    protected void addTags(String[] tags, String id) {
        for (String text : tags) {
            Tag tag = calendar.getTag(text);
            if (tag == null) {
                calendar.addTag(text);
                tag = calendar.getTag(text);
            }
            eventCollection.addTag(id, tag);
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
        start.set(java.util.Calendar.HOUR, getTime(startTime.getText()).get(0));
        start.set(java.util.Calendar.MINUTE, getTime(startTime.getText()).get(1));
        end.set(java.util.Calendar.HOUR, getTime(endTime.getText()).get(0));
        end.set(java.util.Calendar.MINUTE, getTime(endTime.getText()).get(1));
    }

    public void showEventDetails(Event event) {
        nameField.setText(event.getName());
        startDate.setValue(GregorianCalendarToLocalDate(event.getStartDate()));
        endDate.setValue(GregorianCalendarToLocalDate(event.getEndDate()));
        if(!event.isPostponed()){
            startTime.setText(getTime(event.getStartDate()));
            endTime.setText(getTime(event.getEndDate()));
        }
        List<mt.Memo> memos = calendar.getMemos(event.getId());
        List<mt.Tag> tags = calendar.getTags(event.getId());
        for (mt.Memo m : memos) {
            memosField.setText(m.getTitle());
            memoTextArea.setText(m.getText());
        }
//        oldMemoTitle = memosField.getText();
        List<String> tagsText = new ArrayList<>();
        for (mt.Tag t : tags) {
            tagsText.add(t.getText());
        }
        tagsField.setText(String.join(",", tagsText));
//        oldTags = tagsField.getText().split(",");
    }

    private LocalDate GregorianCalendarToLocalDate(GregorianCalendar GC) {
        Date date = GC.getTime();
        return LocalDate.of(date.getYear()+1900, date.getMonth(), date.getDay());
    }

    private String getTime(GregorianCalendar GC) {
        String pattern = "HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(GC.getTime());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setLabelInvisible();
    }

    protected void setLabelInvisible() {
        seriesErrorLabel.setVisible(false);
        dateTimeErrorLabel.setVisible(false);
    }

    protected void save(){
        calendar.getDataSaver().saveCalendar(calendar);
    }
}
