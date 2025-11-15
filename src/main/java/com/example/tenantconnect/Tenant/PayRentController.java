package com.example.tenantconnect.Tenant;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import java.io.File;

public class PayRentController {

    @FXML
    private TextField amountTextField;

    @FXML
    private TextField dueDateTextField;

    @FXML
    private ComboBox<String> paymentMethodComboBox;

    @FXML
    private Button uploadButton;

    @FXML
    private Label fileNameLabel;

    @FXML
    private Button payButton;

    private File selectedFile;

    @FXML
    public void initialize() {
        // Add payment methods to ComboBox
        paymentMethodComboBox.getItems().addAll(
                "Bank Transfer",
                "Credit Card",
                "Debit Card",
                "Check"
        );

        // Set up upload button action
        uploadButton.setOnAction(e -> handleFileUpload());

        // Set up pay button action
        payButton.setOnAction(e -> handlePayment());
    }

    private void handleFileUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Receipt File");

        // Set file filters
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "All Files (*.*)", "*.*"
        );
        fileChooser.getExtensionFilters().add(extFilter);

        selectedFile = fileChooser.showOpenDialog(uploadButton.getScene().getWindow());

        if (selectedFile != null) {
            fileNameLabel.setText(selectedFile.getName());
        } else {
            fileNameLabel.setText("No file chosen");
        }
    }

    private void handlePayment() {
        String paymentMethod = paymentMethodComboBox.getValue();

        // Validate inputs
        if (paymentMethod == null || paymentMethod.isEmpty()) {
            showAlert("Error", "Please select a payment method.");
            return;
        }

        // Process payment
        System.out.println("Payment Details:");
        System.out.println("Amount: " + amountTextField.getText());
        System.out.println("Due Date: " + dueDateTextField.getText());
        System.out.println("Payment Method: " + paymentMethod);
        System.out.println("Receipt: " + (selectedFile != null ? selectedFile.getName() : "None"));

        // Show success message
        showAlert("Success", "Payment submitted successfully!");

        // Reset form
        paymentMethodComboBox.setValue(null);
        fileNameLabel.setText("No file chosen");
        selectedFile = null;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}