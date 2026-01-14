package com.desktopui;

import com.desktopui.utils.SceneNavigator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        SceneNavigator.setStage(stage);

        Parent root = FXMLLoader.load(
                MainApp.class.getResource("/fxml/login.fxml")
        );

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Inventory Management");
        stage.show();
    }
}
