package com.example.tenantconnect.Tenant;

import com.example.tenantconnect.Domain.Complaint;
import com.example.tenantconnect.Domain.Property;
import com.example.tenantconnect.Repositories.DB_Handler;
import com.example.tenantconnect.Services.FacadeClass;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.ResultSet;
import java.time.LocalDateTime;

public class FileComplaintController {

    private final FacadeClass facade = FacadeClass.getInstance();
    private final DB_Handler db = DB_Handler.getInstance();

    @FXML private ComboBox<Property> propertyComboBox;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private TextField titleTextField;
    @FXML private TextArea descriptionTextArea;

    @FXML
    private void initialize() {
        loadTenantProperties();
        setupCategories();
    }

    private void loadTenantProperties() {
        String sql = """
            SELECT DISTINCT p.property_id, p.property_name, p.address
            FROM properties p
            JOIN contracts c ON p.property_id = c.property_id
            WHERE c.tenant_id = %d AND c.contract_status = 'active'
            ORDER BY p.property_name
            """.formatted(FacadeClass.CURRENT_USER_ID);

        var list = FXCollections.<Property>observableArrayList();

        try (ResultSet rs = db.executeSelect(sql)) {
            while (rs != null && rs.next()) {
                Property p = new Property();
                p.setProperty_id(rs.getInt("property_id"));
                p.setProperty_name(rs.getString("property_name"));
                p.setAddress(rs.getString("address"));
                list.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        propertyComboBox.setItems(list);

        // Show nice text instead of "Property@abc123"
        propertyComboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Property p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty || p == null ? null : p.getProperty_name() + " - " + p.getAddress());
            }
        });

        propertyComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Property p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty || p == null ? "Select property..." : p.getProperty_name() + " - " + p.getAddress());
            }
        });

        if (list.isEmpty()) {
            propertyComboBox.setPromptText("No active properties found");
            propertyComboBox.setDisable(true);
        }
    }

    private void setupCategories() {
        categoryComboBox.getItems().addAll(
                "Maintenance", "Plumbing", "Electrical", "Noise",
                "Cleanliness", "Security", "Billing Issue", "Other"
        );
        categoryComboBox.setValue("Maintenance");
    }

    @FXML
    private void onSubmitComplaint() {
        Property selectedProperty = propertyComboBox.getValue();

        if (selectedProperty == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a property", ButtonType.OK).show();
            return;
        }

        String title = titleTextField.getText().trim();
        String desc = descriptionTextArea.getText().trim();
        String cat = categoryComboBox.getValue();

        if (title.isEmpty() || desc.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Title and description are required", ButtonType.OK).show();
            return;
        }

        Complaint c = new Complaint();
        c.setTenant_id(FacadeClass.CURRENT_USER_ID);
        c.setProperty_id(selectedProperty.getProperty_id());
        c.setTitle(title);
        c.setDescription(desc);
        c.setCategory(cat);
        c.setStatus("open");
        c.setPriority("medium");
        c.setCreated_at(LocalDateTime.now().toString());

        boolean success = facade.getComplaintService().FileComplaint(FacadeClass.CURRENT_USER_ID, c);

        if (success) {
            new Alert(Alert.AlertType.INFORMATION, "Complaint submitted successfully!", ButtonType.OK).showAndWait();
            clearForm();
        } else {
            new Alert(Alert.AlertType.ERROR, "Failed to submit complaint. Try again.", ButtonType.OK).showAndWait();
        }
    }

    @FXML
    private void onCancel() {
        clearForm();
    }

    private void clearForm() {
        propertyComboBox.getSelectionModel().clearSelection();
        titleTextField.clear();
        descriptionTextArea.clear();
        categoryComboBox.setValue("Maintenance");
    }
}