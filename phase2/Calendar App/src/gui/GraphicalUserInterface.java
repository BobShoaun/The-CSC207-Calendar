package gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * General class for any graphical user interface
 * handles ui transitions and window management.
 * @author Ng Bob Shoaun
 */
public class GraphicalUserInterface {

    protected Stage window;
    private String defaultWindowTitle;

    /**
     * Set window(stage) of the current GUI
     * @param window
     */
    protected void setWindow (Stage window) {
        this.window = window;
    }

    /**
     * Set the default window title, the window title when none is specified
     * @param title
     */
    protected void setDefaultWindowTitle(String title) {
        defaultWindowTitle = title;
    }

    /**
     * Set the title of the window
     * @param title
     */
    protected void setWindowTitle(String title) {
        window.setTitle(title);
    }

    /**
     * Set the GUI to be dark theme
     */
    protected void setDarkTheme () {
        window.getScene().getStylesheets().add("gui/DarkTheme.css");
    }

    /**
     * changes the scene within the same stage(window)
     * @param fxml fmxl file of the scene
     * @param <T> type of GUI controller
     * @return GUI controller
     */
    protected <T extends GraphicalUserInterface> T showGUI (String fxml) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxml));
        try {
            window.hide();
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            window.setScene(scene);
            window.setTitle(defaultWindowTitle);
            window.show();
            GraphicalUserInterface gui = fxmlLoader.getController();
            gui.setWindow(window);
            gui.setDefaultWindowTitle(defaultWindowTitle);
            return fxmlLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * opens a new stage(window)
     * @param fxml
     * @param <T>
     * @return
     */
    protected <T extends GraphicalUserInterface> T openGUI(String fxml) {
        Stage newWindow = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxml));
        try {
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            newWindow.setScene(scene);
            newWindow.show();
            GraphicalUserInterface gui = fxmlLoader.getController();
            gui.setWindow(newWindow);
            gui.setDefaultWindowTitle(defaultWindowTitle);
            return fxmlLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Closes the current stage (window)
     */
    protected void closeGUI () {
        window.close();
    }


}
