package com.example.tenantconnect.UIcontrollers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

public class ListingsController {

    // Data Model
    public static class Property {
        private String name;
        private String address;
        private String units;
        private String status;

        public Property(String name, String address, String units, String status) {
            this.name = name;
            this.address = address;
            this.units = units;
            this.status = status;
        }
        // Getters needed for TableView
        public String getName() { return name; }
        public String getAddress() { return address; }
        public String getUnits() { return units; }
        public String getStatus() { return status; }
    }

    @FXML private TableView<Property> propertiesTable;
    @FXML private TableColumn<Property, String> nameColumn;
    @FXML private TableColumn<Property, String> addressColumn;
    @FXML private TableColumn<Property, String> unitsColumn;
    @FXML private TableColumn<Property, String> statusColumn;
    @FXML private TableColumn<Property, Void> actionsColumn;

    private ObservableList<Property> propertyData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Setup columns
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        unitsColumn.setCellValueFactory(new PropertyValueFactory<>("units"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Add dummy data
        propertyData.add(new Property("Sunset Apartments", "123 Main St", "12", "Occupied"));
        propertiesTable.setItems(propertyData);
    }

    // --- OPEN THE MODAL ---
    @FXML
    private void handleAddPropertyClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tenantconnect/Owner/AddPropertyForm.fxml"));
            VBox root = loader.load();

            // Pass reference to this controller so the form can send data back
            AddPropertyFormController formController = loader.getController();
            formController.setParentController(this);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL); // Blocks interaction with main window
            stage.setTitle("Add Property");
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method called by the form controller to save data
    public void addPropertyToTable(String name, String address, String units, String status) {
        propertyData.add(new Property(name, address, units, status));
    }
}