package com.example.tenantconnect.Tenant;

import com.example.tenantconnect.Domain.Payment;
import com.example.tenantconnect.Domain.PaymentExtension;
import com.example.tenantconnect.Domain.Property;
import com.example.tenantconnect.Repositories.DB_Handler;
import com.example.tenantconnect.Services.FacadeClass;
import com.example.tenantconnect.controllers.TenantController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestExtensionController extends TenantController {

    private final FacadeClass facade = FacadeClass.getInstance();
    private final DB_Handler db = DB_Handler.getInstance();

    @FXML private ComboBox<PropertyWithPayment> propertyComboBox;
    @FXML private Label currentDueDateLabel;
    @FXML private DatePicker requestedDatePicker;
    @FXML private TextArea reasonTextArea;
    @FXML private Button submitButton;

    // Helper class to hold Property + Payment info
    private static class PropertyWithPayment {
        Property property;
        Payment payment;
        PropertyWithPayment(Property p, Payment pay) {
            this.property = p;
            this.payment = pay;
        }
        @Override
        public String toString() {
            return property.getProperty_name() + " - " + property.getAddress();
        }
    }

    private Payment selectedPayment;

    // //////////////////////////////////////////
    // Controller  "Navigate to Request Extension" //////
    // //////////////////////////////////////////
    @FXML
    private void initialize() {
        findDuePayment();
        propertyComboBox.setOnAction(e -> updateDueDateDisplay());
    }

    private void findDuePayment() {

        // //////////////////////////////////////////
        // Payment  "find Due Payment" //////
        // //////////////////////////////////////////

        List<Payment> pendingPayments = facade.getPaymentService()
                .getDuePaymentsForTenant(FacadeClass.CURRENT_USER_ID);

        var items = FXCollections.<PropertyWithPayment>observableArrayList();
        Map<Integer, Property> propertyCache = new HashMap<>();

        for (Payment payment : pendingPayments) {
            int contractId = payment.getContract_id();

            // Get property from cache or DB
            Property prop = propertyCache.get(contractId);
            if (prop == null) {
                prop = getPropertyByContractId(contractId);
                if (prop != null) {
                    propertyCache.put(contractId, prop);
                }
            }

            if (prop != null) {
                items.add(new PropertyWithPayment(prop, payment));
            }
        }

        propertyComboBox.setItems(items);

        propertyComboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(PropertyWithPayment item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.toString());
            }
        });

        propertyComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(PropertyWithPayment item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Select property..." : item.toString());
            }
        });

        if (items.isEmpty()) {
            currentDueDateLabel.setText("No pending rent");
            propertyComboBox.setPromptText("No properties with pending rent");
            propertyComboBox.setDisable(true);
        }
    }

    private Property getPropertyByContractId(int contractId) {
        String sql = """
        SELECT p.property_id, p.property_name, p.address
        FROM properties p
        JOIN contracts c ON p.property_id = c.property_id
        WHERE c.contract_id = ?
        """;

        // Manually replace the ? with the actual contractId
        String finalSql = sql.replace("?", String.valueOf(contractId));

        try (ResultSet rs = db.executeSelect(finalSql)) {
            if (rs != null && rs.next()) {
                Property p = new Property();
                p.setProperty_id(rs.getInt("property_id"));
                p.setProperty_name(rs.getString("property_name"));
                p.setAddress(rs.getString("address"));
                return p;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void updateDueDateDisplay() {
        PropertyWithPayment selected = propertyComboBox.getValue();
        if (selected == null) {
            currentDueDateLabel.setText("Select property above");
            selectedPayment = null;
            submitButton.setDisable(true);
            return;
        }

        selectedPayment = selected.payment;
        LocalDate due = LocalDate.parse(selectedPayment.getDue_date());
        currentDueDateLabel.setText(due.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")));
        submitButton.setDisable(false);
    }

    // //////////////////////////////////////////
    // Tenant Controller "submitExtensionRequest" //////
    // //////////////////////////////////////////
    @FXML
    public void submitExtensionRequest() {
        if (selectedPayment == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a property.", ButtonType.OK).showAndWait();
            return;
        }

        LocalDate newDate = requestedDatePicker.getValue();
        String reason = reasonTextArea.getText().trim();

        if (newDate == null || !newDate.isAfter(LocalDate.now())) {
            new Alert(Alert.AlertType.WARNING, "New date must be in the future.", ButtonType.OK).showAndWait();
            return;
        }

        if (reason.isEmpty() || reason.length() < 15) {
            new Alert(Alert.AlertType.WARNING, "Please provide a detailed reason (min 15 characters).", ButtonType.OK).showAndWait();
            return;
        }

        PaymentExtension ext = new PaymentExtension();
        ext.setPayment_id(selectedPayment.getPayment_id());
        ext.setTenant_id(FacadeClass.CURRENT_USER_ID);
        ext.setCurrent_due_date(selectedPayment.getDue_date());
        ext.setRequested_due_date(newDate.toString());
        ext.setReason(reason);
        ext.setStatus("pending");

        // //////////////////////////////////////////
        // Payment Service "Request Extension" //////
        // //////////////////////////////////////////
        facade.getPaymentService().requestExtension(FacadeClass.CURRENT_USER_ID, ext);

        new Alert(Alert.AlertType.INFORMATION,
                "Extension request sent successfully!\n\n" +
                        "Property: " + propertyComboBox.getValue().property.getProperty_name() + "\n" +
                        "Current Due: " + selectedPayment.getDue_date() + "\n" +
                        "Requested: " + newDate + "\n\n" +
                        "Your landlord has been notified.",
                ButtonType.OK).showAndWait();

        clearForm();
    }

    @FXML private void onCancel() { clearForm(); }

    private void clearForm() {
        propertyComboBox.getSelectionModel().clearSelection();
        requestedDatePicker.setValue(null);
        reasonTextArea.clear();
        currentDueDateLabel.setText("Select property above");
        submitButton.setDisable(true);
    }
}