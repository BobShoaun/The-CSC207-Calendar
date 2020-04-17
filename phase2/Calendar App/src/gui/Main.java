package gui;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Class containing the main method for starting the GUI.
 */
public class Main extends Application {

    /**
     * Start the GUI with the given Stage
     *
     * @param primaryStage Stage to set GUI to
     */
    @Override
    public void start(Stage primaryStage) {
        GraphicalUserInterface gui = new GraphicalUserInterface();
        gui.setWindow(primaryStage);
        gui.setDefaultWindowTitle("The CSC207 Calendar");
        LoginUI loginUI = gui.showGUI("login.fxml");
        loginUI.setDarkTheme();
    }

    /**
     * Start the GUI
     *
     * @param args Arguments from command-line
     */
    public static void main(String[] args) {
        launch(args);
    }

}