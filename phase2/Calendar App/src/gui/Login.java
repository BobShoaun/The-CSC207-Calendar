package gui;

import exceptions.InvalidDateException;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import user.UserManager;

import java.io.IOException;

public class Login extends GraphicalUserInterface {

    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Label bottomMessage;

    private UserManager userManager = new UserManager();


    public Login() {
        try {
            userManager.loadUsers();
        } catch (IOException e) {
            System.out.println("Problem loading users!");
        }
    }

    @FXML
    private void handleLogin() throws InvalidDateException {
        String usernameText = username.getText();
        String passwordText = password.getText();

        if (!userManager.loginUser(usernameText, passwordText)) {
            bottomMessage.setText("Sorry, that didn't work. Please try again.");
            bottomMessage.setFont(new Font("Source Code Pro", 14));
            bottomMessage.setBackground(Background.EMPTY);
            System.out.println(usernameText + " " + passwordText + " failed to log in");
        } else {
            System.out.println("Logged in");
            showCalendarUI();
        }
    }

    @FXML
    private void handleRegister() {
        System.out.println("register clicked");
        showRegisterUI();
    }

    private void showCalendarUI() throws InvalidDateException {
        Calendar calendarController = showGUI("calendar.fxml");
        calendarController.setUser(userManager.getCurrentUser());
        calendarController.setTheme();
    }

    private void showRegisterUI() {
        Register g = this.showGUI("register.fxml");
        g.setUserManager(userManager);
    }

}