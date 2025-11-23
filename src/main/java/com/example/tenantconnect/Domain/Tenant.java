package com.example.tenantconnect.Domain;

public class Tenant extends User {

    public int getTenantId() {
        return id;
    }

    public void setTenantId(int id) {
        this.id = id;
    }

    public String getTenantEmail() {
        return email;
    }

    public void setTenantEmail(String email) {
        this.email = email;
    }

    public String getTenantPassword() {
        return password;
    }

    public void setTenantPassword(String password) {
        this.password = password;
    }

    public String getTenantFullName() {
        return full_name;
    }

    public void setTenantFullName(String full_name) {
        this.full_name = full_name;
    }

    public String getTenantUserType() {
        return user_type;
    }

    public void setTenantUserType(String user_type) {
        this.user_type = user_type;
    }

    public String getTenantCreatedAt() {
        return created_at;
    }

    public void setTenantCreatedAt(String created_at) {
        this.created_at = created_at;
    }

    public boolean isTenantActive() {
        return is_active;
    }

    public void setTenantActive(boolean is_active) {
        this.is_active = is_active;
    }
}
