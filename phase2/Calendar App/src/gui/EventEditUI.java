package gui;

import entities.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

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

    public void setEvent(Event event) {
        this.event = event;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setLabelInvisible();
    }
    public void handleEdit(){
        System.out.println("Done (edit) clicked");
    }
    public void handleDelete(){
        System.out.println("Done delete clicked");
    }
    public void handleAddAlert(){
        System.out.println("Done add clicked");
    }
    public void handleShareEvent(){
        System.out.println("Done share clicked");
    }
    public void handlePostpone(){
        System.out.println("Done postpone clicked");
    }
    public void handleDuplicate(){}
}
