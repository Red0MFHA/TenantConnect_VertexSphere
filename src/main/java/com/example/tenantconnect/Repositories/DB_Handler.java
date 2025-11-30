package com.example.tenantconnect.Repositories;

import java.sql.*;

public class DB_Handler {

    private static final String DB_URL = "jdbc:sqlite:data/app.db";
    private Connection connection;
    private DB_Handler(){}
    //applying singleton
    private static DB_Handler instance;
    public static DB_Handler getInstance() {
        if (instance == null) {
            instance = new DB_Handler();
            instance.openConnection();
        }
        return instance;
    }
    // Open connection
    private void openConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL);
                System.out.println("Database connected!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Close connection
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Execute INSERT, UPDATE, DELETE, CREATE TABLE, etc.
    public boolean executeQuery(String sql) {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Execute SELECT query
    public ResultSet executeSelect(String sql) {
        try {
            Statement stmt = connection.createStatement();
            return stmt.executeQuery(sql); // Caller must close ResultSet
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Check if a table exists
    public boolean tableExists(String tableName) {
        try {
            DatabaseMetaData meta = connection.getMetaData();
            ResultSet res = meta.getTables(null, null, tableName, new String[]{"TABLE"});
            boolean exists = res.next();
            res.close();
            return exists;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}