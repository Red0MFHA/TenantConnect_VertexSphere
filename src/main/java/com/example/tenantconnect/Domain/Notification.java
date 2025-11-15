package com.example.tenantconnect.Domain;

public class Notification {

    private int notification_id;
    private int user_id;
    private String title;
    private String message;
    private String notification_type;
    private boolean is_read;
    private String related_entity_type;
    private int related_entity_id;
    private String created_at;

    // Getters and Setters
    public int getNotification_id() {
        return notification_id;
    }

    public void setNotification_id(int notification_id) {
        this.notification_id = notification_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNotification_type() {
        return notification_type;
    }

    public void setNotification_type(String notification_type) {
        this.notification_type = notification_type;
    }

    public boolean isIs_read() {
        return is_read;
    }

    public void setIs_read(boolean is_read) {
        this.is_read = is_read;
    }

    public String getRelated_entity_type() {
        return related_entity_type;
    }

    public void setRelated_entity_type(String related_entity_type) {
        this.related_entity_type = related_entity_type;
    }

    public int getRelated_entity_id() {
        return related_entity_id;
    }

    public void setRelated_entity_id(int related_entity_id) {
        this.related_entity_id = related_entity_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
