package com.example.tenantconnect.Tenant;

import com.example.tenantconnect.Domain.Property;
import com.example.tenantconnect.Repositories.DB_Handler;
import com.example.tenantconnect.Services.FacadeClass;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.sql.ResultSet;

public class PayRentController {

    private final FacadeClass facade = FacadeClass.getInstance();
    private final DB_Handler db = DB_Handler.getInstance();

    @FXML private ComboBox<PropertyWithPending> propertyComboBox;
    @FXML private VBox paymentBox;
    @FXML private Label amountLabel;
    @FXML private Label dueLabel;

    // Tiny record just for this page
    private record PropertyWithPending(Property property, int paymentId, double amount, String dueDate) {}

    @FXML
    private void initialize() {
        loadPropertiesWithPendingRent();

        propertyComboBox.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                amountLabel.setText("Rs" + String.format("%,.2f", selected.amount()));
                dueLabel.setText("Due on: " + selected.dueDate());
                paymentBox.setVisible(true);
            } else {
                paymentBox.setVisible(false);
            }
        });
    }

    private void loadPropertiesWithPendingRent() {
        String sql = """
            SELECT DISTINCT 
                p.property_id, p.property_name, p.address,
                pay.payment_id, pay.amount_due, pay.due_date
            FROM properties p
            JOIN contracts c ON p.property_id = c.property_id
            JOIN payments pay ON pay.contract_id = c.contract_id
            WHERE c.tenant_id = %d 
              AND c.contract_status = 'active'
              AND pay.payment_status IN ('pending', 'overdue')
            ORDER BY pay.due_date ASC
            """.formatted(FacadeClass.CURRENT_USER_ID);

        ObservableList<PropertyWithPending> list = FXCollections.observableArrayList();

        try (ResultSet rs = db.executeSelect(sql)) {
            while (rs != null && rs.next()) {
                Property p = new Property();
                p.setProperty_id(rs.getInt("property_id"));
                p.setProperty_name(rs.getString("property_name"));
                p.setAddress(rs.getString("address"));

                list.add(new PropertyWithPending(
                        p,
                        rs.getInt("payment_id"),
                        rs.getDouble("amount_due"),
                        rs.getString("due_date")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        propertyComboBox.setItems(list);

        // Beautiful dropdown display
        propertyComboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(PropertyWithPending item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.property().getProperty_name() + " - " + item.property().getAddress() +
                            " | ₹" + String.format("%,.0f", item.amount()) + " due " + item.dueDate());
                }
            }
        });

        // If nothing to pay
        if (list.isEmpty()) {
            propertyComboBox.setPromptText("No pending rent – You're all paid!");
        } else {
            propertyComboBox.setPromptText("Choose rent to pay...");
        }
    }

    @FXML
    private void handlePayRent() {
        PropertyWithPending selected = propertyComboBox.getValue();
        if (selected == null) return;

        boolean success = facade.getPaymentService()
                .updatePaymentToPaid(FacadeClass.CURRENT_USER_ID, selected.paymentId());

        if (success) {
            new Alert(Alert.AlertType.INFORMATION,
                    "Payment Successful! ₹%,.2f paid.".formatted(selected.amount()),
                    ButtonType.OK).showAndWait();
            loadPropertiesWithPendingRent();  // refresh list
            paymentBox.setVisible(false);
        } else {
            new Alert(Alert.AlertType.ERROR, "Payment failed. Try again.", ButtonType.OK).showAndWait();
        }
    }
}