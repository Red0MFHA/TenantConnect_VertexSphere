package com.example.tenantconnect.Repositories;

import com.example.tenantconnect.Domain.Notification;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NotificationRepository {

    private DB_Handler dbHandler;

    public NotificationRepository() {
        this.dbHandler = DB_Handler.getInstance();
        createNotificationsTable();
        createSettingsTable();
    }

    // ====== Table creation ======
    private void createNotificationsTable() {
        String sql = "CREATE TABLE IF NOT EXISTS notifications (" +
                "notification_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER NOT NULL," +
                "title VARCHAR(255) NOT NULL," +
                "message TEXT NOT NULL," +
                "notification_type VARCHAR(50)," +
                "is_read BOOLEAN DEFAULT 0," +
                "related_entity_type VARCHAR(50)," +
                "related_entity_id INTEGER," +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE" +
                ");";
        dbHandler.executeQuery(sql);
    }

    private void createSettingsTable() {
        String sql = "CREATE TABLE IF NOT EXISTS notification_settings (" +
                "setting_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "owner_id INTEGER NOT NULL," +
                "notification_type VARCHAR(50) NOT NULL," +
                "is_enabled BOOLEAN DEFAULT 1," +
                "frequency_hours INTEGER DEFAULT 24," +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (owner_id) REFERENCES users(user_id) ON DELETE CASCADE," +
                "UNIQUE(owner_id, notification_type)" +
                ");";
        dbHandler.executeQuery(sql);
    }

    // ====== Notification functions ======
    public void addNotification(Notification n) {
        String sql = "INSERT INTO notifications (user_id, title, message, notification_type, is_read, related_entity_type, related_entity_id) " +
                "VALUES (" + n.getUser_id() + ", '" + n.getTitle() + "', '" + n.getMessage() + "', '" + n.getNotification_type() + "', " +
                (n.isIs_read() ? 1 : 0) + ", '" + n.getRelated_entity_type() + "', " + n.getRelated_entity_id() + ");";
        dbHandler.executeQuery(sql);
    }

    public void markAsRead(int notificationId) {
        String sql = "UPDATE notifications SET is_read = 1 WHERE notification_id = " + notificationId + ";";
        dbHandler.executeQuery(sql);
    }

    public void deleteNotification(int notificationId) {
        String sql = "DELETE FROM notifications WHERE notification_id = " + notificationId + ";";
        dbHandler.executeQuery(sql);
    }

    public List<Notification> getNotificationsByUser(int userId) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id = " + userId + " ORDER BY created_at DESC;";
        try (ResultSet rs = dbHandler.executeSelect(sql)) {
            while (rs != null && rs.next()) {
                notifications.add(mapResultSetToNotification(rs));
            }
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notifications;
    }

    public List<Notification> getUnreadNotificationsByUser(int userId) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id = " + userId + " AND is_read = 0 ORDER BY created_at DESC;";
        try (ResultSet rs = dbHandler.executeSelect(sql)) {
            while (rs != null && rs.next()) {
                notifications.add(mapResultSetToNotification(rs));
            }
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notifications;
    }

    private Notification mapResultSetToNotification(ResultSet rs) throws SQLException {
        Notification n = new Notification();
        n.setNotification_id(rs.getInt("notification_id"));
        n.setUser_id(rs.getInt("user_id"));
        n.setTitle(rs.getString("title"));
        n.setMessage(rs.getString("message"));
        n.setNotification_type(rs.getString("notification_type"));
        n.setIs_read(rs.getBoolean("is_read"));
        n.setRelated_entity_type(rs.getString("related_entity_type"));
        n.setRelated_entity_id(rs.getInt("related_entity_id"));
        n.setCreated_at(rs.getString("created_at"));
        return n;
    }

    // ====== Notification Settings functions ======

    // Check if a notification type is enabled for a specific owner
    public boolean isNotificationEnabled(int ownerId, String notificationType) {
        boolean enabled = false;
        String sql = "SELECT is_enabled FROM notification_settings WHERE owner_id = " + ownerId +
                " AND notification_type = '" + notificationType + "';";
        try (ResultSet rs = dbHandler.executeSelect(sql)) {
            if (rs != null && rs.next()) {
                enabled = rs.getBoolean("is_enabled");
            }
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return enabled;
    }

    // Add a new notification setting
    public void addNotificationSetting(int ownerId, String notificationType, boolean isEnabled, int frequencyHours) {
        String sql = "INSERT OR IGNORE INTO notification_settings (owner_id, notification_type, is_enabled, frequency_hours) " +
                "VALUES (" + ownerId + ", '" + notificationType + "', " + (isEnabled ? 1 : 0) + ", " + frequencyHours + ");";
        dbHandler.executeQuery(sql);
    }

    // Retrieve all settings for an owner as strings
    public List<String> getNotificationSettingsByOwner(int ownerId) {
        List<String> settings = new ArrayList<>();
        String sql = "SELECT notification_type, is_enabled, frequency_hours FROM notification_settings " +
                "WHERE owner_id = " + ownerId + " ORDER BY created_at DESC;";
        try (ResultSet rs = dbHandler.executeSelect(sql)) {
            while (rs != null && rs.next()) {
                String setting = "Type: " + rs.getString("notification_type") +
                        ", Enabled: " + rs.getBoolean("is_enabled") +
                        ", Frequency(Hours): " + rs.getInt("frequency_hours");
                settings.add(setting);
            }
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return settings;
    }
}
