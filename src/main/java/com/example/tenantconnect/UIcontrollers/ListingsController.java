package com.example.tenantconnect.UIcontrollers;

import com.example.tenantconnect.Domain.Property;
import com.example.tenantconnect.Services.FacadeClass;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ListingsController {
    FacadeClass f;
    // NOTE: Replace '1' with the actual ID of the logged-in owner.
    private final int CURRENT_OWNER_ID = FacadeClass.CURRENT_USER_ID;

    @FXML private TableView<Property> propertiesTable;
    // ADDED Property ID Column
    @FXML private TableColumn<Property, Integer> propertyIdColumn;
    @FXML private TableColumn<Property, String> nameColumn;
    @FXML private TableColumn<Property, String> addressColumn;
    @FXML private TableColumn<Property, String> unitsColumn; // Keeping unitsColumn as it exists in FXML
    @FXML private TableColumn<Property, String> statusColumn;
    // REMOVED @FXML private TableColumn<Property, Void> actionsColumn;

    private ObservableList<Property> propertyData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        f = FacadeClass.getInstance();

        // 1. Setup columns using Domain.Property field names
        propertyIdColumn.setCellValueFactory(new PropertyValueFactory<>("property_id")); // ADDED
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("property_name"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        // Note: unitsColumn does not map to a getter in the current Property model,
        // but is kept to avoid a LoadException if it was defined in the FXML.
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // 2. Load data from the database on initialization
        loadPropertiesFromDatabase();
        propertiesTable.setItems(propertyData);
    }

    /**
     * Fetches property data for the current owner from the database.
     */
    private void loadPropertiesFromDatabase() {
        propertyData.clear();

        try {
            List<Property> properties = f.getPropertyService().getOwnerProperties(CURRENT_OWNER_ID);
            propertyData.addAll(properties);
        } catch (Exception e) {
            System.err.println("Error fetching properties from database: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load properties. Check console for details.");
        }
    }

    // --- 3. EDIT PROPERTY FUNCTIONALITY ---
    @FXML
    private void handleEditPropertyClick() {
        Optional<PropertyUpdateData> result = showPropertyStatusDialog();

        result.ifPresent(data -> {
            try {
                int propertyId = data.propertyId();
                String newStatus = data.newStatus();

                // FIX: Convert the status to lowercase to match the SQLite CHECK constraint
                String statusForDb = newStatus.toLowerCase();

                // 1. Create a partial Property object for update
                Property p = new Property();
                p.setProperty_id(propertyId);
                p.setStatus(statusForDb); // Use the lowercase status
                p.setOwner_id(CURRENT_OWNER_ID); // Required for validation in service

                // 2. Call the service to update
                boolean success = f.getPropertyService().updateProperty(CURRENT_OWNER_ID, p);

                if (success) {
                    loadPropertiesFromDatabase(); // Refresh table
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Property ID " + propertyId + " status updated to '" + statusForDb + "'.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Failure", "Failed to update property ID " + propertyId + ". It might have an active contract or the ID is invalid.");
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Property ID must be a valid number.");
            }
        });
    }

    /**
     * Creates and shows a custom dialog for editing property status.
     */
    private Optional<PropertyUpdateData> showPropertyStatusDialog() {
        Dialog<PropertyUpdateData> dialog = new Dialog<>();
        dialog.setTitle("Edit Property Status");
        dialog.setHeaderText("Change Status for a Specific Property");

        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField propertyIdField = new TextField();
        propertyIdField.setPromptText("Enter Property ID");

        ComboBox<String> statusComboBox = new ComboBox<>();
        statusComboBox.getItems().addAll("Vacant", "Occupied");
        statusComboBox.setPromptText("Select New Status");

        grid.add(new Label("Property ID:"), 0, 0);
        grid.add(propertyIdField, 1, 0);
        grid.add(new Label("New Status:"), 0, 1);
        grid.add(statusComboBox, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                if (propertyIdField.getText().trim().isEmpty() || statusComboBox.getValue() == null) {
                    showAlert(Alert.AlertType.WARNING, "Missing Input", "Please enter a Property ID and select a new status.");
                    return null;
                }
                try {
                    return new PropertyUpdateData(
                            Integer.parseInt(propertyIdField.getText().trim()),
                            statusComboBox.getValue()
                    );
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Input", "Property ID must be a number.");
                    return null;
                }
            }
            return null;
        });

        return dialog.showAndWait();
    }

    // Helper record for dialog result
    private record PropertyUpdateData(int propertyId, String newStatus) {}


    // --- 4. REMOVE PROPERTY FUNCTIONALITY ---
    @FXML
    private void handleRemovePropertyClick() {
        Optional<String> result = showPropertyIdInputDialog("Remove Property", "Enter the ID of the property you want to remove:", "Property ID:");

        result.ifPresent(idString -> {
            try {
                int propertyId = Integer.parseInt(idString.trim());

                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                confirmation.setTitle("Confirm Deletion");
                confirmation.setHeaderText("Remove Property ID: " + propertyId);
                confirmation.setContentText("Are you sure you want to remove this property? This action cannot be undone.");

                Optional<ButtonType> confirmResult = confirmation.showAndWait();

                if (confirmResult.isPresent() && confirmResult.get() == ButtonType.OK) {
                    boolean success = f.getPropertyService().deleteProperty(CURRENT_OWNER_ID, propertyId);

                    if (success) {
                        loadPropertiesFromDatabase(); // Refresh table
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Property ID " + propertyId + " removed successfully.");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Failure", "Failed to remove property ID " + propertyId + ". A contract might be active or the ID is invalid.");
                    }
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Property ID must be a valid number.");
            }
        });
    }

    /**
     * Helper method to show a simple input dialog for Property ID.
     */
    private Optional<String> showPropertyIdInputDialog(String title, String header, String content) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(content);
        return dialog.showAndWait();
    }


    // --- OPEN THE MODAL (Existing Code) ---
    @FXML
    private void handleAddPropertyClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tenantconnect/Owner/AddPropertyForm.fxml"));
            VBox root = loader.load();

            AddPropertyFormController formController = loader.getController();
            formController.setParentController(this);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add Property");
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method called by the AddPropertyFormController to save data and update the table.
     */
    public void addPropertyToTable(
            String name, String address, String city, String state, String zip_code,
            String property_type, double rent_amount, double security_deposit, String status) {

        // 1. Create and populate the new Property domain object
        Property newProperty = new Property();
        newProperty.setOwner_id(CURRENT_OWNER_ID); // Set owner ID
        newProperty.setProperty_name(name);
        newProperty.setAddress(address);
        newProperty.setCity(city);
        newProperty.setState(state);
        newProperty.setZip_code(zip_code);
        newProperty.setProperty_type(property_type);
        newProperty.setRent_amount(rent_amount);
        newProperty.setSecurity_deposit(security_deposit);
        newProperty.setStatus(status);

        // 2. Save the property to the database via the service
        boolean success = f.getPropertyService().addProperty(CURRENT_OWNER_ID, newProperty);

        // 3. If successful, refresh the table data from the database
        if (success) {
            loadPropertiesFromDatabase();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Property '" + name + "' added successfully!");
        } else {
            showAlert(Alert.AlertType.ERROR, "Failure", "Failed to add property to the database. It might already exist or a contract is active.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
}