package com.desktopui.controller;

import com.desktopui.utils.SceneNavigator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.stage.FileChooser;
import javafx.scene.control.Alert;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;


public class DashboardController {

    @FXML
    private VBox emptyState;

    @FXML
    private VBox cardContainer;

    @FXML
    private TextField searchField;

    private static final String API_URL = "http://localhost:9090/api/items";

    // Cache ALL items for search
    private List<Map<String, Object>> allItems = new ArrayList<>();

    /* ================= INITIALIZE ================= */

    @FXML
    public void initialize() {
        loadItems();

        // LIVE SEARCH
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filterItems(newVal);
        });
    }

    /* ================= LOAD ITEMS ================= */

    private void loadItems() {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                showEmpty();
                return;
            }

            ObjectMapper mapper = new ObjectMapper();
            InputStream inputStream = conn.getInputStream();

            allItems = mapper.readValue(
                    inputStream,
                    new TypeReference<List<Map<String, Object>>>() {}
            );

            if (allItems == null || allItems.isEmpty()) {
                showEmpty();
            } else {
                renderItems(allItems);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showEmpty();
        }
    }

    /* ================= SEARCH ================= */

    private void filterItems(String keyword) {

        if (keyword == null || keyword.isBlank()) {
            renderItems(allItems);
            return;
        }

        String lower = keyword.toLowerCase();

        List<Map<String, Object>> filtered = allItems.stream()
                .filter(item ->
                        item.get("name").toString().toLowerCase().contains(lower)
                     || item.get("description").toString().toLowerCase().contains(lower)
                )
                .toList();

        renderItems(filtered);
    }

    /* ================= RENDER ================= */

    private void showEmpty() {
        emptyState.setVisible(true);
        emptyState.setManaged(true);
        cardContainer.getChildren().clear();
    }

    private void renderItems(List<Map<String, Object>> items) {

        cardContainer.getChildren().clear();

        if (items == null || items.isEmpty()) {
            showEmpty();
            return;
        }

        emptyState.setVisible(false);
        emptyState.setManaged(false);

        for (Map<String, Object> item : items) {
            cardContainer.getChildren().add(createRow(item));
        }
    }

    /* ================= ROW ================= */

    private GridPane createRow(Map<String, Object> item) {

        GridPane row = new GridPane();
        row.getStyleClass().add("table-row");
        row.setPadding(new Insets(18, 0, 18, 0));
        row.setHgap(20);

        row.getColumnConstraints().addAll(
                col(360),
                col(140),
                col(140),
                col(100),
                col(140),
                col(140)
        );

        // IMAGE + NAME
        HBox nameCell = new HBox(12);
        nameCell.setAlignment(Pos.CENTER_LEFT);

        ImageView imageView = new ImageView();
        imageView.setFitWidth(32);
        imageView.setFitHeight(32);
        imageView.setPreserveRatio(true);

        try {
            Object imagePath = item.get("imagePath");
            if (imagePath != null) {
                String imageUrl =
                        "http://localhost:9090/images/" + imagePath;
                imageView.setImage(new Image(imageUrl, true));
            }
        } catch (Exception ignored) {}

        VBox textBox = new VBox(4);

        Label name = new Label(item.get("name").toString());
        name.getStyleClass().add("item-name");

        Label sub = new Label(item.get("description").toString());
        sub.getStyleClass().add("item-sub");

        textBox.getChildren().addAll(name, sub);
        nameCell.getChildren().addAll(imageView, textBox);

        row.add(nameCell, 0, 0);
        row.add(price(item.get("sellingPrice")), 1, 0);
        row.add(price(item.get("costPrice")), 2, 0);
        row.add(center(item.get("unit")), 3, 0);
        row.add(center(item.get("stockInHand")), 4, 0);

        Label total = price(item.get("totalStockValue"));
        total.getStyleClass().add("stock-value");
        row.add(total, 5, 0);

        row.setOnMouseClicked(e ->
                SceneNavigator.openEditItem(mapToItemDTO(item))
        );

        return row;
    }

    /* ================= HELPERS ================= */

    private ColumnConstraints col(double width) {
        ColumnConstraints c = new ColumnConstraints();
        c.setPrefWidth(width);
        c.setMinWidth(width);
        return c;
    }

    private Label price(Object value) {
        double v = Double.parseDouble(value.toString());
        Label label = new Label(String.format("$ %.2f", v));
        label.getStyleClass().add("price");
        return label;
    }

    private Label center(Object value) {
        Label label = new Label(value.toString());
        label.getStyleClass().add("center");
        return label;
    }

    private ItemDTO mapToItemDTO(Map<String, Object> map) {

        ItemDTO item = new ItemDTO();
        item.setId(Long.parseLong(map.get("id").toString()));
        item.setName(map.get("name").toString());
        item.setDescription(map.get("description").toString());
        item.setSellingPrice(Double.parseDouble(map.get("sellingPrice").toString()));
        item.setCostPrice(Double.parseDouble(map.get("costPrice").toString()));
        item.setUnit(map.get("unit").toString());
        item.setStockInHand(Integer.parseInt(map.get("stockInHand").toString()));
        item.setTotalStockValue(Double.parseDouble(map.get("totalStockValue").toString()));

        Object imagePath = map.get("imagePath");
        if (imagePath != null) {
            item.setImageUrl(
                    "http://localhost:9090/images/" + imagePath
            );
        }

        return item;
    }

    /* ================= ACTIONS ================= */

    @FXML
    private void onNewProduct() {
        SceneNavigator.openAddItem();
    }

    @FXML
    private void onExport() {

        if (allItems == null || allItems.isEmpty()) {
            new Alert(Alert.AlertType.WARNING,
                    "No items available to export").show();
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Export Inventory");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV File", "*.csv")
        );
        chooser.setInitialFileName("inventory_export.csv");

        File file = chooser.showSaveDialog(cardContainer.getScene().getWindow());

        if (file == null) return;

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {

            // CSV HEADER
            writer.println(
                    "Name,Description,Selling Price,Cost Price,Unit,Stock In Hand,Total Stock Value"
            );

            // DATA ROWS
            for (Map<String, Object> item : allItems) {

                writer.printf(
                        "\"%s\",\"%s\",%.2f,%.2f,%s,%d,%.2f%n",
                        item.get("name"),
                        item.get("description"),
                        Double.parseDouble(item.get("sellingPrice").toString()),
                        Double.parseDouble(item.get("costPrice").toString()),
                        item.get("unit"),
                        Integer.parseInt(item.get("stockInHand").toString()),
                        Double.parseDouble(item.get("totalStockValue").toString())
                );
            }

            new Alert(Alert.AlertType.INFORMATION,
                    "Export successful!\n\nSaved to:\n" + file.getAbsolutePath())
                    .show();

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "Failed to export inventory").show();
        }
    }
}
