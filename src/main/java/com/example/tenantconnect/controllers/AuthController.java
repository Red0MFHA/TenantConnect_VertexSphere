package com.example.tenantconnect.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.io.IOException;


import com.example.tenantconnect.Repositories.UserRepository;


public class AuthController {

    private UserRepository userRepo;

    @FXML
    private TextField loginEmail;
    @FXML
    private PasswordField loginPassword;


    // Signup fields
    @FXML private TextField signupName;
    @FXML private TextField signupEmail;
    @FXML private PasswordField signupPassword;
    @FXML private PasswordField signupConfirm;
    @FXML private ComboBox<String> signupRole;

//    public AuthController() {
//        userRepo = new UserRepository();
//    }
    @FXML
    private void initialize() {
        userRepo = new UserRepository();
        // Fill roles
        signupRole.getItems().addAll("owner", "tenant");
    }

    // LOGIN button pressed
    @FXML
    private void onLogin(ActionEvent event) {
        String email = loginEmail.getText();
        String password = loginPassword.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Email or Password cannot be empty");
            return;
        }

        //database checks
        int userId = userRepo.login(email, password);
        if (userId != -1) {
            showAlert("Success", "Login successful! User ID: " + userId);
            changeScene(event, "/com/example/tenantconnect/views/Dashboard.fxml");
        } else {
            showAlert("Invalid", "Email or Password is incorrect");
        }
    }

    // SIGNUP button pressed
    @FXML
    private void onSignup(ActionEvent event) {
        String fullName = signupName.getText().trim();
        String email = signupEmail.getText().trim();
        String password = signupPassword.getText();
        String confirmPassword = signupConfirm.getText();
        String role = signupRole.getValue();

        // 1️⃣ Check all fields are filled
        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || role == null) {
            showAlert("Error", "All fields must be filled!");
            return;
        }

        // 2️⃣ Validate email format
        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            showAlert("Error", "Invalid email format!");
            return;
        }

        // 3️⃣ Check passwords match
        if (!password.equals(confirmPassword)) {
            showAlert("Error", "Passwords do not match!");
            return;
        }

        // 4️⃣ Attempt to insert user into DB
        boolean success = userRepo.addUser(email, password, fullName, role);
        if (success) {
            showAlert("Success", "Signup successful! You can now login.");
            changeScene(event, "/com/example/tenantconnect/views/Login.fxml");
        } else {
            showAlert("Error", "Signup failed! Email might already exist.");
        }
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
