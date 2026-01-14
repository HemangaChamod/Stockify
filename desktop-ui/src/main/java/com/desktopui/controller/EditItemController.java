package com.desktopui.controller;
import com.desktopui.utils.SceneNavigator;
import com.desktopui.api.ApiClient;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;

public class EditItemController {

    /* ---------- FXML FIELDS ---------- */

    @FXML private TextField nameField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField sellingPriceField;
    @FXML private TextField costPriceField;
    @FXML private ComboBox<String> unitCombo;
    @FXML private TextField stockField;
    @FXML private TextField totalValueField;
    @FXML private ImageView itemImage;

    /* ---------- STATE ---------- */

    private ItemDTO currentItem;
    private File selectedImageFile;

    /* ---------- INITIALIZE ---------- */

    @FXML
    public void initialize() {
        unitCombo.getItems().addAll("PCS", "KG", "LITRE");
    }

    /* ---------- LOAD ITEM ---------- */

    public void loadItem(ItemDTO item) {
        this.currentItem = item;

        nameField.setText(item.getName());
        descriptionArea.setText(item.getDescription());
        sellingPriceField.setText(String.valueOf(item.getSellingPrice()));
        costPriceField.setText(String.valueOf(item.getCostPrice()));
        unitCombo.setValue(item.getUnit());
        stockField.setText(String.valueOf(item.getStockInHand()));
        totalValueField.setText(String.valueOf(item.getTotalStockValue()));

        if (item.getImageUrl() != null) {
            itemImage.setImage(new Image(item.getImageUrl(), true));
        }
    }

    /* ---------- BUTTON ACTIONS ---------- */

    @FXML
    private void onSave() {

    // Disable buttons to prevent double-click
    nameField.getScene().getRoot().setDisable(true);

    new Thread(() -> {
        try {
            ItemDTO updatedItem = new ItemDTO();
            updatedItem.setId(currentItem.getId());
            updatedItem.setName(nameField.getText());
            updatedItem.setDescription(descriptionArea.getText());
            updatedItem.setSellingPrice(
                    Double.parseDouble(sellingPriceField.getText())
            );
            updatedItem.setCostPrice(
                    Double.parseDouble(costPriceField.getText())
            );
            updatedItem.setUnit(unitCombo.getValue());
            updatedItem.setStockInHand(
                    Integer.parseInt(stockField.getText())
            );

            // BLOCKING CALL — NOW SAFE
            ApiClient.updateItem(updatedItem);

            // Switch scene on FX thread
            javafx.application.Platform.runLater(() -> {
                SceneNavigator.openDashboard();
            });

        } catch (Exception e) {
            e.printStackTrace();
            javafx.application.Platform.runLater(() -> {
                nameField.getScene().getRoot().setDisable(false);
                new Alert(Alert.AlertType.ERROR,
                        "Failed to save item").show();
            });
        }
    }).start();
}


    @FXML
    public void onCancel() {
        SceneNavigator.openDashboard();
    }


    @FXML
    public void onChangeImage() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File file = chooser.showOpenDialog(null);
        if (file != null) {
            selectedImageFile = file;
            itemImage.setImage(new Image(file.toURI().toString()));
        }
    }

    @FXML
    public void onDelete() {

    // Confirmation dialog
    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
    confirm.setTitle("Confirm Delete");
    confirm.setHeaderText("Delete this item?");
    confirm.setContentText(
            "This action cannot be undone.\n\nAre you sure?"
    );

    if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
        return; // user cancelled
    }

    // Disable UI
    nameField.getScene().getRoot().setDisable(true);

    // Run delete in background thread
    new Thread(() -> {
        try {
            ApiClient.deleteItem(currentItem.getId());

            // Back to dashboard on FX thread
            javafx.application.Platform.runLater(() -> {
                SceneNavigator.openDashboard();
            });

        } catch (Exception e) {
            e.printStackTrace();

            javafx.application.Platform.runLater(() -> {
                nameField.getScene().getRoot().setDisable(false);
                new Alert(
                        Alert.AlertType.ERROR,
                        "Failed to delete item"
                ).show();
            });
        }
    }).start();
    }

}
