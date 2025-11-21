package com.example.tenantconnect.Repositories;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.example.tenantconnect.Domain.Contract;
import com.example.tenantconnect.Domain.PropertyAssignment;
public class ContractRepository {

    private final DB_Handler dbHandler;

    public ContractRepository() {
        this.dbHandler = DB_Handler.getInstance();
        ensureTableExists(); // Create table if it doesn't exist
    }

    // Ensure the table exists
    // Ensure the necessary tables exist
    private void ensureTableExists() {

        // --- CONTRACTS TABLE ---
        if (!dbHandler.tableExists("contracts")) {
            String createContractsTable = """
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
            dbHandler.executeQuery(createContractsTable);
            System.out.println("Contracts table created.");
        }

        // --- PROPERTY_ASSIGNMENTS TABLE ---
        if (!dbHandler.tableExists("property_assignments")) {
            String createPA = """
            CREATE TABLE property_assignments (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                owner_id INTEGER NOT NULL,
                property_id INTEGER NOT NULL,
                property_name TEXT NOT NULL,
                tenant_id INTEGER,
                contract_id INTEGER,
                
                FOREIGN KEY (owner_id) REFERENCES users(user_id) ON DELETE CASCADE,
                FOREIGN KEY (property_id) REFERENCES properties(property_id) ON DELETE CASCADE,
                FOREIGN KEY (tenant_id) REFERENCES users(user_id) ON DELETE SET NULL,
                FOREIGN KEY (contract_id) REFERENCES contracts(contract_id) ON DELETE CASCADE
            );
        """;

            dbHandler.executeQuery(createPA);
            System.out.println("Property Assignments table created.");
        }
    }


    //  Add new contract
    public boolean addContract(int propertyId, int tenantId, String startDate, String endDate,
                               double monthlyRent, double securityDeposit, String contractStatus) {

        try {
            // 1. Check if property exists
            String checkPropertySQL = "SELECT * FROM properties WHERE property_id = " + propertyId;
            ResultSet rsProperty = dbHandler.executeSelect(checkPropertySQL);

            if (rsProperty == null || !rsProperty.next()) {
                System.out.println("Property not found!");
                return false;
            }

            int ownerId = rsProperty.getInt("owner_id");
            String propertyName = rsProperty.getString("property_name");
            rsProperty.close();

            // 2. Check if tenant exists
            String checkTenantSQL = "SELECT * FROM users WHERE user_id = " + tenantId;
            ResultSet rsTenant = dbHandler.executeSelect(checkTenantSQL);

            if (rsTenant == null || !rsTenant.next()) {
                System.out.println("Tenant not found!");
                return false;
            }
            rsTenant.close();


            // 3. Insert contract
            String contractSQL =
                    "INSERT INTO contracts (property_id, tenant_id, start_date, end_date, monthly_rent, security_deposit, contract_status) " +
                            "VALUES (" + propertyId + ", " + tenantId + ", '" + startDate + "', '" + endDate + "', " +
                            monthlyRent + ", " + securityDeposit + ", '" + contractStatus + "')";

            boolean contractInserted = dbHandler.executeQuery(contractSQL);

            if (!contractInserted) {
                System.out.println("Failed to create contract!");
                return false;
            }


            // 4. Get the created contract ID
            ResultSet rsId = dbHandler.executeSelect("SELECT last_insert_rowid() AS id");
            int contractId = -1;

            if (rsId != null && rsId.next()) {
                contractId = rsId.getInt("id");
            }
            rsId.close();

            if (contractId == -1) {
                System.out.println("Could not retrieve contract ID!");
                return false;
            }


            // 5. Insert into property_assignments
            String assignmentSQL =
                    "INSERT INTO property_assignments (owner_id, property_id, property_name, tenant_id, contract_id) " +
                            "VALUES (" + ownerId + ", " + propertyId + ", '" + propertyName + "', " +
                            tenantId + ", " + contractId + ")";

            boolean assignmentInserted = dbHandler.executeQuery(assignmentSQL);

            if (!assignmentInserted) {
                System.out.println("Contract added but property assignment failed!");
                return false;
            }

            System.out.println("Contract and Property Assignment added successfully!");
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    //  Delete contract by contract_id
    public boolean deleteContract(int contractId) {
        Contract contract = this.getContractById(contractId);
        if(contract == null) return false;
        if(contract.getContract_status().equals("active")) return false;
        String sql = "DELETE FROM contracts WHERE contract_id = " + contractId;
        return dbHandler.executeQuery(sql);
    }

    //MAP for contract
    private Contract mapToContract(ResultSet rs) throws SQLException {
        Contract c = new Contract();
        c.setContract_id(rs.getInt("contract_id"));
        c.setProperty_id(rs.getInt("property_id"));
        c.setTenant_id(rs.getInt("tenant_id"));
        c.setStart_date(rs.getString("start_date"));
        c.setEnd_date(rs.getString("end_date"));
        c.setMonthly_rent(rs.getDouble("monthly_rent"));
        c.setSecurity_deposit(rs.getDouble("security_deposit"));
        c.setContract_status(rs.getString("contract_status"));
        c.setCreated_at(rs.getString("created_at"));
        return c;
    }
    private PropertyAssignment map(ResultSet rs) throws SQLException {
        PropertyAssignment pa = new PropertyAssignment();
        pa.id = rs.getInt("id");
        pa.ownerId = rs.getInt("owner_id");
        pa.propertyId = rs.getInt("property_id");
        pa.propertyName = rs.getString("property_name");
        pa.tenantId = rs.getInt("tenant_id");
        pa.contractId = rs.getInt("contract_id");
        return pa;
    }

    //  Get all contracts for a specific tenant
    public List<Contract> getContractsByTenant(int tenantId) {
        List<Contract> contracts = new ArrayList<>();
        String sql = "SELECT * FROM contracts WHERE tenant_id = " + tenantId;
        ResultSet rs = dbHandler.executeSelect(sql);

        try {
            while (rs != null && rs.next()) {
                contracts.add(mapToContract(rs));
            }
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contracts;
    }
    public List<Contract> getPendingContractsByTenant(int tenantId) {
        List<Contract> contracts = new ArrayList<>();
        String sql = "SELECT * FROM contracts WHERE tenant_id = " + tenantId+"AND contract_status = "+"pending";
        ResultSet rs = dbHandler.executeSelect(sql);

        try {
            while (rs != null && rs.next()) {
                contracts.add(mapToContract(rs));
            }
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contracts;
    }

    //  Get all contracts for a specific property
    public List<Contract> getContractsByProperty(int propertyId) {
        List<Contract> contracts = new ArrayList<>();
        String sql = "SELECT * FROM contracts WHERE property_id = " + propertyId;
        ResultSet rs = dbHandler.executeSelect(sql);

        try {
            while (rs != null && rs.next()) {
                contracts.add(mapToContract(rs));
            }
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contracts;
    }

    public List<Contract> getContractsByPropertyAndTenant(int propertyId,int tenantId) {
        List<Contract> contracts = new ArrayList<>();
        String sql = "SELECT * FROM contracts WHERE property_id = " + propertyId + " AND tenant_id = " + tenantId;
        ResultSet rs = dbHandler.executeSelect(sql);

        try {
            while (rs != null && rs.next()) {
                contracts.add(mapToContract(rs));
            }
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contracts;
    }
    //  Get contract by ID
    public Contract getContractById(int contractId) {
        String sql = "SELECT * FROM contracts WHERE contract_id = " + contractId;
        ResultSet rs = dbHandler.executeSelect(sql);

        try {
            if (rs != null && rs.next()) {
                Contract c = mapToContract(rs);
                rs.close();
                return c;
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
    public boolean updateContract(Contract contract) {
        if (contract == null) {
            System.out.println("Contract object is null!");
            return false;
        }

        int contractId = contract.getContract_id();

        // Safety: Check if contract exists
        String checkSQL = "SELECT contract_id FROM contracts WHERE contract_id = " + contractId;
        ResultSet rs = dbHandler.executeSelect(checkSQL);

        try {
            if (rs == null || !rs.next()) {
                System.out.println("Contract not found with ID: " + contractId);
                return false;
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        // Build SQL update query
        String sql = "UPDATE contracts SET " +
                "property_id = " + contract.getProperty_id() + ", " +
                "tenant_id = " + contract.getTenant_id() + ", " +
                "start_date = '" + contract.getStart_date() + "', " +
                "end_date = '" + contract.getEnd_date() + "', " +
                "monthly_rent = " + contract.getMonthly_rent() + ", " +
                "security_deposit = " + contract.getSecurity_deposit() + ", " +
                "contract_status = '" + contract.getContract_status() + "' " +
                "WHERE contract_id = " + contractId;

        boolean success = dbHandler.executeQuery(sql);

        if (success) {
            System.out.println("Contract updated successfully!");
        } else {
            System.out.println("Failed to update contract!");
        }

        return success;
    }

    //  Get all active contracts
    public List<Contract> getActiveContracts() {
        List<Contract> contracts = new ArrayList<>();
        String sql = "SELECT * FROM contracts WHERE contract_status = 'active'";
        ResultSet rs = dbHandler.executeSelect(sql);

        try {
            while (rs != null && rs.next()) {
                contracts.add(mapToContract(rs));
            }
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contracts;
    }
    public int getOwnerIdByContractId(int contractId) {
        int ownerId = -1;

        try {
            String sql = """
            SELECT p.owner_id 
            FROM contracts c
            JOIN properties p ON c.property_id = p.property_id
            WHERE c.contract_id = %d
            """.formatted(contractId);

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

    // Optional utility: Get all contracts (for admin/debug)
    public List<Contract> getAllContracts() {
        List<Contract> contracts = new ArrayList<>();
        String sql = "SELECT * FROM contracts";
        ResultSet rs = dbHandler.executeSelect(sql);

        try {
            while (rs != null && rs.next()) {
                contracts.add(mapToContract(rs));
            }
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contracts;
    }

    public int getAssignmentId(int ownerId, int tenantId) {
        int assignmentId = -1;
        try {
            String sql = "SELECT id FROM property_assignments " +
                    "WHERE owner_id = " + ownerId + " AND tenant_id = " + tenantId;

            ResultSet rs = dbHandler.executeSelect(sql);

            if (rs != null && rs.next()) {
                assignmentId = rs.getInt("id");
            }

            if (rs != null) rs.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return assignmentId;
    }


    public List<PropertyAssignment> getAssignmentsByOwnerId(int ownerId) {
        List<PropertyAssignment> list = new ArrayList<>();

        String sql = "SELECT * FROM property_assignments WHERE owner_id = " + ownerId;
        ResultSet rs = dbHandler.executeSelect(sql);

        try {
            while (rs != null && rs.next()) {
                list.add(map(rs));
            }
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Contract> getContractByOwner(int owner_id){
        List<Contract> contracts = new ArrayList<>();
        String sql = "SELECT * FROM contracts c join properties p on c.property_id=p.property_id WHERE p.owner_id = " + owner_id+";";
        ResultSet rs = dbHandler.executeSelect(sql);

        try {
            while (rs != null && rs.next()) {
                contracts.add(mapToContract(rs));
            }
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contracts;
    }

    public List<PropertyAssignment> getAssignmentsByTenantId(int tenantId) {
        List<PropertyAssignment> list = new ArrayList<>();

        String sql = "SELECT * FROM property_assignments WHERE tenant_id = " + tenantId;
        ResultSet rs = dbHandler.executeSelect(sql);

        try {
            while (rs != null && rs.next()) {
                list.add(map(rs));
            }
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
    public List<PropertyAssignment> getAssignmentsByPropertyId(int propertyId) {
        List<PropertyAssignment> list = new ArrayList<>();

        String sql = "SELECT * FROM property_assignments WHERE property_id = " + propertyId;
        ResultSet rs = dbHandler.executeSelect(sql);

        try {
            while (rs != null && rs.next()) {
                list.add(map(rs));
            }
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

}
