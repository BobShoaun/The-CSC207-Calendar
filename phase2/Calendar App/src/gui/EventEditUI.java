package gui;

import entities.Event;
import exceptions.InvalidDateException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ResourceBundle;

/**
 * GUI controller for editing events
 */
public class EventEditUI extends EventAddUI {

    @FXML protected Button shareEventButton;
    @FXML protected Button addAlertButton;
    @FXML protected Button deleteButton;
    @FXML protected Button editButton;

    public Event getEvent() {
        return event;
    }

    @FXML protected Button postponeButton;
    @FXML protected Button duplicateButton;

    private Event event;

    public void setEvent(Event event) {
        this.event = event;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setLabelInvisible();
    }

    public void showEventDetails(){
        nameField.setText(event.getName());
        startDate.setValue(GregorianCalendarToLocalDate(event.getStartDate()));
        endDate.setValue(GregorianCalendarToLocalDate(event.getEndDate()));
        startTime.setText(getTime(event.getStartDate()));
        endTime.setText(getTime(event.getEndDate()));
        List<mt.Memo> memos = calendar.getMemos(event.getId());
        List<mt.Tag> tags = calendar.getTags(event.getId());
        for (mt.Memo m:memos){
            memosField.setText(m.getTitle());
            memoTextArea.setText(m.getText());
        }
        StringBuilder tag = new StringBuilder();
        for (mt.Tag t: tags){
            tag.append(t.getText());
        }
        tagsField.setText(tag.toString());
    }
    private LocalDate GregorianCalendarToLocalDate(GregorianCalendar GC){
        Date date = GC.getTime();
        return  LocalDate.of(date.getYear(), date.getMonth(), date.getDay());
    }

    private String getTime(GregorianCalendar GC){
        String pattern = "HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(GC.getTime());
    }

    public void handleEdit(){
        System.out.println("Done (edit) clicked");
        try {
            getUserInput();
            event.setName(name);
            event.setStartDate(start);
            event.setEndDate(end);
        } catch (InvalidDateException e) {
            e.printStackTrace();
        }

    }
    public void handleDelete(){
        System.out.println("Done delete clicked");
    }
    public void handleAddAlert(){
        System.out.println("Done add clicked");
        openGUI("alert.fxml");
    }
    public void handleShareEvent(){
        System.out.println("Done share clicked");
    }
    public void handlePostpone(){
        System.out.println("Done postpone clicked");
    }
    public void handleDuplicate(){}
}
