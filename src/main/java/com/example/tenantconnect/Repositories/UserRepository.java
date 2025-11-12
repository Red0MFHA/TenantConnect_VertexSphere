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
}
