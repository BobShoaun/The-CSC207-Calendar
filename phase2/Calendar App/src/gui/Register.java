package gui;

import exceptions.*;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import user.UserManager;

import java.io.IOException;

public class Register {

    @FXML
    private Label usernameErrorLabel;
    @FXML
    private Label passwordErrorLabel;
    @FXML
    private Label confirmPasswordErrorLabel;

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;

    private UserManager userManager;

    public void setUserManager (UserManager userManager) {
        this.userManager = userManager;
    }

    public Register () {

    }

    public void init () {
        setLabelsNotVisible();
    }

    private void setLabelsNotVisible () {
        usernameErrorLabel.setVisible(false);
        passwordErrorLabel.setVisible(false);
        confirmPasswordErrorLabel.setVisible(false);
    }

    @FXML
    private void handleRegister(Event e) throws IOException, InvalidDateException {
        setLabelsNotVisible();
        System.out.println("Register");
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        try {
            userManager.registerUser(username, password, confirmPassword);
            userManager.loginUser(username, password);
            showCalendarUI(e);

        } catch (UsernameTakenException ex) {
            usernameErrorLabel.setText("Username already taken!");
            usernameErrorLabel.setVisible(true);
        } catch (InvalidUsernameException ex){
            usernameErrorLabel.setText("Invalid username!");
            usernameErrorLabel.setVisible(true);
        } catch (PasswordMismatchException ex) {
            confirmPasswordErrorLabel.setText("Passwords do not match!");
            confirmPasswordErrorLabel.setVisible(true);
        } catch (InvalidPasswordException ex) {
            passwordErrorLabel.setText("Invalid password!");
            passwordErrorLabel.setVisible(true);
        }

    }

    @FXML
    private void handleCancel(Event e) throws IOException { // go back to login page
        System.out.println("Cancel");
        showLoginUI(e);
    }

    private void showCalendarUI(Event e) throws IOException, InvalidDateException {
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.hide();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("calendar.fxml"));
        Parent root = fxmlLoader.load();
        Calendar calendarController = fxmlLoader.getController();
        calendarController.setUser(userManager.getCurrentUser());
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void showLoginUI(Event e) throws IOException {
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.hide();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

}