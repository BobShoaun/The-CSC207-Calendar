package gui;

import entities.EventCollection;
import entities.IDGenerator;
import exceptions.InvalidDateException;
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
import java.util.GregorianCalendar;
import java.util.ResourceBundle;

public class AddEvent extends GraphicalUserInterface implements Initializable {

    public Button doneButton;
    public TextArea memoTextArea;
    public Label seriesErrorLabel;
    @FXML
    private TextField nameField;
    @FXML
    private DatePicker startTime;
    @FXML
    private DatePicker endTime;
    @FXML
    private TextField seriesField;
    @FXML
    private TextField tagsField;
    @FXML
    private TextField memosField;

    private Calendar calendar;
    private EventCollection currEvents;

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    @FXML
    private void handleDone(Event e) throws InvalidDateException {
        seriesErrorLabel.setVisible(false);
        String name = nameField.getText();
        GregorianCalendar start = GregorianCalendar.from(startTime.getValue().atStartOfDay(ZoneId.systemDefault()));
        GregorianCalendar end = GregorianCalendar.from(endTime.getValue().atStartOfDay(ZoneId.systemDefault()));
        //TODO: implement time in start and end after UI support
        String memoTitle = tagsField.getText();
        String memoContent = memoTextArea.getText();
        String[] tags = memosField.getText().split(",");
        String id = IDGenerator.generateEventId(name, start);
        entities.Event newEvent = new entities.Event(name, start, end);
        addEvent(newEvent);
        addMemo(memoTitle, memoContent, id);
        addTags(tags, id);
    }
    private void addEvent(entities.Event newEvent){
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

    private void addMemo(String memoTitle,String memoContent, String id){
        Memo memo = calendar.getMemo(memoTitle, memoContent);
        if (memo != null) {
            currEvents.addMemo(id, memo);
        } else {
            calendar.addMemo(memoTitle, memoContent);
        }
    }

    private void addTags(String[] tags, String id) {
        for (String text : tags) {
            Tag tag = calendar.getTag(text);
            if (tag != null) {
                currEvents.addTag(id, tag);
            } else {
                calendar.addTag(text);
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        seriesErrorLabel.setVisible(false);
    }

}
