package com.example.tenantconnect.UIcontrollers;

import com.example.tenantconnect.Services.FacadeClass;
import com.example.tenantconnect.Domain.PaymentExtension;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

/**
 * Controller for the Payment Extensions view (extensions.fxml).
 */
public class ExtensionsController {

    // --- Table Data Model (for Display) ---
    // Note: In a real app, the Service or Repository would handle joining
    // the Tenant and Property names, but for now we map directly from PaymentExtension.
    public static class ExtensionTableItem {
        private final int extensionId;
        private final int tenantId; // Changed to ID as names aren't in PaymentExtension object
        private final int paymentId; // Corresponds to the Payment ID
        private final String currentDueDate;
        private final String requestedDate;
        private final String reason;
        private final String status;

        public ExtensionTableItem(int extensionId, int tenantId, int paymentId, String currentDueDate, String requestedDate, String reason, String status) {
            this.extensionId = extensionId;
            this.tenantId = tenantId;
            this.paymentId = paymentId;
            this.currentDueDate = currentDueDate;
            this.requestedDate = requestedDate;
            this.reason = reason;
            this.status = status;
        }

        // Getters for TableView compatibility
        public int getExtensionId() { return extensionId; }
        public int getTenantId() { return tenantId; }
        public int getPaymentId() { return paymentId; }
        public String getCurrentDueDate() { return currentDueDate; }
        public String getRequestedDate() { return requestedDate; }
        public String getReason() { return reason; }
        public String getStatus() { return status; }
    }


    // --- FXML Bindings ---
    @FXML private TableView<ExtensionTableItem> extensionsTable;
    @FXML private TableColumn<ExtensionTableItem, Integer> extensionIdColumn;
    @FXML private TableColumn<ExtensionTableItem, Integer> tenantIdColumn; // Use Tenant ID for now
    @FXML private TableColumn<ExtensionTableItem, Integer> paymentIdColumn; // Use Payment ID for now
    @FXML private TableColumn<ExtensionTableItem, String> currentDueColumn;
    @FXML private TableColumn<ExtensionTableItem, String> requestedDateColumn;
    @FXML private TableColumn<ExtensionTableItem, String> reasonColumn;
    @FXML private TableColumn<ExtensionTableItem, String> statusColumn;

    @FXML private TextField extensionIdField;
    @FXML private Button acceptButton;
    @FXML private Button rejectButton;

    private FacadeClass f;
    private final int CURRENT_OWNER_ID = FacadeClass.CURRENT_USER_ID; // Use a known owner ID for testing
    private ObservableList<ExtensionTableItem> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        f = FacadeClass.getInstance();

        // 1. Initialize Columns - link data fields to table columns
        extensionIdColumn.setCellValueFactory(new PropertyValueFactory<>("extensionId"));
        tenantIdColumn.setCellValueFactory(new PropertyValueFactory<>("tenantId")); // New Binding
        paymentIdColumn.setCellValueFactory(new PropertyValueFactory<>("paymentId")); // New Binding
        currentDueColumn.setCellValueFactory(new PropertyValueFactory<>("currentDueDate"));
        requestedDateColumn.setCellValueFactory(new PropertyValueFactory<>("requestedDate"));
        reasonColumn.setCellValueFactory(new PropertyValueFactory<>("reason"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // 2. Load Data from Service
        loadExtensionsData();

        // 3. Set up button actions (already linked in FXML but good practice to ensure here)
        // acceptButton.setOnAction(event -> handleAcceptExtension());
        // rejectButton.setOnAction(event -> handleRejectExtension());
    }

    private void loadExtensionsData() {
        data.clear();
        // Assuming user type is "owner" for this dashboard
        List<PaymentExtension> extensions = f.getPaymentService().getPendingExtensionRequests(CURRENT_OWNER_ID, "owner");

        for (PaymentExtension ext : extensions) {
            data.add(new ExtensionTableItem(
                    ext.getExtension_id(),
                    ext.getTenant_id(),
                    ext.getPayment_id(),
                    ext.getCurrent_due_date(),
                    ext.getRequested_due_date(),
                    ext.getReason(),
                    ext.getStatus()
            ));
        }

        extensionsTable.setItems(data);
    }

    // --- Action Handlers ---

    @FXML
    private void handleAcceptExtension() {
        processExtensionRequest("approved");
    }

    @FXML
    private void handleRejectExtension() {
        processExtensionRequest("rejected");
    }

    private void processExtensionRequest(String newStatus) {
        String idText = extensionIdField.getText().trim();
        if (idText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing ID", "Please enter the Extension ID.");
            return;
        }

        try {
            int extensionId = Integer.parseInt(idText);

            Optional<String> result = showInputDialog(newStatus);
            if (result.isEmpty()) {
                return;
            }
            String message = result.get();

            boolean success = false;

            if (newStatus.equals("approved")) {
                success = f.getPaymentService().approveExtentionRequest(extensionId, message);
            } else if (newStatus.equals("rejected")) {
                success = f.getPaymentService().rejectExtentionRequest(extensionId, message);
            }

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Extension ID " + extensionId + " " + newStatus.toUpperCase() + " successfully.");
                loadExtensionsData(); // Refresh data
                extensionIdField.clear();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update Extension ID " + extensionId + ". Check the ID and ensure it is pending.");
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid ID", "Extension ID must be a number.");
        }
    }

    // Helper for simple alerts
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Helper for input dialog
    private Optional<String> showInputDialog(String status) {
        TextInputDialog dialog = new TextInputDialog("Regarding your extension request...");
        dialog.setTitle("Extension " + status);
        dialog.setHeaderText("Enter a brief note for the tenant regarding the decision.");
        dialog.setContentText("Message:");
        return dialog.showAndWait();
    }
}