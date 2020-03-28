package gui;

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

public class LoginController {

    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Label bottomMessage;

    private UserManager userManager = new UserManager();

    public LoginController() throws IOException{
        userManager.loadUsers();
    }

    @FXML
    private void handleLogin(Event e) throws IOException {
        String usernameText = username.getText();
        String passwordText = password.getText();

        if (!userManager.loginUser(usernameText, passwordText)) {
            bottomMessage.setText("Sorry, that didn't work. Please try again.");
            bottomMessage.setFont(new Font("Source Code Pro", 14));
            bottomMessage.setBackground(Background.EMPTY);
            System.out.println(usernameText + " " + passwordText + " failed to log in");
        } else {
            System.out.println("Logged in");
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.hide();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("calendar.fxml"));
            Parent root = fxmlLoader.load();
            CalendarController calendarController = fxmlLoader.getController();
            calendarController.setUser(userManager.getCurrentUser());
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
    }

    @FXML
    private void handleRegister(Event e) throws IOException{
        String usernameText = username.getText();
        String passwordText = password.getText();
        try {
            userManager.registerUser(usernameText, passwordText, passwordText);
        } catch (UsernameTakenException ex) {
            bottomMessage.setText("This username has already been taken.");
            return;
        } catch (PasswordMismatchException ignored) {

        }
        bottomMessage.setText("User successfully registered");
    }

}
