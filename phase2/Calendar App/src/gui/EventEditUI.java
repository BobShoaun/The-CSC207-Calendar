package gui;

import entities.Event;
import entities.EventCollection;
import exceptions.InvalidDateException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import user.DataSaver;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

/**
 * GUI controller for editing events
 */
public class EventEditUI extends EventAddUI {

    @FXML
    protected Button shareEventButton;
    @FXML
    protected Button addAlertButton;
    @FXML
    protected Button deleteButton;
    @FXML
    protected Button editButton;

    @FXML
    protected Button postponeButton;
    @FXML
    protected Button duplicateButton;

    private Event event;
//    private String oldMemoTitle;
//    private String[] oldTags;

    private String username;

    public void setEvent(Event event) {
        this.event = event;
    }

    public void setEventCollection(EventCollection e) {
        this.eventCollection = e;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setLabelInvisible();
    }

    protected void setUsername(String username) {
        this.username = username;
    }


    public void handleEdit() {
        System.out.println("Done (edit) clicked");
        try {
            getUserInput();
            Event editedEvent = createEvent(name, start, end);
            if (event.isPostponed()) {
                eventCollection.rescheduleEvent(event,start,end);
            } else {
                eventCollection.editEvent(event, editedEvent);
            }
//            editMemo();
//            editTags();
        } catch (InvalidDateException e) {
            dateTimeErrorLabel.setText("Invalid Date");
            dateTimeErrorLabel.setVisible(true);
        }

    }

//    private void editMemo(){
//        if (oldMemoTitle.equals("") && !memoTitle.equals("") ){
//            addMemo(memoTitle, memoContent, event.getId());
//        }else if (!oldMemoTitle.equals("") && memoTitle.equals("")){
//            calendar.getMemo(oldMemoTitle).removeEvent(event.getId());
//        }else if(!oldMemoTitle.equals(memoTitle)){
//            calendar.editMemoText(oldMemoTitle,memoContent);
//            calendar.editMemoTitle(oldMemoTitle,memoTitle);
//        }
//    }

    //    private void editTags(){
//        for(String t:oldTags){
//            calendar.removeTag(t,event.getId());
//        }
//        addTags(tags,event.getId());
//    }
    public void handleDelete() {
        System.out.println("delete clicked");
        try {
            eventCollection.removeEvent(event);
            closeGUI();
        } catch (InvalidDateException e) {
            e.printStackTrace();
            System.out.println("Something is wrong with event Generator then");
        }
    }

    public void handleAddAlert() {
        System.out.println("add clicked");
        Alert controller = openGUI("alert.fxml");
        DataSaver ds = new DataSaver("users/" + username + "/" + calendar.getName());
        controller.initialize(ds.loadAlertCollection(event.getId()));
    }

    public void handleShareEvent() throws InvalidDateException {
        System.out.println("share clicked");
        eventCollection.postponedEvent(event);
    }

    public void handlePostpone() {
        System.out.println("postpone clicked");
        try {
            eventCollection.postponedEvent(event);
        } catch (InvalidDateException e) {
            System.out.println("Something is wrong with event generator");
        }
    }

    public void handleDuplicate() {
        System.out.println("Duplicate clicked");
        //TODO: need date input...
        EventAddUI dup = openGUI("EventAddUI.fxml");
        dup.setCalendar(calendar);
        dup.setCalendarController(calendarController);
        dup.showEventDetails(event);
    }


}
