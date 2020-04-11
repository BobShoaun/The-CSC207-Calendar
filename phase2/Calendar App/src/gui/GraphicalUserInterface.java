package gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class GraphicalUserInterface {

    protected Stage window;

    protected void setWindow (Stage window) {
        this.window = window;
    }

    protected void setWindowTitle(String title) {
        window.setTitle(title);
    }

    /**
     *
     * @param fxml fmxl file of the GUI
     * @param <T> type of GUI controller
     * @return GUI controller
     */
    protected <T extends GraphicalUserInterface> T showGUI (String fxml) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxml));
        try {
            window.hide();
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
//            scene.getStylesheets().add("gui/DarkTheme.css"); // enable this for global dark theme
            window.setScene(scene);
            window.show();
            ((GraphicalUserInterface) fxmlLoader.getController()).setWindow(window); // set the window of the next GUI too
            return fxmlLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
