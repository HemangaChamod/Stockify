package com.desktopui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.beans.value.ChangeListener;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import com.desktopui.utils.SceneNavigator;

public class AddItemController {

    @FXML private TextField nameField;
    @FXML private TextArea descriptionField;
    @FXML private TextField sellingPriceField;
    @FXML private TextField costPriceField;
    @FXML private TextField stockField;
    @FXML private TextField totalValueField;
    @FXML private ComboBox<String> unitCombo;
    @FXML private Label imageNameLabel;
    @FXML private ImageView imagePreview;

    private File selectedImage;

    /* ---------------- INITIALIZE ---------------- */

    @FXML
    public void initialize() {
        unitCombo.getItems().addAll("Pieces (pcs)", "Kg", "Liters", "Box");

        ChangeListener<String> calculator =
                (obs, oldVal, newVal) -> calculateTotal();

        costPriceField.textProperty().addListener(calculator);
        stockField.textProperty().addListener(calculator);

        totalValueField.setEditable(false);
    }

    private void calculateTotal() {
        try {
            double cost = Double.parseDouble(costPriceField.getText());
            int stock = Integer.parseInt(stockField.getText());
            totalValueField.setText(String.format("%.2f", cost * stock));
        } catch (Exception e) {
            totalValueField.setText("");
        }
    }

    /* ---------------- IMAGE UPLOAD ---------------- */

    @FXML
    private void handleImageUpload() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Item Image");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        selectedImage = chooser.showOpenDialog(null);

        if (selectedImage != null) {
            imageNameLabel.setText(selectedImage.getName());
            imagePreview.setImage(
                    new Image(selectedImage.toURI().toString())
            );
        }
    }

    /* ---------------- CREATE ITEM ---------------- */

    @FXML
    private void handleCreateItem() {
        try {
            // ---------- VALIDATION ----------
            if (nameField.getText().isBlank()
                    || sellingPriceField.getText().isBlank()
                    || costPriceField.getText().isBlank()
                    || stockField.getText().isBlank()
                    || unitCombo.getValue() == null) {

                showAlert("Validation Error", "Please fill all required fields");
                return;
            }

            if (selectedImage == null) {
                showAlert("Validation Error", "Please upload an image");
                return;
            }

            URL url = new URL("http://localhost:9090/api/items");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            String boundary = "----JavaFXBoundary" + System.currentTimeMillis();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty(
                    "Content-Type",
                    "multipart/form-data; boundary=" + boundary
            );
            conn.setRequestProperty("Accept", "application/json");

            OutputStream output = conn.getOutputStream();
            PrintWriter writer = new PrintWriter(
                    new OutputStreamWriter(output, StandardCharsets.UTF_8), true
            );

            // ---------- FORM FIELDS ----------
            addFormField(writer, boundary, "name", nameField.getText());
            addFormField(writer, boundary, "description", descriptionField.getText());
            addFormField(writer, boundary, "sellingPrice", sellingPriceField.getText());
            addFormField(writer, boundary, "costPrice", costPriceField.getText());
            addFormField(writer, boundary, "unit", unitCombo.getValue());
            addFormField(writer, boundary, "stockInHand", stockField.getText());

            // ---------- IMAGE ----------
            addFilePart(writer, output, boundary, "image", selectedImage);

            writer.append("--").append(boundary).append("--").append("\r\n");
            writer.flush();
            writer.close();

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                showAlert("Success", "Item created successfully");
                SceneNavigator.openDashboard();
            } else {
                showAlert("Error", "Failed to create item (Code: " + responseCode + ")");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Server error");
        }
    }

    /* ---------------- HELPERS ---------------- */

    private void addFormField(
            PrintWriter writer, String boundary,
            String name, String value) {

        writer.append("--").append(boundary).append("\r\n");
        writer.append("Content-Disposition: form-data; name=\"")
                .append(name).append("\"\r\n\r\n");
        writer.append(value).append("\r\n");
    }

    private void addFilePart(
            PrintWriter writer, OutputStream output,
            String boundary, String fieldName, File file)
            throws IOException {

        writer.append("--").append(boundary).append("\r\n");
        writer.append("Content-Disposition: form-data; name=\"")
                .append(fieldName)
                .append("\"; filename=\"")
                .append(file.getName()).append("\"\r\n");
        writer.append("Content-Type: ")
                .append(Files.probeContentType(file.toPath()))
                .append("\r\n\r\n");
        writer.flush();

        Files.copy(file.toPath(), output);
        output.flush();

        writer.append("\r\n");
        writer.flush();
    }

    private void clearForm() {
        nameField.clear();
        descriptionField.clear();
        sellingPriceField.clear();
        costPriceField.clear();
        stockField.clear();
        totalValueField.clear();
        unitCombo.getSelectionModel().clearSelection();
        imagePreview.setImage(null);
        imageNameLabel.setText("Supports JPG, PNG up to 5MB");
        selectedImage = null;
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
    private void handleCancel() {
        SceneNavigator.openDashboard();
    }

}
