package gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.text.Font;
import user.UserManager;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * GUI controller class for the Login GUI
 */
public class LoginController extends GraphicalUserInterface implements Initializable {

    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Label loginErrorLabel;

    private final UserManager userManager = new UserManager();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loginErrorLabel.setVisible(false);
    }

    /**
     * constructor for the login gui controller
     */
    public LoginController() {
        try {
            userManager.loadUsers();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogin() {
        String usernameText = username.getText();
        String passwordText = password.getText();

        if (!userManager.loginUser(usernameText, passwordText)) {
            loginErrorLabel.setVisible(true);

//            bottomMessage.setText("Sorry, that didn't work. Please try again.");
//            bottomMessage.setFont(new Font("Source Code Pro", 14));
//            bottomMessage.setBackground(Background.EMPTY);
        } else {
            showCalendarUI();
        }
    }

    private void showCalendarUI() {
        CalendarController calendarUIController = showGUI("calendar.fxml");
        calendarUIController.setUserManager(userManager);
        calendarUIController.setUser(userManager.getCurrentUser());
    }

    @FXML
    private void handleRegister() {
        RegisterController g = showGUI("register.fxml");
        g.setUserManager(userManager);
        g.setDarkTheme();
    }

}