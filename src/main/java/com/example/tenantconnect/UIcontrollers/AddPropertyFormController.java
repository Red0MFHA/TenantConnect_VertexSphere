package com.example.tenantconnect.UIcontrollers;

        import javafx.fxml.FXML;
        import javafx.scene.control.ComboBox;
        import javafx.scene.control.TextField;
        import javafx.stage.Stage;
        import javafx.scene.control.Alert;

        public class AddPropertyFormController {

        @FXML private TextField nameField;
        @FXML private TextField addressField;
        @FXML private TextField unitsField;
        @FXML private ComboBox<String> statusComboBox;

    private ListingsController parentController;

    public void setParentController(ListingsController parentController) {
    this.parentController = parentController;
    }

    @FXML
    private void submitForm() {
    // Get values
    String name = nameField.getText();
    String address = addressField.getText();
    String units = unitsField.getText();
    String status = statusComboBox.getValue();

    // Basic Validation
    if (name.isEmpty() || address.isEmpty() || units.isEmpty()) {
    showAlert("Error", "Please fill in all fields.");
    return;
    }

    // Send data back to the main table
    if (parentController != null) {
    parentController.addPropertyToTable(name, address, units, status);
    }

    // Close the window
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