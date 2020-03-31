package gui;

import exceptions.InvalidDateException;
import exceptions.PasswordMismatchException;
import exceptions.UsernameTakenException;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

public class Login {

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
    private void handleLogin(Event e) throws IOException, InvalidDateException {
        String usernameText = username.getText();
        String passwordText = password.getText();

        if (!userManager.loginUser(usernameText, passwordText)) {
            bottomMessage.setText("Sorry, that didn't work. Please try again.");
            bottomMessage.setFont(new Font("Source Code Pro", 14));
            bottomMessage.setBackground(Background.EMPTY);
            System.out.println(usernameText + " " + passwordText + " failed to log in");
        } else {
            System.out.println("Logged in");
            showCalendarUI(e);
        }
    }

    @FXML
    private void handleRegister(Event e) throws IOException{
        System.out.println("register clicked");

        showRegisterUI(e);
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

    private void showRegisterUI(Event e) throws IOException {
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.hide();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("register.fxml"));
        Parent root = fxmlLoader.load();

        Register registerController = fxmlLoader.getController();
        registerController.setUserManager(userManager);
        registerController.init();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

}