package gui;

import event.Event;
import event.EventCollection;
import exceptions.InvalidDateException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import memotag.Memo;
import memotag.Tag;
import user.DataSaver;
import user.UserManager;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * GUI controller for editing events
 */
public class EventEditUI extends EventAddUI {

    @FXML
    private CheckBox postponeCheckbox;
//    @FXML
//    private Button shareEventButton;
//    @FXML
//    private Button addAlertButton;
//    @FXML
//    private Button deleteButton;
//    @FXML
//    private Button editButton;
//    @FXML
//    private Button duplicateButton;

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
        if (calendar.getMemo(event) != null) {
            oldMemoTitle = calendar.getMemo(event).getTitle();
        }
        showEventDetails(event);
        postponeCheckbox.setSelected(event.isPostponed());
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    public void setEventCollection(EventCollection e) {
        this.eventCollection = e;
    }

    protected void setUsername(String username) {
        this.username = username;
    }

    @FXML
    private void handleEdit() {
        System.out.println("Done (edit) clicked");
        try {
            getUserInput();
            if (!event.getName().equals(name)) {
                //Update all the references to tags and memos
                List<Tag> tags = calendar.getTags(event);
                for (Tag t : tags) {
                    calendar.removeTag(t.getText(), event);
                }
                Memo memo = calendar.getMemo(event);
                if (memo != null) {
                    memo.removeEvent(event);
                }
                calendar.renameEvent(event, name); //This changes the id, so this is all necessary
                for (Tag t :
                        tags) {
                    t.addEvent(event.getId());
                }
                if (memo != null) {
                    memo.addEvent(event);
                }

            }
            eventCollection.rescheduleEvent(event, start, end);
            postPoneEvent();
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

    private void editMemo() {
        String newMemoTitle = memosField.getText();
        if (calendar.getMemo(newMemoTitle) != null) { //The changed memo already exists, so we move the event to the new one
            if (newMemoTitle.equals("")) {
                if (calendar.getMemo(oldMemoTitle) != null)
                    calendar.getMemo(oldMemoTitle).removeEvent(event);
                return;
            }
            if (calendar.getMemo(newMemoTitle) != null) { //The changed memo already exists, so we move the event to the new one
                calendar.getMemo(newMemoTitle).addEvent(event);
                calendar.getMemo(newMemoTitle).setText(memoTextArea.getText());
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

    private void editTags() {
        for (Tag t : calendar.getTags(event)) {
            calendar.removeTag(t.getText(), event);
        }
        addTags(event.getId());
    }

    @FXML
    private void handleDelete() {
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

    @FXML
    private void handleAddAlert() {
        System.out.println("add clicked");
        Alert controller = openGUI("alert.fxml");
        DataSaver ds = new DataSaver("users/" + username + "/" + calendar.getName());
        controller.initialize(ds.loadAlertCollection(event.getId()));
    }

    @FXML
    private void handleShareEvent() {
        Share controller = openGUI("Share.fxml");
        controller.setEvent(event);
        controller.setUserManger(userManager);
        save();
    }

    private void postPoneEvent() {
        if (postponeCheckbox.isSelected() && !event.isPostponed()) {
            System.out.println("postpone clicked");
            try {
                eventCollection.postponeEvent(event);
                save();
            } catch (InvalidDateException e) {
                System.out.println("Something is wrong with event generator");
            }
        }
    }

    @FXML
    private void handleDuplicate() {
        System.out.println("Duplicate clicked");
        //TODO: need date input...
        EventAddUI dup = openGUI("EventAddUI.fxml");
        dup.setCalendar(calendar);
        dup.setCalendarController(calendarController);
        dup.showEventDetails(event);
    }
}
