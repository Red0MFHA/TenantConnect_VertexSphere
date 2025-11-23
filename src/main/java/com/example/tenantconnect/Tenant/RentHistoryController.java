package com.example.tenantconnect.Tenant;

import com.example.tenantconnect.Domain.Payment;
import com.example.tenantconnect.Services.FacadeClass;
import com.example.tenantconnect.controllers.TenantController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.sql.ResultSet;

public class RentHistoryController extends TenantController {

    private final FacadeClass facade = FacadeClass.getInstance();

    @FXML private TableView<Payment> rentTable;
    @FXML private TableColumn<Payment, String> monthCol;
    @FXML private TableColumn<Payment, String> dueDateCol;
    @FXML private TableColumn<Payment, String> amountCol;
    @FXML private TableColumn<Payment, String> statusCol;
    @FXML private TableColumn<Payment, String> paidDateCol;
    @FXML private TableColumn<Payment, String> propertyCol;
    @FXML private Label emptyLabel;

    private final DateTimeFormatter monthFmt = DateTimeFormatter.ofPattern("MMMM yyyy");
    private final DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd MMM yyyy");

    @FXML
    private void initialize() {
        System.out.println("RentHistoryController: initialize() called");
        System.out.println("CURRENT_USER_ID = " + FacadeClass.CURRENT_USER_ID);

        setupColumns();
        loadRentHistory();
    }

    private void setupColumns() {
        System.out.println("Setting up table columns...");

        monthCol.setCellValueFactory(cellData -> {
            Payment p = cellData.getValue();
            System.out.println("Month column: Payment ID=" + p.getPayment_id() + ", Due Date=" + p.getDue_date());
            try {
                LocalDate due = LocalDate.parse(p.getDue_date());
                return javafx.beans.binding.Bindings.createStringBinding(() -> due.format(monthFmt));
            } catch (Exception e) {
                System.out.println("ERROR parsing due_date: " + p.getDue_date());
                return javafx.beans.binding.Bindings.createStringBinding(() -> "Invalid Date");
            }
        });

        dueDateCol.setCellValueFactory(cellData -> {
            try {
                LocalDate due = LocalDate.parse(cellData.getValue().getDue_date());
                return javafx.beans.binding.Bindings.createStringBinding(() -> due.format(dateFmt));
            } catch (Exception e) {
                return javafx.beans.binding.Bindings.createStringBinding(() -> "Invalid");
            }
        });

        amountCol.setCellValueFactory(cellData ->
                javafx.beans.binding.Bindings.createStringBinding(() ->
                        "Rs." + String.format("%,.2f", cellData.getValue().getAmount_due()))
        );

        statusCol.setCellValueFactory(cellData -> {
            String s = cellData.getValue().getPayment_status();
            System.out.println("Status for payment " + cellData.getValue().getPayment_id() + " = " + s);
            String display = switch (s) {
                case "paid" -> "Paid";
                case "pending" -> "Pending";
                case "overdue" -> "Overdue";
                default -> "Unknown(" + s + ")";
            };
            return javafx.beans.binding.Bindings.createStringBinding(() -> display);
        });

        paidDateCol.setCellValueFactory(cellData -> {
            String dateStr = cellData.getValue().getPayment_date();
            System.out.println("Paid date raw: '" + dateStr + "'");
            if (dateStr == null || dateStr.trim().isEmpty()) {
                return javafx.beans.binding.Bindings.createStringBinding(() -> "Not Paid");
            }
            try {
                LocalDate paid = LocalDate.parse(dateStr.substring(0, 10));
                return javafx.beans.binding.Bindings.createStringBinding(() -> paid.format(dateFmt));
            } catch (Exception e) {
                System.out.println("ERROR parsing payment_date: " + dateStr);
                return javafx.beans.binding.Bindings.createStringBinding(() -> "Invalid");
            }
        });

        propertyCol.setCellValueFactory(cellData ->
                javafx.beans.binding.Bindings.createStringBinding(() -> {
                    System.out.println("Fetching property for contract_id = " + cellData.getValue().getContract_id());
                    return getPropertyName(cellData.getValue().getContract_id());
                })
        );
    }

    private void loadRentHistory() {
        System.out.println("\n=== LOAD RENT HISTORY START ===");
        System.out.println("Fetching rent history for tenant ID: " + FacadeClass.CURRENT_USER_ID);

        var payments = facade.getPaymentService().getRentHistory(FacadeClass.CURRENT_USER_ID, "tenant");

        System.out.println("getRentHistory() returned " + (payments == null ? "NULL" : payments.size()) + " payments");

        if (payments == null || payments.isEmpty()) {
            System.out.println("NO PAYMENTS FOUND — showing empty state");
            emptyLabel.setText("No payment history found");
            emptyLabel.setVisible(true);
            rentTable.setVisible(false);
            return;
        }

        System.out.println("Payments found: " + payments.size());
        for (Payment p : payments) {
            System.out.println("Payment ID: " + p.getPayment_id() +
                    ", Contract ID: " + p.getContract_id() +
                    ", Due: " + p.getDue_date() +
                    ", Amount: " + p.getAmount_due() +
                    ", Status: " + p.getPayment_status() +
                    ", Paid Date: '" + p.getPayment_date() + "'");
        }

        rentTable.setItems(FXCollections.observableArrayList(payments));
        emptyLabel.setVisible(false);
        rentTable.setVisible(true);
        System.out.println("Table items set. Total rows: " + rentTable.getItems().size());
        System.out.println("=== LOAD RENT HISTORY END ===\n");
    }

    private String getPropertyName(int contractId) {
        System.out.println("getPropertyName() called for contract_id = " + contractId);
        String sql = "SELECT p.property_name FROM properties p " +
                "JOIN contracts c ON p.property_id = c.property_id " +
                "WHERE c.contract_id = " + contractId;

        try {
            ResultSet rs = com.example.tenantconnect.Repositories.DB_Handler.getInstance().executeSelect(sql);
            if (rs != null && rs.next()) {
                String name = rs.getString("property_name");
                System.out.println("Property found: " + name);
                rs.close();
                return name;
            } else {
                System.out.println("No property found for contract_id = " + contractId);
            }
            if (rs != null) rs.close();
        } catch (Exception e) {
            System.out.println("SQL ERROR in getPropertyName: " + e.getMessage());
            e.printStackTrace();
        }
        return "Unknown Property";
    }
}