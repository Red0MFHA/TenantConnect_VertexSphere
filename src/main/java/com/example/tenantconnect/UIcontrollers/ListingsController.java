//package com.example.tenantconnect.UIcontrollers;
//import com.example.tenantconnect.Services.FacadeClass;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Scene;
//import javafx.scene.control.TableColumn;
//import javafx.scene.control.TableView;
//import javafx.scene.control.cell.PropertyValueFactory;
//import javafx.scene.layout.VBox;
//import javafx.stage.Modality;
//import javafx.stage.Stage;
//import java.io.IOException;
//
//public class ListingsController {
//FacadeClass f;
//    // Data Model
//    public static class Property {
//        private String name;
//        private String address;
//        private String units;
//        private String status;
//
//        public Property(String name, String address, String units, String status) {
//            this.name = name;
//            this.address = address;
//            this.units = units;
//            this.status = status;
//        }
//        // Getters needed for TableView
//        public String getName() { return name; }
//        public String getAddress() { return address; }
//        public String getUnits() { return units; }
//        public String getStatus() { return status; }
//    }
//
//    @FXML private TableView<Property> propertiesTable;
//    @FXML private TableColumn<Property, String> nameColumn;
//    @FXML private TableColumn<Property, String> addressColumn;
//    @FXML private TableColumn<Property, String> unitsColumn;
//    @FXML private TableColumn<Property, String> statusColumn;
//    @FXML private TableColumn<Property, Void> actionsColumn;
//
//    private ObservableList<Property> propertyData = FXCollections.observableArrayList();
//
//    @FXML
//    public void initialize() {
//        f= FacadeClass.getInstance();
//        // Setup columns
//        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
//        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
//        unitsColumn.setCellValueFactory(new PropertyValueFactory<>("units"));
//        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
//
//        // Add dummy data
//        propertyData.add(new Property("Sunset Apartments", "123 Main St", "12", "Occupied"));
//        propertiesTable.setItems(propertyData);
//    }
//
//    // --- OPEN THE MODAL ---
//    @FXML
//    private void handleAddPropertyClick() {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tenantconnect/Owner/AddPropertyForm.fxml"));
//            VBox root = loader.load();
//
//            // Pass reference to this controller so the form can send data back
//            AddPropertyFormController formController = loader.getController();
//            formController.setParentController(this);
//
//            Stage stage = new Stage();
//            stage.initModality(Modality.APPLICATION_MODAL); // Blocks interaction with main window
//            stage.setTitle("Add Property");
//            stage.setScene(new Scene(root));
//            stage.showAndWait();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // Method called by the form controller to save data
//    public void addPropertyToTable(String name, String address, String units, String status) {
//        propertyData.add(new Property(name, address, units, status));
//    }
//}

package com.example.tenantconnect.UIcontrollers;

import com.example.tenantconnect.Domain.Property;
import com.example.tenantconnect.Services.FacadeClass;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;

public class ListingsController {
    FacadeClass f;
    // NOTE: Replace '1' with the actual ID of the logged-in owner.
    private final int CURRENT_OWNER_ID = FacadeClass.CURRENT_USER_ID;

    @FXML private TableView<Property> propertiesTable;
    @FXML private TableColumn<Property, String> nameColumn;
    @FXML private TableColumn<Property, String> addressColumn;
    // Removed unitsColumn because Domain.Property doesn't have a getUnits() method.
    @FXML private TableColumn<Property, String> statusColumn;
    @FXML private TableColumn<Property, Void> actionsColumn;

    private ObservableList<Property> propertyData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        f = FacadeClass.getInstance();

        // 1. Setup columns using Domain.Property field names
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("property_name"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        // statusColumn is kept
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
            // Show an alert on failure
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load properties. Check console for details.");
        }
    }

    // --- OPEN THE MODAL ---
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
     * The signature now includes all fields required by PropertyService.addProperty.
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