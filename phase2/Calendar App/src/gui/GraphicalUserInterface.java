package gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class GraphicalUserInterface {

    protected Stage window;
    private String defaultWindowTitle;

    protected void setWindow (Stage window) {
        this.window = window;
    }

    protected void setDefaultWindowTitle(String title) {
        defaultWindowTitle = title;
    }

    protected void setWindowTitle(String title) {
        window.setTitle(title);
    }

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
            updateNextGUI(fxmlLoader.getController());
            return fxmlLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * opens a new stage(window)
     */
    protected <T extends GraphicalUserInterface> T openGUI(String fxml) {
        Stage newWindow = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxml));
        try {
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            newWindow.setScene(scene);
            newWindow.show();
            return fxmlLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void updateNextGUI (GraphicalUserInterface gui) {
        gui.setWindow(window);
        gui.setDefaultWindowTitle(defaultWindowTitle);
    }


}
