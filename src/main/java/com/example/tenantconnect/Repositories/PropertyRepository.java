package com.example.tenantconnect.Repositories;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PropertyRepository {

    private final DB_Handler dbHandler;

    public PropertyRepository() {
        this.dbHandler = DB_Handler.getInstance();
        ensureTableExists(); // Auto-create the table if not found
    }

    // Create the table if missing
    private void ensureTableExists() {
        if (!dbHandler.tableExists("properties")) {
            String createTableSQL = """
                CREATE TABLE properties (
                    property_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    owner_id INTEGER NOT NULL,
                    property_name VARCHAR(255) NOT NULL,
                    address TEXT NOT NULL,
                    city VARCHAR(100) NOT NULL,
                    state VARCHAR(100),
                    zip_code VARCHAR(20),
                    property_type VARCHAR(50),
                    rent_amount DECIMAL(10,2) NOT NULL,
                    security_deposit DECIMAL(10,2),
                    status VARCHAR(20) CHECK(status IN ('vacant', 'occupied')) DEFAULT 'vacant' NOT NULL,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (owner_id) REFERENCES users(user_id) ON DELETE CASCADE
                );
            """;
            dbHandler.executeQuery(createTableSQL);
            System.out.println("Properties table created successfully!");
        } else {
            System.out.println("ℹProperties table already exists.");
        }
    }

    // Add new property
    public boolean addProperty(int ownerId, String propertyName, String address, String city,
                               String state, String zipCode, String propertyType,
                               double rentAmount, double securityDeposit, String status) {

        String sql = "INSERT INTO properties (owner_id, property_name, address, city, state, zip_code, property_type, rent_amount, security_deposit, status) " +
                "VALUES (" + ownerId + ", '" + propertyName + "', '" + address + "', '" + city + "', '" +
                state + "', '" + zipCode + "', '" + propertyType + "', " + rentAmount + ", " + securityDeposit + ", '" + status + "')";
        return dbHandler.executeQuery(sql);
    }

    // Delete property (only if owner matches)
    public boolean deleteProperty(int propertyId, int ownerId) {
        String sql = "DELETE FROM properties WHERE property_id = " + propertyId + " AND owner_id = " + ownerId;
        return dbHandler.executeQuery(sql);
    }

    // Get all properties by owner ID
    public List<String> getPropertiesByOwnerId(int ownerId) {
        List<String> properties = new ArrayList<>();
        String sql = "SELECT * FROM properties WHERE owner_id = " + ownerId;
        ResultSet rs = dbHandler.executeSelect(sql);
        try {
            while (rs != null && rs.next()) {
                String info = rs.getInt("property_id") + " - " + rs.getString("property_name") +
                        " (" + rs.getString("status") + ") - Rent: " + rs.getDouble("rent_amount");
                properties.add(info);
            }
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return properties;
    }

    // Get property by ID
    public String getPropertyById(int propertyId) {
        String sql = "SELECT * FROM properties WHERE property_id = " + propertyId;
        ResultSet rs = dbHandler.executeSelect(sql);
        try {
            if (rs != null && rs.next()) {
                String result = rs.getInt("property_id") + " - " + rs.getString("property_name") +
                        " (" + rs.getString("city") + ", " + rs.getString("status") + ")";
                rs.close();
                return result;
            }
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Update property status (only if owner matches)
    public boolean updateStatus(String newStatus, int propertyId, int ownerId) {
        // Step 1: Verify owner
        String verifySQL = "SELECT owner_id FROM properties WHERE property_id = " + propertyId;
        ResultSet rs = dbHandler.executeSelect(verifySQL);

        try {
            if (rs != null && rs.next()) {
                int dbOwnerId = rs.getInt("owner_id");
                rs.close();

                if (dbOwnerId == ownerId) {
                    // Step 2: Perform update
                    String updateSQL = "UPDATE properties SET status = '" + newStatus + "' WHERE property_id = " + propertyId;
                    return dbHandler.executeQuery(updateSQL);
                } else {
                    System.out.println("Unauthorized: Owner ID mismatch.");
                }
            }
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Optional utility: Get all properties (admin/debug)
    public List<String> getAllProperties() {
        List<String> properties = new ArrayList<>();
        String sql = "SELECT * FROM properties";
        ResultSet rs = dbHandler.executeSelect(sql);
        try {
            while (rs != null && rs.next()) {
                properties.add(
                        rs.getInt("property_id") + " | " +
                                rs.getString("property_name") + " | " +
                                rs.getString("city") + " | Status: " +
                                rs.getString("status")
                );
            }
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return properties;
    }
}
