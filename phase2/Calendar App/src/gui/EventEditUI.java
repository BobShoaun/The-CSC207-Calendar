package gui;

import entities.Event;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setLabelInvisible();
    }

    protected void setUsername(String username) {
        this.username = username;
    }

    public void showEventDetails() {
        nameField.setText(event.getName());
        startDate.setValue(GregorianCalendarToLocalDate(event.getStartDate()));
        endDate.setValue(GregorianCalendarToLocalDate(event.getEndDate()));
        startTime.setText(getTime(event.getStartDate()));
        endTime.setText(getTime(event.getEndDate()));
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


    public void handleEdit() {
        System.out.println("Done (edit) clicked");
        try {
            getUserInput();
            Event editedEvent = createEvent(name, start, end);
            currEvents.editEvent(event,editedEvent);

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
    public void handleDelete() throws InvalidDateException {
        System.out.println("delete clicked");
        currEvents.removeEvent(event);
        closeGUI();
    }

    public void handleAddAlert() {
        System.out.println("add clicked");
        Alert controller = openGUI("alert.fxml");
        DataSaver ds = new DataSaver("users/" + username + "/" + calendar.getName());
        controller.initialize(ds.loadAlertCollection(event.getId()));
    }

    public void handleShareEvent() {
        System.out.println("share clicked");
    }

    public void handlePostpone() {
        System.out.println("postpone clicked");
        //TODO: need an postpone UI window
        //TODO: rechedual too
    }

    public void handleDuplicate() {
        System.out.println("Duplicate clicked");
        //TODO: need
    }

    private LocalDate GregorianCalendarToLocalDate(GregorianCalendar GC) {
        Date date = GC.getTime();
        return LocalDate.of(date.getYear(), date.getMonth(), date.getDay());
    }

    private String getTime(GregorianCalendar GC) {
        String pattern = "HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(GC.getTime());
    }
}
