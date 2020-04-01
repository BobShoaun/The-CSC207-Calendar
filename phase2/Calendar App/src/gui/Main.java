package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        primaryStage.setTitle("Login");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

//    protected static void swapWindows(Event e, String fxml, String title) throws IOException {
//        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
//        stage.hide();
//        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxml + ".fxml"));
//        Parent root = fxmlLoader.load();
//
//        Scene scene = new Scene(root);
//        stage.setScene(scene);
//        stage.setTitle(title);
//        stage.show();
//    }

    public static void main(String[] args) {
        launch(args);
    }
}
