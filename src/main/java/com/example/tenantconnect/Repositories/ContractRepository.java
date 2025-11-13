package com.example.tenantconnect.Repositories;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ContractRepository {

    private final DB_Handler dbHandler;

    public ContractRepository() {
        this.dbHandler = DB_Handler.getInstance();
        ensureTableExists(); // Create table if it doesn't exist
    }

    // Ensure the table exists
    private void ensureTableExists() {
        if (!dbHandler.tableExists("contracts")) {
            String createTableSQL = """
                CREATE TABLE contracts (
                    contract_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    property_id INTEGER NOT NULL,
                    tenant_id INTEGER NOT NULL,
                    start_date DATE NOT NULL,
                    end_date DATE NOT NULL,
                    monthly_rent DECIMAL(10,2) NOT NULL,
                    security_deposit DECIMAL(10,2),
                    contract_status VARCHAR(20) CHECK(contract_status IN ('pending', 'active', 'terminated', 'rejected')) DEFAULT 'pending',
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (property_id) REFERENCES properties(property_id) ON DELETE CASCADE,
                    FOREIGN KEY (tenant_id) REFERENCES users(user_id) ON DELETE CASCADE
                );
            """;
            dbHandler.executeQuery(createTableSQL);
            System.out.println(" Contracts table created successfully!");
        } else {
            System.out.println("ℹ Contracts table already exists.");
        }
    }

    //  Add new contract
    public boolean addContract(int propertyId, int tenantId, String startDate, String endDate,
                               double monthlyRent, double securityDeposit, String contractStatus) {

        String sql = "INSERT INTO contracts (property_id, tenant_id, start_date, end_date, monthly_rent, security_deposit, contract_status) " +
                "VALUES (" + propertyId + ", " + tenantId + ", '" + startDate + "', '" + endDate + "', " +
                monthlyRent + ", " + securityDeposit + ", '" + contractStatus + "')";
        return dbHandler.executeQuery(sql);
    }

    //  Delete contract by contract_id
    public boolean deleteContract(int contractId) {
        String sql = "DELETE FROM contracts WHERE contract_id = " + contractId;
        return dbHandler.executeQuery(sql);
    }

    //  Get all contracts for a specific tenant
    public List<String> getContractsByTenant(int tenantId) {
        List<String> contracts = new ArrayList<>();
        String sql = "SELECT * FROM contracts WHERE tenant_id = " + tenantId;
        ResultSet rs = dbHandler.executeSelect(sql);
        try {
            while (rs != null && rs.next()) {
                String info = "Contract #" + rs.getInt("contract_id") +
                        " | Property ID: " + rs.getInt("property_id") +
                        " | Rent: " + rs.getDouble("monthly_rent") +
                        " | Status: " + rs.getString("contract_status");
                contracts.add(info);
            }
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contracts;
    }

    //  Get all contracts for a specific property
    public List<String> getContractsByProperty(int propertyId) {
        List<String> contracts = new ArrayList<>();
        String sql = "SELECT * FROM contracts WHERE property_id = " + propertyId;
        ResultSet rs = dbHandler.executeSelect(sql);
        try {
            while (rs != null && rs.next()) {
                String info = "Contract #" + rs.getInt("contract_id") +
                        " | Tenant ID: " + rs.getInt("tenant_id") +
                        " | Rent: " + rs.getDouble("monthly_rent") +
                        " | Status: " + rs.getString("contract_status");
                contracts.add(info);
            }
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contracts;
    }

    //  Get contract by ID
    public String getContractById(int contractId) {
        String sql = "SELECT * FROM contracts WHERE contract_id = " + contractId;
        ResultSet rs = dbHandler.executeSelect(sql);
        try {
            if (rs != null && rs.next()) {
                String result = "Contract #" + rs.getInt("contract_id") +
                        " | Property: " + rs.getInt("property_id") +
                        " | Tenant: " + rs.getInt("tenant_id") +
                        " | Status: " + rs.getString("contract_status");
                rs.close();
                return result;
            }
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //  Update contract status (only if the contract exists)
    public boolean updateContractStatus(String newStatus, int contractId) {
        String sql = "UPDATE contracts SET contract_status = '" + newStatus + "' WHERE contract_id = " + contractId;
        return dbHandler.executeQuery(sql);
    }

    //  Get all active contracts
    public List<String> getActiveContracts() {
        List<String> contracts = new ArrayList<>();
        String sql = "SELECT * FROM contracts WHERE contract_status = 'active'";
        ResultSet rs = dbHandler.executeSelect(sql);
        try {
            while (rs != null && rs.next()) {
                contracts.add("Contract #" + rs.getInt("contract_id") +
                        " | Tenant: " + rs.getInt("tenant_id") +
                        " | Property: " + rs.getInt("property_id"));
            }
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contracts;
    }

    // Optional utility: Get all contracts (for admin/debug)
    public List<String> getAllContracts() {
        List<String> contracts = new ArrayList<>();
        String sql = "SELECT * FROM contracts";
        ResultSet rs = dbHandler.executeSelect(sql);
        try {
            while (rs != null && rs.next()) {
                contracts.add(
                        rs.getInt("contract_id") + " | Property: " +
                                rs.getInt("property_id") + " | Tenant: " +
                                rs.getInt("tenant_id") + " | Status: " +
                                rs.getString("contract_status")
                );
            }
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contracts;
    }
}
