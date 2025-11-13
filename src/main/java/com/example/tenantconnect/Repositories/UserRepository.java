package com.example.tenantconnect.Repositories;

import com.example.tenantconnect.Repositories.DB_Handler;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    private DB_Handler dbHandler;

    public UserRepository() {
        this.dbHandler = DB_Handler.getInstance(); // get the shared DBHandler
        this.ensureTableExists(); // to ensure that users table exists
    }
    private void ensureTableExists() {
        if (!dbHandler.tableExists("users")) {
            String createTableSQL = """
                CREATE TABLE users (
                    user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    email VARCHAR(255) UNIQUE NOT NULL,
                    password VARCHAR(255) NOT NULL,
                    full_name VARCHAR(100) NOT NULL,
                    user_type VARCHAR(10) CHECK(user_type IN ('owner', 'tenant')) NOT NULL,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    is_active BOOLEAN DEFAULT 1
                );
            """;
            dbHandler.executeQuery(createTableSQL);
            System.out.println("✅ Users table created successfully!");
        } else {
            System.out.println("ℹ️ Users table already exists.");
        }
    }
    public boolean addUser(String email, String password, String fullName, String userType) {
        String sql = "INSERT INTO users(email, password, full_name, user_type) " +
                "VALUES('" + email + "','" + password + "','" + fullName + "','" + userType + "')";
        return dbHandler.executeQuery(sql);
    }
    public boolean deleteUser(int userId) {
        return dbHandler.executeQuery("DELETE FROM users WHERE user_id=" + userId);
    }

    public List<String> getAllUsers() {
        List<String> users = new ArrayList<>();
        ResultSet rs = dbHandler.executeSelect("SELECT * FROM users");
        try {
            while (rs != null && rs.next()) {
                users.add(rs.getInt("user_id") + " - " + rs.getString("email"));
            }
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public boolean tableExists() {
        return dbHandler.tableExists("users");
    }

    public int login(String email, String password) {
        String sql = "SELECT user_id FROM users " +
                "WHERE email='" + email + "' AND password='" + password + "' AND is_active=1";

        ResultSet rs = dbHandler.executeSelect(sql);
        try {
            if (rs != null && rs.next()) {
                int userId = rs.getInt("user_id");
                rs.close();
                return userId; // Found user
            }
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Not found or inactive
    }

    // ===== SELECT BY ID =====
    public String getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id=" + userId;
        ResultSet rs = dbHandler.executeSelect(sql);
        try {
            if (rs != null && rs.next()) {
                String result = rs.getInt("user_id") + " - " + rs.getString("email") +
                        " - " + rs.getString("full_name");
                rs.close();
                return result;
            }
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ===== SELECT BY NAME =====
    public List<String> getUsersByName(String name) {
        List<String> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE full_name LIKE '%" + name + "%'";
        ResultSet rs = dbHandler.executeSelect(sql);
        try {
            while (rs != null && rs.next()) {
                users.add(rs.getInt("user_id") + " - " + rs.getString("full_name"));
            }
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    // ===== SELECT BY EMAIL =====
    public int getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email='" + email + "'";
        ResultSet rs = dbHandler.executeSelect(sql);
        try {
            if (rs != null && rs.next()) {
                int result = rs.getInt("user_id") ;
                rs.close();
                return result;
            }
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // ===== SELECT BY DATE RANGE =====
    public List<String> getUsersByDateRange(String startDate, String endDate) {
        // Expected format: "YYYY-MM-DD"
        List<String> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE date(created_at) BETWEEN '" + startDate + "' AND '" + endDate + "'";
        ResultSet rs = dbHandler.executeSelect(sql);
        try {
            while (rs != null && rs.next()) {
                users.add(rs.getInt("user_id") + " - " + rs.getString("email") +
                        " - " + rs.getString("created_at"));
            }
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
}
