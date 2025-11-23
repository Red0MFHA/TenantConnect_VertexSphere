package com.example.tenantconnect.Repositories;

import com.example.tenantconnect.Domain.DashboardData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.chart.XYChart;

public class DashboardRepository {

    private DB_Handler dbHandler; // Assume DB_Handler is the class for database access

    public DashboardRepository() {
        this.dbHandler = DB_Handler.getInstance(); // Use singleton instance
    }

    /**
     * Fetches all aggregated statistics for the dashboard of a specific owner.
     */
    public DashboardData getOwnerStats(int ownerId) {
        // 1. Property Counts
        String propertiesSql = "SELECT " +
                "(SELECT COUNT(property_id) FROM properties WHERE owner_id = " + ownerId + ") AS Total, " +
                "(SELECT COUNT(property_id) FROM properties WHERE owner_id = " + ownerId + " AND status = 'occupied') AS Occupied, " +
                "(SELECT COUNT(property_id) FROM properties WHERE owner_id = " + ownerId + " AND status = 'vacant') AS Vacant;";

        // 2. Overdue Rent
        // Joins payments to contracts to properties to filter by owner_id
        String rentSql = "SELECT IFNULL(SUM(p.amount_due - IFNULL(p.amount_paid, 0)), 0) AS OverdueAmount " +
                "FROM payments p " +
                "JOIN contracts c ON p.contract_id = c.contract_id " +
                "JOIN properties pr ON c.property_id = pr.property_id " +
                "WHERE pr.owner_id = " + ownerId + " AND p.payment_status = 'overdue';";

        // 3. Open Complaints
        String complaintsSql = "SELECT COUNT(complaint_id) AS OpenComplaints " +
                "FROM complaints c " +
                "JOIN properties pr ON c.property_id = pr.property_id " +
                "WHERE pr.owner_id = " + ownerId + " AND c.status = 'open';";

        int total = 0, occupied = 0, vacant = 0, openComplaints = 0;
        double overdueRent = 0.0;

        try (ResultSet rs = dbHandler.executeSelect(propertiesSql)) {
            if (rs != null && rs.next()) {
                total = rs.getInt("Total");
                occupied = rs.getInt("Occupied");
                vacant = rs.getInt("Vacant");
            }
        } catch (SQLException e) { e.printStackTrace(); }

        try (ResultSet rs = dbHandler.executeSelect(rentSql)) {
            if (rs != null && rs.next()) {
                // Ensure correct handling of Decimal/Double type
                overdueRent = rs.getDouble("OverdueAmount");
            }
        } catch (SQLException e) { e.printStackTrace(); }

        try (ResultSet rs = dbHandler.executeSelect(complaintsSql)) {
            if (rs != null && rs.next()) {
                openComplaints = rs.getInt("OpenComplaints");
            }
        } catch (SQLException e) { e.printStackTrace(); }

        return new DashboardData(total, occupied, vacant, overdueRent, openComplaints);
    }

    /**
     * Fetches monthly revenue data for the chart (Paid Payments).
     * This is more complex and will be simplified for this example.
     */
    public List<XYChart.Data<String, Number>> getMonthlyRevenue(int ownerId, int months) {
        List<XYChart.Data<String, Number>> revenueData = new ArrayList<>();
        // In a real DB, you'd aggregate SUM(p.amount_paid) GROUP BY month

        // Mocked aggregation structure for simplicity based on expected output
        // You MUST replace this with a proper SQL aggregation query later.
        // Example SQL (complex for SQLite/plain SQL):
        /*
        SELECT
            strftime('%Y-%m', paid_date) as Month,
            SUM(amount_paid) as Revenue
        FROM payments p
        JOIN contracts c ON p.contract_id = c.contract_id
        JOIN properties pr ON c.property_id = pr.property_id
        WHERE pr.owner_id = ? AND p.payment_status = 'paid'
        GROUP BY Month
        ORDER BY Month DESC
        LIMIT ?;
        */

        // Placeholder data structure to match the chart's expected XYChart.Data
        revenueData.add(new XYChart.Data<>("Jun", 15000));
        revenueData.add(new XYChart.Data<>("Jul", 18000));
        revenueData.add(new XYChart.Data<>("Aug", 16500));
        revenueData.add(new XYChart.Data<>("Sep", 20000));
        revenueData.add(new XYChart.Data<>("Oct", 19500));
        revenueData.add(new XYChart.Data<>("Nov", 21000));

        return revenueData;
    }
}