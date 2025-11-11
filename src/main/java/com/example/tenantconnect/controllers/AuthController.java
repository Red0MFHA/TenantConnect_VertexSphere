package com.example.tenantconnect.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.io.IOException;

public class AuthController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    // LOGIN button pressed
    @FXML
    private void onLogin(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Email or Password cannot be empty");
            return;
        }

        // --- Dummy authentication (replace with Database check) ---
        if (email.equals("admin@test.com") && password.equals("1234")) {
            showAlert("Success", "Login successful!");
            changeScene(event, "/com/example/tenantconnect/views/Dashboard.fxml");
        } else {
            showAlert("Invalid", "Email or Password is incorrect");
        }
    }

    // SIGNUP button pressed
    @FXML
    private void onSignup(ActionEvent event) {
        showAlert("Signup", "Redirecting to Signup Page...");
        changeScene(event, "/com/example/tenantconnect/views/Signup.fxml");
    }

    // ---------------- Helper Methods ----------------

    private void changeScene(ActionEvent event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene()
                    .getWindow();

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Failed to load page: " + e.getMessage());
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.show();
    }
}
