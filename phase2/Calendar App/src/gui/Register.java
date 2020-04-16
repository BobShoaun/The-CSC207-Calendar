package gui;

import exceptions.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import user.UserManager;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * GUI for registering new users
 * @author Ng Bob Shoaun
 */
public class Register extends GraphicalUserInterface implements Initializable {

    @FXML private Label usernameErrorLabel;
    @FXML private Label passwordErrorLabel;
    @FXML private Label confirmPasswordErrorLabel;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

    private UserManager userManager;

    /**
     * Sets the user manager to be used by this GUI
     * @param userManager
     */
    public void setUserManager (UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setLabelsNotVisible();
    }

    private void setLabelsNotVisible () {
        usernameErrorLabel.setVisible(false);
        passwordErrorLabel.setVisible(false);
        confirmPasswordErrorLabel.setVisible(false);
    }

    @FXML
    private void handleRegister() throws IOException, InvalidDateException {
        setLabelsNotVisible();
        System.out.println("Register");
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        try {
            userManager.registerUser(username, password, confirmPassword);
            userManager.loginUser(username, password);
            showCalendarUI();

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
    private void handleCancel() { // go back to login page
        System.out.println("Cancel");
        showLoginUI();
    }

    private void showCalendarUI() {
        Calendar calendarController = showGUI("calendar.fxml");
        calendarController.setUserManager(userManager);
        calendarController.setUser(userManager.getCurrentUser());
    }

    private void showLoginUI() {
        Login login = showGUI("login.fxml");
        login.setDarkTheme();
    }
}