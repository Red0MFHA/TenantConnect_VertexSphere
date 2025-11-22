package com.example.tenantconnect.Repositories;

import com.example.tenantconnect.Domain.Payment;
import com.example.tenantconnect.Domain.PaymentExtension;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.tenantconnect.UIcontrollers.RentTrackingController;

public class PaymentRepository {

    private DB_Handler dbHandler;

    public PaymentRepository() {
        this.dbHandler = DB_Handler.getInstance();
        createPaymentsTable();
    }

    // Create payments table
    private void createPaymentsTable() {
        String sql = "CREATE TABLE IF NOT EXISTS payments (" +
                "payment_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "contract_id INTEGER NOT NULL," +
                "payment_date DATE," +
                "due_date DATE NOT NULL," +
                "amount_due DECIMAL(10,2) NOT NULL," +
                "amount_paid DECIMAL(10,2)," +
                "payment_status VARCHAR(20) CHECK(payment_status IN ('pending', 'paid', 'overdue', 'extension_requested')) DEFAULT 'pending'," +
                "paid_date DATE," +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (contract_id) REFERENCES contracts(contract_id) ON DELETE CASCADE" +
                ");";
        dbHandler.executeQuery(sql);
    }

    // Insert payment
    public void addPayment(Payment p) {
        String sql = "INSERT INTO payments (contract_id, payment_date, due_date, amount_due, amount_paid, payment_status) " +
                "VALUES (" + p.getContract_id() + ", '" + p.getPayment_date() + "', '" + p.getDue_date() + "', " +
                p.getAmount_due() + ", " + p.getAmount_paid() + ", '" + p.getPayment_status() + "');";
        dbHandler.executeQuery(sql);
    }

    // Update payment status or amounts
    public void updatePayment(Payment p) {
        String sql = "UPDATE payments SET payment_date = '" + p.getPayment_date() + "', " +
                "due_date = '" + p.getDue_date() + "', " +
                "amount_due = " + p.getAmount_due() + ", " +
                "amount_paid = " + p.getAmount_paid() + ", " +
                "payment_status = '" + p.getPayment_status() + "' " +
                "WHERE payment_id = " + p.getPayment_id() + ";";
        dbHandler.executeQuery(sql);
    }
    public boolean markPaymentAsPaid(int tenantId, int paymentId) {
        String sql = """
        UPDATE payments 
        SET payment_status = 'paid', 
            paid_date = DATE('now'),
            amount_paid = amount_due
        WHERE payment_id = %d
          AND contract_id IN (
                SELECT contract_id 
                FROM contracts 
                WHERE tenant_id = %d
          );
        """.formatted(paymentId, tenantId);

        try {
            dbHandler.executeQuery(sql);

            // Check if actually updated
            String checkSql = "SELECT payment_status FROM payments WHERE payment_id = " + paymentId;
            ResultSet rs = dbHandler.executeSelect(checkSql);

            if (rs != null && rs.next()) {
                boolean success = rs.getString("payment_status").equals("paid");
                rs.close();
                return success;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public int getOwnerIdByPaymentId(int paymentId) {
        int ownerId = -1;

        try {
            String sql = """
            SELECT p.owner_id
            FROM payments pay
            JOIN contracts c ON pay.contract_id = c.contract_id
            JOIN properties p ON c.property_id = p.property_id
            WHERE pay.payment_id = %d
            """.formatted(paymentId);

            ResultSet rs = dbHandler.executeSelect(sql);

            if (rs != null && rs.next()) {
                ownerId = rs.getInt("owner_id");
            }

            if (rs != null) rs.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ownerId; // returns -1 if not found
    }

    // Delete payment
    public void deletePayment(int paymentId) {
        String sql = "DELETE FROM payments WHERE payment_id = " + paymentId + ";";
        dbHandler.executeQuery(sql);
    }

    // new functioon created for renttracking
    public List<RentTrackingController.RentTableItem> getOwnerRentTrackingData(int ownerId) {
        List<RentTrackingController.RentTableItem> rentData = new ArrayList<>();
        System.out.println("DEBUG: Fetching rent data for owner ID: " + ownerId);
        // SQL joins payments, contracts, properties, and users to get all necessary display info
        String sql = """
SELECT 
    pay.payment_id,
    u.user_id AS tenant_id,
    u.full_name AS tenant_name, 
    pr.property_name,
    pay.due_date,
    pay.amount_due,
    pay.payment_status
FROM payments pay
JOIN contracts c ON pay.contract_id = c.contract_id
JOIN properties pr ON c.property_id = pr.property_id
JOIN users u ON c.tenant_id = u.user_id
WHERE pr.owner_id = 
"""
                + ownerId + // <-- CONCATENATE THE VARIABLE HERE
                """
             ORDER BY pay.due_date ASC;
            """;
        try (ResultSet rs = dbHandler.executeSelect(sql)) {
            while (rs != null && rs.next()) {
                RentTrackingController.RentTableItem item = new RentTrackingController.RentTableItem(
                        rs.getInt("payment_id"),
                        rs.getInt("tenant_id"),
                        rs.getString("tenant_name"),
                        rs.getString("property_name"),
                        rs.getString("due_date"),
                        rs.getFloat("amount_due"),
                        rs.getString("payment_status")
                );
                rentData.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rentData;
    }

    // Retrieve payments by contract
    public List<Payment> getPaymentsByContract(int contractId) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payments WHERE contract_id = " + contractId + " ORDER BY due_date ASC;";
        try (ResultSet rs = dbHandler.executeSelect(sql)) {
            while (rs != null && rs.next()) {
                payments.add(mapResultSetToPayment(rs));
            }
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return payments;
    }

    private Payment mapResultSetToPayment(ResultSet rs) throws SQLException {
        Payment p = new Payment();
        p.setPayment_id(rs.getInt("payment_id"));
        p.setContract_id(rs.getInt("contract_id"));
        p.setPayment_date(rs.getString("payment_date"));
        p.setDue_date(rs.getString("due_date"));
        p.setAmount_due(rs.getFloat("amount_due"));
        p.setAmount_paid(rs.getFloat("amount_paid"));
        p.setPayment_status(rs.getString("payment_status"));
        p.setCreated_at(rs.getString("created_at"));
        return p;
    }


    private void createExtensionsTable() {
        String sql = "CREATE TABLE IF NOT EXISTS payment_extensions (" +
                "extension_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "payment_id INTEGER NOT NULL," +
                "tenant_id INTEGER NOT NULL," +
                "current_due_date DATE NOT NULL," +
                "requested_due_date DATE NOT NULL," +
                "reason TEXT," +
                "status VARCHAR(20) CHECK(status IN ('pending', 'approved', 'rejected')) DEFAULT 'pending'," +
                "responded_at DATETIME," +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (payment_id) REFERENCES payments(payment_id) ON DELETE CASCADE," +
                "FOREIGN KEY (tenant_id) REFERENCES users(user_id) ON DELETE CASCADE" +
                ");";
        dbHandler.executeQuery(sql);
    }

    // Insert extension request
    public void addPaymentExtension(PaymentExtension ext) {
        String sql = "INSERT INTO payment_extensions (payment_id, tenant_id, current_due_date, requested_due_date, reason, status) " +
                "VALUES (" + ext.getPayment_id() + ", " + ext.getTenant_id() + ", '" + ext.getCurrent_due_date() + "', '" +
                ext.getRequested_due_date() + "', '" + ext.getReason() + "', '" + ext.getStatus() + "');";
        dbHandler.executeQuery(sql);
    }

    // Update extension status (approve/reject)
    public void updatePaymentExtensionStatus(int extensionId, String status) {
        String sql = "UPDATE payment_extensions SET status = '" + status + "', responded_at = CURRENT_TIMESTAMP " +
                "WHERE extension_id = " + extensionId + ";";
        dbHandler.executeQuery(sql);
    }
    //update when the extension request is updates
    public void updateExtensionRequest(PaymentExtension pr){
        //updating Status
        String sql = "UPDATE payment_extensions SET status = '"+ pr.getStatus() +"' WHERE extension_id = "+ pr.getExtension_id() + ";";
        dbHandler.executeQuery(sql);
        //updating duedate
        String sql1 = "UPDATE payments SET due_date = '"+pr.getCurrent_due_date()  +"' where payment_id = "+ pr.getPayment_id() + ";" ;
        dbHandler.executeQuery(sql1);
    }
    // Delete extension
    public void deletePaymentExtension(int extensionId) {
        String sql = "DELETE FROM payment_extensions WHERE extension_id = " + extensionId + ";";
        dbHandler.executeQuery(sql);
    }

    // Retrieve extensions for a specific payment
    public List<PaymentExtension> getExtensionsByPayment(int paymentId) {
        List<PaymentExtension> extensions = new ArrayList<>();
        String sql = "SELECT * FROM payment_extensions WHERE payment_id = " + paymentId + " ORDER BY created_at ASC;";
        try (ResultSet rs = dbHandler.executeSelect(sql)) {
            while (rs != null && rs.next()) {
                extensions.add(mapResultSetToExtension(rs));
            }
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return extensions;
    }

    //map for payment extension
    private PaymentExtension mapResultSetToExtension(ResultSet rs) throws SQLException {
        PaymentExtension ext = new PaymentExtension();
        ext.setExtension_id(rs.getInt("extension_id"));
        ext.setPayment_id(rs.getInt("payment_id"));
        ext.setTenant_id(rs.getInt("tenant_id"));
        ext.setCurrent_due_date(rs.getString("current_due_date"));
        ext.setRequested_due_date(rs.getString("requested_due_date"));
        ext.setReason(rs.getString("reason"));
        ext.setStatus(rs.getString("status"));
        ext.setCreated_at(rs.getString("created_at"));
        return ext;
    }

    // Retrieve all due payments for a specific tenant (amount_due > amount_paid or status pending/overdue)
    public List<Payment> getDuePaymentsByTenant(int tenantId) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT p.* FROM payments p " +
                "JOIN contracts c ON p.contract_id = c.contract_id " +
                "WHERE c.tenant_id = " + tenantId + " " +
                "AND (p.payment_status = 'pending' OR p.payment_status = 'overdue');";
        try (ResultSet rs = dbHandler.executeSelect(sql)) {
            while (rs != null && rs.next()) {
                payments.add(mapResultSetToPayment(rs));
            }
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return payments;
    }

    // Retrieve all due payments for properties of a specific owner
    public List<Payment> getDuePaymentsByOwner(int ownerId) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT p.* FROM payments p " +
                "JOIN contracts c ON p.contract_id = c.contract_id " +
                "JOIN properties pr ON c.property_id = pr.property_id " +
                "WHERE pr.owner_id = " + ownerId + " " +
                "AND (p.payment_status = 'pending' OR p.payment_status = 'overdue');";
        try (ResultSet rs = dbHandler.executeSelect(sql)) {
            while (rs != null && rs.next()) {
                payments.add(mapResultSetToPayment(rs));
            }
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return payments;
    }


    // Retrieve all history payments for a specific tenant (amount_due > amount_paid or status pending/overdue)
    public List<Payment> getHistoryPaymentsByTenant(int tenantId) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT p.* FROM payments p " +
                "JOIN contracts c ON p.contract_id = c.contract_id " +
                "WHERE c.tenant_id = " + tenantId +
                ";";
        try (ResultSet rs = dbHandler.executeSelect(sql)) {
            while (rs != null && rs.next()) {
                payments.add(mapResultSetToPayment(rs));
            }
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return payments;
    }

    // Retrieve all due payments for properties of a specific owner
    public List<Payment> getHistoryPaymentsByOwner(int ownerId) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT p.* FROM payments p " +
                "JOIN contracts c ON p.contract_id = c.contract_id " +
                "JOIN properties pr ON c.property_id = pr.property_id " +
                "WHERE pr.owner_id = " + ownerId +
                ";";
        try (ResultSet rs = dbHandler.executeSelect(sql)) {
            while (rs != null && rs.next()) {
                payments.add(mapResultSetToPayment(rs));
            }
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return payments;
    }
    // Retrieve unresolved payment extensions for a specific tenant
    public List<PaymentExtension> getUnresolvedExtensionsByTenant(int tenantId) {
        List<PaymentExtension> extensions = new ArrayList<>();
        String sql = "SELECT * FROM payment_extensions " +
                "WHERE tenant_id = " + tenantId + " AND status = 'pending' " +
                "ORDER BY created_at ASC;";
        try (ResultSet rs = dbHandler.executeSelect(sql)) {
            while (rs != null && rs.next()) {
                extensions.add(mapResultSetToExtension(rs));
            }
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return extensions;
    }

    // Retrieve unresolved payment extensions for all payments belonging to properties of a specific owner
    public List<PaymentExtension> getUnresolvedExtensionsByOwner(int ownerId) {
        List<PaymentExtension> extensions = new ArrayList<>();
        String sql = "SELECT pe.* FROM payment_extensions pe " +
                "JOIN payments p ON pe.payment_id = p.payment_id " +
                "JOIN contracts c ON p.contract_id = c.contract_id " +
                "JOIN properties pr ON c.property_id = pr.property_id " +
                "WHERE pr.owner_id = " + ownerId + " AND pe.status = 'pending' " +
                "ORDER BY pe.created_at ASC;";
        try (ResultSet rs = dbHandler.executeSelect(sql)) {
            while (rs != null && rs.next()) {
                extensions.add(mapResultSetToExtension(rs));
            }
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return extensions;
    }
    public PaymentExtension getPaymentExtensionByExtensionID(int extensionID) {
        String sql = "SELECT * FROM payment_extensions " +
                "WHERE extension_id = " + extensionID + " LIMIT 1;";

        try (ResultSet rs = dbHandler.executeSelect(sql)) {
            if (rs != null && rs.next()) {
                PaymentExtension extension = mapResultSetToExtension(rs);
                rs.close();
                return extension;   // Return single object
            }
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;  // Return null if not found
    }
}
