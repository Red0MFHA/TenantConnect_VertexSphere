// File: ComplaintsController.java

package com.example.tenantconnect.UIcontrollers; // Adjust package name

import com.example.tenantconnect.Services.FacadeClass;
import com.example.tenantconnect.Services.ComplaintService;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class ComplaintsController {

    private FacadeClass f;

    // NOTE: Replace this with the actual ID of the logged-in owner.
    private final int CURRENT_OWNER_ID = FacadeClass.CURRENT_USER_ID;

    // FXML Fields
    @FXML private TableView<ComplaintTableItem> complaintsTable;
    @FXML private TableColumn<ComplaintTableItem, Integer> tenantIdColumn; // Mapped to Complaint ID
    @FXML private TableColumn<ComplaintTableItem, String> tenantColumn;
    @FXML private TableColumn<ComplaintTableItem, String> propertyColumn;
    @FXML private TableColumn<ComplaintTableItem, String> categoryColumn;
    @FXML private TableColumn<ComplaintTableItem, String> titleColumn;
    @FXML private TableColumn<ComplaintTableItem, String> dateColumn;
    @FXML private TableColumn<ComplaintTableItem, String> statusColumn;

    @FXML private TextField complaintIdField;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private Button updateStatusButton;

    private ObservableList<ComplaintTableItem> complaintsData;

    // --- Inner Class for TableView ---
    public static class ComplaintTableItem {
        private final SimpleIntegerProperty complaintId;
        private final SimpleStringProperty tenantName;
        private final SimpleStringProperty propertyName;
        private final SimpleStringProperty category;
        private final SimpleStringProperty title;
        private final SimpleStringProperty date;
        private final SimpleStringProperty status;

        public ComplaintTableItem(int complaintId, String tenantName, String propertyName, String category, String title, String date, String status) {
            this.complaintId = new SimpleIntegerProperty(complaintId);
            this.tenantName = new SimpleStringProperty(tenantName);
            this.propertyName = new SimpleStringProperty(propertyName);
            this.category = new SimpleStringProperty(category);
            this.title = new SimpleStringProperty(title);
            this.date = new SimpleStringProperty(date);
            this.status = new SimpleStringProperty(status);
        }

        // Getters (must match PropertyValueFactory names in initialize method)
        public int getComplaintId() { return complaintId.get(); }
        public String getTenantName() { return tenantName.get(); }
        public String getPropertyName() { return propertyName.get(); }
        public String getCategory() { return category.get(); }
        public String getTitle() { return title.get(); }
        public String getDate() { return date.get(); }
        public String getStatus() { return status.get(); }
    }
    // --- End Inner Class ---

    @FXML
    public void initialize() {
        f = FacadeClass.getInstance();

        // 1. Setup Table Columns
        // NOTE: FXML mapping uses "tenantIdColumn" for Complaint ID
        tenantIdColumn.setCellValueFactory(new PropertyValueFactory<>("complaintId"));
        tenantColumn.setCellValueFactory(new PropertyValueFactory<>("tenantName"));
        propertyColumn.setCellValueFactory(new PropertyValueFactory<>("propertyName"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // 2. Setup ComboBox Items (Already in FXML, but ensure it's loaded)
        // The FXML defines the items, but if you want to dynamically add more:
        // statusComboBox.getItems().addAll("Open", "In Progress", "Resolved", "Rejected");

        // 3. Load data and set items
        loadComplaintsData();

        // 4. Set up the Update Status button action
        updateStatusButton.setOnAction(event -> handleUpdateStatus());
    }

    private void loadComplaintsData() {
        // Fetch data using the new Repository method
        List<ComplaintTableItem> data = f.getComplaintService().getOwnerComplaintTrackingData(CURRENT_OWNER_ID);
        complaintsData = FXCollections.observableArrayList(data);
        complaintsTable.setItems(complaintsData);
    }

    private void handleUpdateStatus() {
        String complaintIdStr = complaintIdField.getText().trim();
        String newStatus = statusComboBox.getValue(); // This gets "Rejected", "Resolved", or "Schedule Later"

        if (complaintIdStr.isEmpty() || newStatus == null) {
            showAlert(Alert.AlertType.WARNING, "Missing Input", "Please enter a Complaint ID and select a new Status.");
            return;
        }

        try {
            int complaintId = Integer.parseInt(complaintIdStr);

            // Map ComboBox selection to database status values
            String dbStatus = mapStatusToDB(newStatus);
            String resolutionMessage = "Complaint #" + complaintId + " status updated to: " + newStatus;

            // 1. Get Tenant ID for Notification
            int tenantId = f.getComplaintService().getTenantIdForNotification(complaintId);

            // 2. Call Service to update status and send notification
            boolean success = f.getComplaintService().updateComplaintStatus(tenantId, complaintId, dbStatus, resolutionMessage);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Complaint ID " + complaintId + " status updated to '" + newStatus + "'.");
                loadComplaintsData(); // Refresh the table
            } else {
                showAlert(Alert.AlertType.ERROR, "Failure", "Could not update Complaint ID " + complaintId + ". Check if the ID exists.");
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Complaint ID must be a number.");
        }
    }

    private String mapStatusToDB(String displayStatus) {
        return switch (displayStatus) {
            case "Resolved" -> "resolved";
            case "Rejected" -> "rejected";
            case "Schedule Later" -> "in_progress"; // Map "Schedule Later" to 'in_progress' for the database
            default -> displayStatus.toLowerCase().replace(" ", "_");
        };
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
}