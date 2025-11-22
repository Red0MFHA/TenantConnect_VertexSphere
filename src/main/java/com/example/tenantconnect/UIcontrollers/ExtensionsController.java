package com.example.tenantconnect.UIcontrollers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;

/**
 * Controller for the Payment Extensions view (extensions.fxml).
 * Initializes the table with mock data and sets up column bindings.
 */
public class ExtensionsController {

    // --- Data Model ---
    /**
     * Represents a single payment extension request record.
     */
    public static class ExtensionRequest {
        private final String tenant;
        private final String property;
        private final LocalDate currentDueDate;
        private final LocalDate requestedDate;
        private final String reason;
        private final String status;

        public ExtensionRequest(String tenant, String property, LocalDate currentDueDate, LocalDate requestedDate, String reason, String status) {
            this.tenant = tenant;
            this.property = property;
            this.currentDueDate = currentDueDate;
            this.requestedDate = requestedDate;
            this.reason = reason;
            this.status = status;
        }

        // Getters for TableView compatibility (required for PropertyValueFactory)
        public String getTenant() { return tenant; }
        public String getProperty() { return property; }
        public LocalDate getCurrentDueDate() { return currentDueDate; }
        public LocalDate getRequestedDate() { return requestedDate; }
        public String getReason() { return reason; }
        public String getStatus() { return status; }
    }

    // --- FXML Bindings ---
    @FXML private TableView<ExtensionRequest> extensionsTable;
    @FXML private TableColumn<ExtensionRequest, String> tenantColumn;
    @FXML private TableColumn<ExtensionRequest, String> propertyColumn;
    // Note: We use LocalDate for date fields as it's the modern Java way
    @FXML private TableColumn<ExtensionRequest, LocalDate> currentDueColumn;
    @FXML private TableColumn<ExtensionRequest, LocalDate> requestedDateColumn;
    @FXML private TableColumn<ExtensionRequest, String> reasonColumn;
    @FXML private TableColumn<ExtensionRequest, String> statusColumn;
    @FXML private TableColumn<ExtensionRequest, Void> actionsColumn; // Placeholder for action buttons

    private final ObservableList<ExtensionRequest> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // 1. Initialize Columns - link data fields to table columns
        tenantColumn.setCellValueFactory(new PropertyValueFactory<>("tenant"));
        propertyColumn.setCellValueFactory(new PropertyValueFactory<>("property"));
        currentDueColumn.setCellValueFactory(new PropertyValueFactory<>("currentDueDate"));
        requestedDateColumn.setCellValueFactory(new PropertyValueFactory<>("requestedDate"));
        reasonColumn.setCellValueFactory(new PropertyValueFactory<>("reason"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // 2. Load Mock Data for the table
        data.add(new ExtensionRequest(
                "Jane Doe", "Unit 101, Sunset Apts",
                LocalDate.of(2025, 11, 1),
                LocalDate.of(2025, 11, 15),
                "Late pay due to medical bills.", "Pending"
        ));
        data.add(new ExtensionRequest(
                "John Smith", "Unit 205, River View",
                LocalDate.of(2025, 10, 15),
                LocalDate.of(2025, 11, 1),
                "Awaiting transfer of funds from another account.", "Approved"
        ));
        data.add(new ExtensionRequest(
                "Alice Johnson", "Unit 3B, Downtown Plaza",
                LocalDate.of(2025, 12, 5),
                LocalDate.of(2025, 12, 12),
                "Short-term cash flow delay.", "Pending"
        ));

        // 3. Set data to the TableView
        extensionsTable.setItems(data);

        // Note: Actions column requires a custom CellFactory for buttons,
        // which is typically added here but omitted for a clean initialization.
    }
}