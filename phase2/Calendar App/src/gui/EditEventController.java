package gui;

import alert.AlertCollection;
import event.Event;
import event.EventCollection;
import event.Series;
import exceptions.InvalidDateException;
import exceptions.NoSuchSeriesException;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import memotag.Memo;
import memotag.Tag;
import user.DataSaver;
import user.UserManager;

import java.util.List;

/**
 * GUI controller for editing events
 */
public class EditEventController extends AddEventController {

    @FXML
    private CheckBox postponeCheckbox;
    private Event event;
    private String oldMemoTitle;
    private String username;
    private UserManager userManager;

    /**
     * Set the Event to edit, with the correct fields in the GUI.
     *
     * @param event Event to edit.
     */
    public void setEvent(Event event) {
        this.event = event;
        if (calendar.getMemo(event) != null) {
            oldMemoTitle = calendar.getMemo(event).getTitle();
        }
        showEventDetails(event);
        postponeCheckbox.setSelected(event.isPostponed());
    }

    /**
     * Set the User Manager (for event sharing and such)
     *
     * @param userManager UserManager to set
     */
    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    /**
     * Set the EventCollection to edit
     *
     * @param e EventCollection to be set
     */
    public void setEventCollection(EventCollection e) {
        if (e instanceof Series) {
            System.out.println("EventC set to:" + ((Series) e).getName());
        } else if (e instanceof EventCollection) {
            System.out.println("EventC set to default");
        } else {
            System.out.println("Something is wrong");
        }
        this.eventCollection = e;
    }

    /**
     * Set the name of the user
     *
     * @param username Username
     */
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
            EventCollection old = eventCollection;
            if (isEventCollectionChanged(eventCollection)) {
                System.out.println("removed:::" + old.removeEvent(event));
                eventCollection.addEvent(event);
            } else {
                eventCollection.rescheduleEvent(event, start, end);
            }
            postponeEvent();
            editMemo(event, oldMemoTitle);
            editTags();
            closeGUI();
            save();
        } catch (InvalidDateException e) {
            dateTimeErrorLabel.setText("Invalid Date");
            dateTimeErrorLabel.setVisible(true);
        } catch (NoSuchSeriesException e) {
            seriesErrorLabel.setVisible(true);
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
        AlertController controller = openGUI("alert.fxml");
        DataSaver ds = new DataSaver("users/" + username + "/" + calendar.getName());
        AlertCollection alertCollection = ds.loadAlertCollection(event.getId());
        controller.initialize(alertCollection, calendar);
    }

    @FXML
    private void handleShareEvent() {
        ShareController controller = openGUI("share.fxml");
        controller.setEvent(event);
        controller.setUserManger(userManager);
        save();
    }

    private void postponeEvent() {
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
    private void handleCancel() {
        closeGUI();
    }

    @FXML
    private void handleDuplicate() {
        System.out.println("Duplicate clicked");
        //TODO: need date input...
        AddEventController dup = openGUI("addEvent.fxml");
        dup.setCalendar(calendar);
        dup.setCalendarUIController(calendarUIController);
        dup.showEventDetails(event);
    }
}
