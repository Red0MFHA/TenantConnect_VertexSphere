package com.example.tenantconnect.Tenant;

import com.example.tenantconnect.Domain.Payment;
import com.example.tenantconnect.Services.FacadeClass;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RentStatusController {

    private final FacadeClass facade = FacadeClass.getInstance();

    @FXML private TableView<Payment> rentStatusTable;
    @FXML private TableColumn<Payment, String> monthCol;
    @FXML private TableColumn<Payment, String> amountCol;
    @FXML private TableColumn<Payment, String> dueDateCol;
    @FXML private TableColumn<Payment, String> paidDateCol;
    @FXML private TableColumn<Payment, String> statusCol;
    @FXML private TableColumn<Payment, String> propertyCol;

    private final DateTimeFormatter monthFmt = DateTimeFormatter.ofPattern("MMMM yyyy");
    private final DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd MMM yyyy");

    @FXML
    private void initialize() {
        setupColumns();
        loadRentStatus();
    }

    private void setupColumns() {
        monthCol.setCellValueFactory(cellData -> {
            LocalDate due = LocalDate.parse(cellData.getValue().getDue_date());
            return javafx.beans.binding.Bindings.createStringBinding(() -> due.format(monthFmt));
        });

        amountCol.setCellValueFactory(cellData ->
                javafx.beans.binding.Bindings.createStringBinding(() ->
                        "Rs " + String.format("%,.0f", cellData.getValue().getAmount_due()))
        );

        dueDateCol.setCellValueFactory(cellData -> {
            LocalDate due = LocalDate.parse(cellData.getValue().getDue_date());
            return javafx.beans.binding.Bindings.createStringBinding(() -> due.format(dateFmt));
        });

        paidDateCol.setCellValueFactory(cellData -> {
            String dateStr = cellData.getValue().getPayment_date();
            if (dateStr == null || dateStr.trim().isEmpty()) {
                return javafx.beans.binding.Bindings.createStringBinding(() -> "Not Paid");
            }
            try {
                LocalDate paid = LocalDate.parse(dateStr.substring(0, 10));
                return javafx.beans.binding.Bindings.createStringBinding(() -> paid.format(dateFmt));
            } catch (Exception e) {
                return javafx.beans.binding.Bindings.createStringBinding(() -> "Not Paid");
            }
        });

        statusCol.setCellValueFactory(cellData -> {
            String s = cellData.getValue().getPayment_status();
            String display = switch (s) {
                case "paid" -> "Paid";
                case "pending" -> "Pending";
                case "overdue" -> "Overdue";
                default -> s;
            };
            return javafx.beans.binding.Bindings.createStringBinding(() -> display);
        });

        propertyCol.setCellValueFactory(cellData ->
                javafx.beans.binding.Bindings.createStringBinding(() ->
                        getPropertyName(cellData.getValue().getContract_id()))
        );
    }

    private void loadRentStatus() {
        var payments = facade.getPaymentService()
                .getDuePaymentsForTenant(FacadeClass.CURRENT_USER_ID);  // Only current due ones

        rentStatusTable.setItems(FXCollections.observableArrayList(payments));
    }

    private String getPropertyName(int contractId) {
        String sql = "SELECT p.property_name FROM properties p " +
                "JOIN contracts c ON p.property_id = c.property_id " +
                "WHERE c.contract_id = " + contractId;

        try (var rs = com.example.tenantconnect.Repositories.DB_Handler.getInstance().executeSelect(sql)) {
            if (rs != null && rs.next()) {
                return rs.getString("property_name");
            }
        } catch (Exception ignored) {}
        return "Unknown Property";
    }
}