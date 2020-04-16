package gui;

import entities.Event;
import entities.EventCollection;
import exceptions.InvalidDateException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import mt.Memo;
import mt.Tag;
import user.DataSaver;
import user.UserManager;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
    private String oldMemoTitle;
//    private String[] oldTags;

    private String username;
    private UserManager userManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setLabelInvisible();
    }

    public void setEvent(Event event) {
        this.event = event;
        if(calendar.getMemo(event) != null)
        {
            oldMemoTitle = calendar.getMemo(event).getTitle();
        }
        showEventDetails(event);
    }

    public void setUserManager(UserManager userManager){
        this.userManager = userManager;
    }

    public void setEventCollection(EventCollection e) {
        this.eventCollection = e;
    }

    protected void setUsername(String username) {
        this.username = username;
    }

    public void handleEdit() {
        System.out.println("Done (edit) clicked");
        try {
            getUserInput();
            if(!event.getName().equals(name)){
                //Update all the references to tags and memos
                event.setName(name); //This changes the id, so this is all necessary
                List<Tag> tags = calendar.getTags(event);
                for (Tag t :tags) {
                    calendar.removeTag(t.getText(), event);
                }
                for (Tag t :
                        tags) {
                    t.addEvent(event.getId());
                }
                Memo memo = calendar.getMemo(event);
                if(memo != null){
                    memo.removeEvent(event);
                    memo.addEvent(event);
                }

            }
            eventCollection.rescheduleEvent(event, start, end);
            editMemo();
            editTags();
            calendarController.updateDisplayedEvents();
            closeGUI();
            save();
        } catch (InvalidDateException e) {
            dateTimeErrorLabel.setText("Invalid Date");
            dateTimeErrorLabel.setVisible(true);
        }

    }

    private void editMemo(){
        String newMemoTitle = memosField.getText();
        if(calendar.getMemo(newMemoTitle) != null){ //The changed memo already exists, so we move the event to the new one
            calendar.getMemo(newMemoTitle).addEvent(event);
            calendar.getMemo(newMemoTitle).setText(memoTextArea.getText());
            if(calendar.getMemo(oldMemoTitle) != null){
                calendar.getMemo(oldMemoTitle).removeEvent(event);
            }
        } else {
            if(calendar.getMemo(oldMemoTitle) != null){  //The new memo does not already exist so we change the old one
                calendar.getMemo(oldMemoTitle).setTitle(newMemoTitle);
                calendar.getMemo(newMemoTitle).setText(memoTextArea.getText());
            }
            else{ //Or we create a new one
                calendar.addMemo(new Memo(newMemoTitle, memoTextArea.getText()));
                calendar.getMemo(newMemoTitle).addEvent(event);
            }
        }
    }

    private void editTags(){
        for(Tag t: calendar.getTags(event)){
            calendar.removeTag(t.getText(), event);
        }
        addTags(event.getId());
    }
    public void handleDelete() {
        System.out.println("delete clicked");
        try {
            eventCollection.removeEvent(event);
            save();
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
        Share controller = openGUI("Share.fxml");
        controller.setEvent(event);
        controller.setUserManger(userManager);
        save();
    }

    public void handlePostpone() {
        System.out.println("postpone clicked");
        try {
            eventCollection.postponedEvent(event);
            save();
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
