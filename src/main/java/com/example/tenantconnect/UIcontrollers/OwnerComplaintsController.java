package com.example.tenantconnect.UIcontrollers;

import com.example.tenantconnect.Domain.Complaint;
import com.example.tenantconnect.Services.FacadeClass;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class OwnerComplaintsController {

    private final FacadeClass facade = FacadeClass.getInstance();

    @FXML private TableView<Complaint> complaintsTable;
    @FXML private TableColumn<Complaint, String> tenantColumn;
    @FXML private TableColumn<Complaint, String> propertyColumn;
    @FXML private TableColumn<Complaint, String> categoryColumn;
    @FXML private TableColumn<Complaint, String> titleColumn;
    @FXML private TableColumn<Complaint, String> dateColumn;
    @FXML private TableColumn<Complaint, String> statusColumn;
    @FXML private TableColumn<Complaint, Void> actionsColumn;

    @FXML
    private void initialize() {
        setupTableColumns();
        loadComplaints();
    }

    private void setupTableColumns() {
        tenantColumn.setCellValueFactory(new PropertyValueFactory<>("tenant_id"));
        propertyColumn.setCellValueFactory(new PropertyValueFactory<>("property_id"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        dateColumn.setCellValueFactory(cellData -> {
            String dateStr = cellData.getValue().getCreated_at();
            if (dateStr != null && dateStr.length() > 10) {
                return javafx.beans.binding.Bindings.createStringBinding(
                        () -> LocalDateTime.parse(dateStr.substring(0, 19))
                                .format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
                );
            }
            return javafx.beans.binding.Bindings.createStringBinding(() -> "N/A");
        });
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Actions column with Resolve/Reject buttons
        actionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button resolveBtn = new Button("Resolve");
            private final Button rejectBtn = new Button("Reject");
            private final HBox hbox = new HBox(8, resolveBtn, rejectBtn);

            {
                resolveBtn.setStyle("-fx-background-color: #10B981; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 6 12; -fx-background-radius: 8;");
                rejectBtn.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 6 12; -fx-background-radius: 8;");
                hbox.setStyle("-fx-alignment: center;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Complaint complaint = getTableRow().getItem();
                    if ("Resolved".equalsIgnoreCase(complaint.getStatus()) || "Rejected".equalsIgnoreCase(complaint.getStatus())) {
                        setGraphic(null);
                    } else {
                        resolveBtn.setOnAction(e -> resolveComplaint(complaint));
                        rejectBtn.setOnAction(e -> rejectComplaint(complaint));
                        setGraphic(hbox);
                    }
                }
            }
        });
    }

    private void loadComplaints() {
        var complaints = facade.getComplaintService()
                .getDueComplaintsByOwner(FacadeClass.CURRENT_USER_ID);  // Assuming owner is logged in

        complaintsTable.setItems(FXCollections.observableArrayList(complaints));
    }

    private void resolveComplaint(Complaint complaint) {
        boolean success = facade.getComplaintService()
                .updateComplaintStatus(complaint.getTenant_id(), complaint.getComplaint_id(), "Resolved", "Issue has been fixed.");

        if (success) {
            showAlert("Success", "Complaint resolved successfully!", Alert.AlertType.INFORMATION);
            loadComplaints(); // Refresh table
        } else {
            showAlert("Error", "Failed to resolve complaint.", Alert.AlertType.ERROR);
        }
    }

    private void rejectComplaint(Complaint complaint) {
        boolean success = facade.getComplaintService()
                .updateComplaintStatus(complaint.getTenant_id(), complaint.getComplaint_id(), "Rejected", "Complaint was not valid.");

        if (success) {
            showAlert("Rejected", "Complaint has been rejected.", Alert.AlertType.INFORMATION);
            loadComplaints();
        } else {
            showAlert("Error", "Failed to reject complaint.", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}