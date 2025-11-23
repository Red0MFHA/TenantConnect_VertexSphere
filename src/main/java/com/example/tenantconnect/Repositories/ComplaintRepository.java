package com.example.tenantconnect.Repositories;

import com.example.tenantconnect.Domain.Complaint;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;import com.example.tenantconnect.UIcontrollers.ComplaintsController;

public class ComplaintRepository {

    private DB_Handler dbHandler;

    public ComplaintRepository() {
        this.dbHandler = DB_Handler.getInstance();
        createComplaintsTable();
    }

    // ===== Table creation =====
    private void createComplaintsTable() {
        String sql = "CREATE TABLE IF NOT EXISTS complaints (" +
                "complaint_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "tenant_id INTEGER NOT NULL," +
                "property_id INTEGER NOT NULL," +
                "title VARCHAR(255) NOT NULL," +
                "description TEXT NOT NULL," +
                "category VARCHAR(50)," +
                "status VARCHAR(20) CHECK(status IN ('open', 'in_progress', 'resolved', 'rejected')) DEFAULT 'open'," +
                "priority VARCHAR(20) CHECK(priority IN ('low', 'medium', 'high', 'urgent')) DEFAULT 'medium'," +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "resolved_at DATETIME," +
                "owner_notes TEXT," +
                "FOREIGN KEY (tenant_id) REFERENCES users(user_id) ON DELETE CASCADE," +
                "FOREIGN KEY (property_id) REFERENCES properties(property_id) ON DELETE CASCADE" +
                ");";
        dbHandler.executeQuery(sql);
    }

    // ===== CRUD operations =====
    public void addComplaint(Complaint c) {
        String sql = "INSERT INTO complaints (tenant_id, property_id, title, description, category, status, priority, owner_notes) " +
                "VALUES (" + c.getTenant_id() + ", " + c.getProperty_id() + ", '" + c.getTitle() + "', '" +
                c.getDescription() + "', '" + c.getCategory() + "', '" + c.getStatus() + "', '" +
                c.getPriority() + "', '" + c.getOwner_notes() + "');";
        dbHandler.executeQuery(sql);
    }

    public void updateComplaint(Complaint c) {
        String sql = "UPDATE complaints SET title = '" + c.getTitle() + "', description = '" + c.getDescription() + "', " +
                "category = '" + c.getCategory() + "', status = '" + c.getStatus() + "', " +
                "priority = '" + c.getPriority() + "', resolved_at = '" + c.getResolved_at() + "', " +
                "owner_notes = '" + c.getOwner_notes() + "' " +
                "WHERE complaint_id = " + c.getComplaint_id() + ";";
        dbHandler.executeQuery(sql);
    }
    public List<ComplaintsController.ComplaintTableItem> getOwnerComplaintTrackingData(int ownerId) {
        List<ComplaintsController.ComplaintTableItem> complaintData = new ArrayList<>();
        System.out.println("ownerId: " + ownerId);
        // We join tenants (users) and properties to the complaints table.
        String sql = """
    SELECT 
        comp.complaint_id,
        u.full_name AS tenant_name, 
        pr.property_name,
        comp.category,
        comp.title,
        comp.created_at AS date,
        comp.status
    FROM complaints comp
    JOIN properties pr ON comp.property_id = pr.property_id
    JOIN users u ON comp.tenant_id = u.user_id
    WHERE pr.owner_id = %d
    ORDER BY comp.created_at DESC;
    """.formatted(ownerId); // Assuming you are now using .formatted() successfully

        try (ResultSet rs = dbHandler.executeSelect(sql)) {
            while (rs != null && rs.next()) {
                // Note: date format conversion might be needed later, but using String for now.
                ComplaintsController.ComplaintTableItem item = new ComplaintsController.ComplaintTableItem(
                        rs.getInt("complaint_id"),
                        rs.getString("tenant_name"),
                        rs.getString("property_name"),
                        rs.getString("category"),
                        rs.getString("title"),
                        rs.getString("date").substring(0, 10), // Truncate date for table view (e.g., 'YYYY-MM-DD')
                        rs.getString("status")
                );
                complaintData.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return complaintData;
    }
    // You will also need a method to get the Tenant ID for a notification after updating status.
    public int getTenantIdByComplaintId(int complaintId) {
        String sql = "SELECT tenant_id FROM complaints WHERE complaint_id = " + complaintId;
        try (ResultSet rs = dbHandler.executeSelect(sql)) {
            if (rs != null && rs.next()) {
                return rs.getInt("tenant_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    public boolean updateStatus(int complaintId, String newStatus){
        String sql = "UPDATE complaints SET  status = '" + newStatus + "' WHERE complaint_id = " + complaintId + ";";
        return dbHandler.executeQuery(sql);
    }

    public void deleteComplaint(int complaintId) {
        String sql = "DELETE FROM complaints WHERE complaint_id = " + complaintId + ";";
        dbHandler.executeQuery(sql);
    }

    public List<Complaint> getComplaintsByTenant(int tenantId) {
        List<Complaint> complaints = new ArrayList<>();
        String sql = "SELECT * FROM complaints WHERE tenant_id = " + tenantId + " ORDER BY created_at DESC;";
        try (ResultSet rs = dbHandler.executeSelect(sql)) {
            while (rs != null && rs.next()) {
                complaints.add(mapResultSetToComplaint(rs));
            }
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return complaints;
    }

    public List<Complaint> getComplaintsByOwner(int ownerId) {
        List<Complaint> complaints = new ArrayList<>();
        String sql = "SELECT comp.* FROM complaints comp " +
                "JOIN properties p ON comp.property_id = p.property_id " +
                "WHERE p.owner_id = " + ownerId + " ORDER BY comp.created_at DESC;";
        try (ResultSet rs = dbHandler.executeSelect(sql)) {
            while (rs != null && rs.next()) {
                complaints.add(mapResultSetToComplaint(rs));
            }
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return complaints;
    }

    // ===== Retrieve unresolved complaints =====
    public List<Complaint> getUnresolvedComplaintsByTenant(int tenantId) {
        List<Complaint> complaints = new ArrayList<>();
        String sql = "SELECT * FROM complaints WHERE tenant_id = " + tenantId + " " +
                "AND status != 'resolved' AND status != 'rejected' ORDER BY created_at ASC;";
        try (ResultSet rs = dbHandler.executeSelect(sql)) {
            while (rs != null && rs.next()) {
                complaints.add(mapResultSetToComplaint(rs));
            }
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return complaints;
    }

    public List<Complaint> getUnresolvedComplaintsByOwner(int ownerId) {
        List<Complaint> complaints = new ArrayList<>();
        String sql = "SELECT comp.* FROM complaints comp " +
                "JOIN properties p ON comp.property_id = p.property_id " +
                "WHERE p.owner_id = " + ownerId + " AND comp.status != 'resolved' AND comp.status != 'rejected' " +
                "ORDER BY comp.created_at ASC;";
        try (ResultSet rs = dbHandler.executeSelect(sql)) {
            while (rs != null && rs.next()) {
                complaints.add(mapResultSetToComplaint(rs));
            }
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return complaints;
    }

    // ===== Mapping =====
    private Complaint mapResultSetToComplaint(ResultSet rs) throws SQLException {
        Complaint c = new Complaint();
        c.setComplaint_id(rs.getInt("complaint_id"));
        c.setTenant_id(rs.getInt("tenant_id"));
        c.setProperty_id(rs.getInt("property_id"));
        c.setTitle(rs.getString("title"));
        c.setDescription(rs.getString("description"));
        c.setCategory(rs.getString("category"));
        c.setStatus(rs.getString("status"));
        c.setPriority(rs.getString("priority"));
        c.setCreated_at(rs.getString("created_at"));
        c.setResolved_at(rs.getString("resolved_at"));
        c.setOwner_notes(rs.getString("owner_notes"));
        return c;
    }
}

