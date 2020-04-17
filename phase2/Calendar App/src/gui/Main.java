package gui;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        GraphicalUserInterface gui = new GraphicalUserInterface();
        gui.setWindow(primaryStage);
        gui.setDefaultWindowTitle("The CSC207 Calendar");
        LoginUI loginUI = gui.showGUI("login.fxml");
        loginUI.setDarkTheme();
    }

    public static void main(String[] args) {
        launch(args);
    }

}