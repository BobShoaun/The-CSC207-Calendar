package gui;

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

    @FXML
    private void handleLogin(Event e) throws IOException {
        String usernameText = username.getText();
        String passwordText = password.getText();

        if (!userManager.loginUser(usernameText, passwordText)) {
            bottomMessage.setText("Sorry, that didn't work. Please try again.");
            bottomMessage.setFont(new Font("Source Code Pro", 16));
            bottomMessage.setBackground(Background.EMPTY);
        } else {
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.hide();
            Parent root = FXMLLoader.load(getClass().getResource("calendar.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
    }

}
