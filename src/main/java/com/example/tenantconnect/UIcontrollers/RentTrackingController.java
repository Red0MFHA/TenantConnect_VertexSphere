package com.example.tenantconnect.UIcontrollers;

import com.example.tenantconnect.Services.FacadeClass;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.List;

public class RentTrackingController {

    // --- TEMPORARY STRUCT (Static Nested Class) ---
    // This serves as the temporary structure for combining and displaying data
    public static class RentTableItem {
        private int paymentId; // Used for the reminder function
        private String tenantName;
        private String propertyName;
        private String dueDate;
        private float amount;
        private String status;

        public RentTableItem(int paymentId, String tenantName, String propertyName, String dueDate, float amount, String status) {
            this.paymentId = paymentId;
            this.tenantName = tenantName;
            this.propertyName = propertyName;
            this.dueDate = dueDate;
            this.amount = amount;
            this.status = status;
        }

        // Getters (required for PropertyValueFactory)
        public int getPaymentId() { return paymentId; }
        public String getTenantName() { return tenantName; }
        public String getPropertyName() { return propertyName; }
        public String getDueDate() { return dueDate; }
        public float getAmount() { return amount; }
        public String getStatus() { return status; }
    }
    // ---------------------------------------------


    // NOTE: Replace '1' with the actual ID of the logged-in owner.
    private final int CURRENT_OWNER_ID = FacadeClass.CURRENT_USER_ID;

    FacadeClass f;

    // Use the temporary struct as the type
    @FXML private TableView<RentTableItem> rentTable;
    @FXML private TableColumn<RentTableItem, String> tenantColumn;
    @FXML private TableColumn<RentTableItem, String> propertyColumn;
    @FXML private TableColumn<RentTableItem, String> dueDateColumn;
    @FXML private TableColumn<RentTableItem, Float> amountColumn;
    @FXML private TableColumn<RentTableItem, String> statusColumn;

    @FXML private TextField reminderRowIdField;
    @FXML private Button sendReminderButton;

    private ObservableList<RentTableItem> rentData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        f = FacadeClass.getInstance();

        // 1. Setup Table Columns using RentTableItem property names
        tenantColumn.setCellValueFactory(new PropertyValueFactory<>("tenantName"));
        propertyColumn.setCellValueFactory(new PropertyValueFactory<>("propertyName"));
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // 2. Load data and set items
        loadRentTrackingData();
        rentTable.setItems(rentData);

        // 3. Set up the Reminder button action
        sendReminderButton.setOnAction(event -> handleSendReminder());
    }

    /**
     * Fetches combined rent and tenant data for the current owner from the database.
     */
    private void loadRentTrackingData() {
        rentData.clear();
        try {
            // Get data using the new service method
            List<RentTableItem> data = f.getPaymentService().getRentTrackingDataForOwner(CURRENT_OWNER_ID);
            rentData.addAll(data);

        } catch (Exception e) {
            System.err.println("Error fetching rent tracking data: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load rent data.");
        }
    }

    /**
     * Handles the click event for the Send Reminder button.
     */
    private void handleSendReminder() {
        String inputIds = reminderRowIdField.getText().trim();
        if (inputIds.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Required", "Please enter one or more payment IDs to send a reminder.");
            return;
        }

        String[] idArray = inputIds.split(",");
        String successMessages = "";
        String failureMessages = "";

        for (String idStr : idArray) {
            try {
                int paymentId = Integer.parseInt(idStr.trim());

                // --- Reminder Logic Placeholder ---
                // We need to resolve the Tenant ID from the Payment ID before calling f.getPaymentService().sendReminder(tenantId, message)
                // For now, we'll confirm the successful parsing and prompt for the next step.

                successMessages += "Reminder for Payment ID " + paymentId + " ready to send (needs tenant ID lookup). \n";
                // ----------------------------------

            } catch (NumberFormatException e) {
                failureMessages += "Invalid ID: '" + idStr + "'. \n";
            }
        }

        String result = (successMessages.isEmpty() ? "" : "Successfully processed IDs:\n" + successMessages) +
                (failureMessages.isEmpty() ? "" : "\nFailed IDs:\n" + failureMessages);

        if (!result.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Reminder Status", result);
        }
        reminderRowIdField.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
}