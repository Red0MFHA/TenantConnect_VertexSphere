package com.example.tenantconnect.UIcontrollers;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Alert;

public class AddPropertyFormController {

    // Existing fields
    @FXML private TextField nameField;
    @FXML private TextField addressField;
    @FXML private TextField unitsField;
    @FXML private ComboBox<String> statusComboBox;

    // NEW FIELDS added via FXML
    @FXML private TextField cityField;
    @FXML private TextField stateField;
    @FXML private TextField zipCodeField;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private TextField rentField;
    @FXML private TextField depositField;

    private ListingsController parentController;

    public void setParentController(ListingsController parentController) {
        this.parentController = parentController;
    }

    @FXML
    public void initialize() {
        // Optional initialization for ComboBoxes if needed
        if (statusComboBox.getValue() == null) {
            statusComboBox.setValue("Vacant");
        }
        if (typeComboBox.getValue() == null) {
            typeComboBox.setValue("Apartment");
        }
    }

    @FXML
    private void submitForm() {
        // 1. Get String values for all fields
        String name = nameField.getText();
        String address = addressField.getText();
        String city = cityField.getText();
        String state = stateField.getText();
        String zip_code = zipCodeField.getText();
        String property_type = typeComboBox.getValue();
        String rentStr = rentField.getText();
        String depositStr = depositField.getText();
        String status = statusComboBox.getValue();
        // units field is ignored for persistence, but still collected here if needed later
        // String units = unitsField.getText();

        // 2. Basic Validation: Check for mandatory fields (Rent and Deposit need number check too)
        if (name.isEmpty() || address.isEmpty() || city.isEmpty() || state.isEmpty() ||
                zip_code.isEmpty() || property_type == null || rentStr.isEmpty() || depositStr.isEmpty() || status == null) {
            showAlert("Validation Error", "Please fill in all required fields.");
            return;
        }

        double rent_amount;
        double security_deposit;
        try {
            rent_amount = Double.parseDouble(rentStr);
            security_deposit = Double.parseDouble(depositStr);
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Rent Amount and Security Deposit must be valid numbers.");
            return;
        }

        // 3. Send all 9 required data points to the main controller
        if (parentController != null) {
            parentController.addPropertyToTable(
                    name, address, city, state, zip_code,
                    property_type, rent_amount, security_deposit, status
            );
        }

        // 4. Close the window
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
}