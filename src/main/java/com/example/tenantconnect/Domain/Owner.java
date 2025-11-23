package com.example.tenantconnect.Domain;

public class Owner extends User {

    public int getOwnerId() {
        return id;
    }

    public void setOwnerId(int id) {
        this.id = id;
    }

    public String getOwnerEmail() {
        return email;
    }

    public void setOwnerEmail(String email) {
        this.email = email;
    }

    public String getOwnerPassword() {
        return password;
    }

    public void setOwnerPassword(String password) {
        this.password = password;
    }

    public String getOwnerFullName() {
        return full_name;
    }

    public void setOwnerFullName(String full_name) {
        this.full_name = full_name;
    }

    public String getOwnerUserType() {
        return user_type;
    }

    public void setOwnerUserType(String user_type) {
        this.user_type = user_type;
    }

    public String getOwnerCreatedAt() {
        return created_at;
    }

    public void setOwnerCreatedAt(String created_at) {
        this.created_at = created_at;
    }

    public boolean isOwnerActive() {
        return is_active;
    }

    public void setOwnerActive(boolean is_active) {
        this.is_active = is_active;
    }
}
