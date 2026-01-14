package com.desktopui.utils;

import com.desktopui.MainApp;
import com.desktopui.controller.EditItemController;
import com.desktopui.controller.ItemDTO;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneNavigator {

    private static Stage stage;

    public static void setStage(Stage primaryStage) {
        stage = primaryStage;
    }

    // Always reload dashboard
    public static void openDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource("/fxml/dashboard.fxml")
            );
            Parent root = loader.load();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openAddItem() {
        try {
            Parent root = FXMLLoader.load(
                    MainApp.class.getResource("/fxml/add_item.fxml")
            );
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openEditItem(ItemDTO item) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource("/fxml/edit_item.fxml")
            );
            Parent root = loader.load();

            EditItemController controller = loader.getController();
            controller.loadItem(item);

            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
