package com.desktopui.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class LoginController {

    @FXML
    private TextField signinEmail;

    @FXML
    private PasswordField signinPassword;

    @FXML
    private Label signInTab;

    @FXML
    private Label signUpTab;

    @FXML
    private Button actionButton;

    private boolean isSignUpMode = false;

    /* ---------------- TAB SWITCH ---------------- */

    @FXML
    private void showSignIn() {
        isSignUpMode = false;

        signInTab.getStyleClass().setAll("tab-active");
        signUpTab.getStyleClass().setAll("tab-inactive");

        actionButton.setText("Sign In");
    }

    @FXML
    private void showSignUp() {
        isSignUpMode = true;

        signUpTab.getStyleClass().setAll("tab-active");
        signInTab.getStyleClass().setAll("tab-inactive");

        actionButton.setText("Create Account");
    }

    /* ---------------- MAIN ACTION BUTTON ---------------- */

    @FXML
    private void handleAction() {
        String email = signinEmail.getText();
        String password = signinPassword.getText();

        if (email.isBlank() || password.isBlank()) {
            showAlert("Validation Error", "Email and password are required");
            return;
        }

        boolean success;

        if (isSignUpMode) {
            success = register(email, password);
            if (!success) {
                showAlert("Signup Failed", "User already exists or server error");
                return;
            }
        } else {
            success = login(email, password);
            if (!success) {
                showAlert("Login Failed", "Invalid email or password");
                return;
            }
        }

        loadDashboard();
    }

    /* ---------------- BACKEND CALLS ---------------- */

    private boolean login(String email, String password) {
        return sendAuthRequest("http://localhost:9090/api/auth/login", email, password);
    }

    private boolean register(String email, String password) {
        return sendAuthRequest("http://localhost:9090/api/auth/register", email, password);
    }

    private boolean sendAuthRequest(String endpoint, String email, String password) {
        try {
            URL url = new URL(endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String json = String.format(
                    "{\"email\":\"%s\",\"password\":\"%s\"}",
                    email, password
            );

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }

            int status = conn.getResponseCode();
            return status == 200 || status == 201;


        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Server Error", "Backend not reachable");
            return false;
        }
    }

    /* ---------------- NAVIGATION ---------------- */

    private void loadDashboard() {
        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) signinEmail.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Dashboard");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
