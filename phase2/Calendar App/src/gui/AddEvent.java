package gui;

import entities.EventCollection;
import entities.IDGenerator;
import exceptions.InvalidDateException;
import exceptions.InvalidTimeInputException;
import exceptions.NoSuchSeriesException;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import mt.Memo;
import mt.Tag;
import user.Calendar;

import java.net.URL;
import java.time.ZoneId;
import java.util.*;

public class AddEvent extends GraphicalUserInterface implements Initializable {

    @FXML
    private TextField nameField;
    @FXML
    private DatePicker startDate;
    @FXML
    private TextField startTime;
    @FXML
    private DatePicker endDate;
    @FXML
    private TextField endTime;
    @FXML
    private TextField tagsField;
    @FXML
    private TextField memosField;
    @FXML
    private TextArea memoTextArea;
    @FXML
    private TextField seriesField;
    @FXML
    private Label seriesErrorLabel;

    public Button doneButton; // done think we need this?

    private Calendar calendar;
    private EventCollection currEvents;

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    @FXML
    private void handleDone(Event e) throws InvalidDateException {
        System.out.println("Done clicked");
        seriesErrorLabel.setVisible(false);
        String name = nameField.getText();
        String memoTitle = memosField.getText();
        String memoContent = memoTextArea.getText();
        String[] tags = tagsField.getText().split(",");
        GregorianCalendar start = GregorianCalendar.from(startDate.getValue().atStartOfDay(ZoneId.systemDefault()));
        GregorianCalendar end = GregorianCalendar.from(endDate.getValue().atStartOfDay(ZoneId.systemDefault()));

        String id = IDGenerator.generateEventId(name, start);
        try {
            setTime(start, end);
            entities.Event newEvent = new entities.Event(name, start, end);
            addEvent(newEvent);
            addMemo(memoTitle, memoContent, id);
            addTags(tags, id);
            closeGUI();
            System.out.println("Event created:" + newEvent);
        } catch (InvalidDateException ex) {
            System.out.println("Invalid date input");
            //TODO: implement UI warning
        } catch (InvalidTimeInputException ex) {
            //TODO: add ErrorLabel
            System.out.println("Incorrect Time input");
        }
    }

    private void addEvent(entities.Event newEvent) {
        String SeriesName = seriesField.getText();

        if (SeriesName.equals("")) {
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

    private void addMemo(String memoTitle, String memoContent, String id) {
        Memo memo = calendar.getMemo(memoTitle, memoContent);
        if (memo == null) {
            calendar.addMemo(memoTitle, memoContent);
            memo = calendar.getMemo(memoTitle, memoContent);
        }
        currEvents.addMemo(id, memo);
    }

    private void addTags(String[] tags, String id) {
        for (String text : tags) {
            Tag tag = calendar.getTag(text);
            if (tag == null) {
                calendar.addTag(text);
                tag = calendar.getTag(text);
            }
            currEvents.addTag(id, tag);
        }
    }

    private List<Integer> getTime(String input) throws InvalidTimeInputException {
        List<Integer> ret = new ArrayList<>();
        Scanner sc = new Scanner(input).useDelimiter(":");
        while (sc.hasNextInt()) {
            ret.add(sc.nextInt());
        }
        if (ret.size() != 2||ret.get(0)<0|| 23<ret.get(0)||ret.get(1)<0||59<ret.get(1)) {
            throw new InvalidTimeInputException();
        }
        return ret;
    }

    private void setTime(GregorianCalendar start, GregorianCalendar end) throws InvalidTimeInputException {
        start.set(java.util.Calendar.HOUR, getTime(startTime.getText()).get(0));
        start.set(java.util.Calendar.MINUTE, getTime(startTime.getText()).get(1));
        end.set(java.util.Calendar.HOUR, getTime(endTime.getText()).get(0));
        end.set(java.util.Calendar.MINUTE, getTime(endTime.getText()).get(1));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        seriesErrorLabel.setVisible(false);
    }

}
