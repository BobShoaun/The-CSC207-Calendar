package gui;

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
import java.time.ZoneId;
import java.util.*;

/**
 * GUI controller for adding new events.
 */
public class EventAddUI extends GraphicalUserInterface implements Initializable {

    @FXML protected TextField nameField;
    @FXML protected DatePicker startDate;
    @FXML protected TextField startTime;
    @FXML protected DatePicker endDate;
    @FXML protected TextField endTime;
    @FXML protected TextField tagsField;
    @FXML protected TextField memosField;
    @FXML protected TextArea memoTextArea;
    @FXML protected TextField seriesField;
    @FXML protected Label seriesErrorLabel;
    @FXML protected Label dateTimeErrorLabel;
    @FXML protected Button doneButton;

    protected Calendar calendar;
    protected EventCollection currEvents;

    protected String name;
    protected String memoTitle;
    protected String memoContent;
    protected String[] tags;
    protected GregorianCalendar start;
    protected GregorianCalendar end;

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }


    /**
     * Saves the event to Calendar according to user input
     */
    @FXML
    private void handleDone()  {
        System.out.println("Done clicked");
        setLabelInvisible();
        try {
            getUserInput();
            createEvent(name, start, end, tags, memoTitle, memoContent);
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
        if(startDate.getValue()== null|| endDate.getValue()==null){
            throw new InvalidDateException();
        }
        start = GregorianCalendar.from(startDate.getValue().atStartOfDay(ZoneId.systemDefault()));
        end = GregorianCalendar.from(endDate.getValue().atStartOfDay(ZoneId.systemDefault()));

    }


    /**
     * Create the Event and its related info
     *
     * @param name        of the event
     * @param start       start time of the event
     * @param end         end time of the event
     * @param tags        that are associated with this event
     * @param memoTitle   the title of event memo
     * @param memoContent the content of event memo
     */
    protected void createEvent(String name, GregorianCalendar start, GregorianCalendar end, String[] tags, String memoTitle, String memoContent) {
        try {
            getUserInput();
            String id = IDManager.generateEventId(name, start);
            setTime(start, end);
            entities.Event newEvent = new entities.Event(name, start, end);
            addEvent(newEvent);
            if(!memoTitle.equals("")){addMemo(memoTitle, memoContent, id);}
            if(tags.length>1|| !tags[0].equals("")){addTags(tags, id);}
            closeGUI();
            System.out.println("Event created:" + newEvent);
        } catch (InvalidDateException ex) {
            dateTimeErrorLabel.setText("Invalid Date");
            dateTimeErrorLabel.setVisible(true);
        } catch (InvalidTimeInputException ex) {
            dateTimeErrorLabel.setText("Invalid Time");
            dateTimeErrorLabel.setVisible(true);
        }
    }

    /**
     * Add the new event to Calendar
     *
     * @param newEvent be to added
     */
    private void addEvent(entities.Event newEvent) {
        String SeriesName = seriesField.getText();

        if (SeriesName.equals("") || SeriesName.equals("Default")) {
            currEvents = calendar.getSingleEventCollection();
        } else {
            try {
                currEvents = calendar.getSeries(SeriesName);
            } catch (NoSuchSeriesException ex) {
                seriesErrorLabel.setVisible(true);
            }
        }
        currEvents.addEvent(newEvent);
    }

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
        currEvents.addMemo(id, memo);
    }

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
            currEvents.addTag(id, tag);
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setLabelInvisible();
    }

    protected void setLabelInvisible() {
        seriesErrorLabel.setVisible(false);
        dateTimeErrorLabel.setVisible(false);
    }
}
