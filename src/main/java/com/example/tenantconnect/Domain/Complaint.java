package com.example.tenantconnect.Domain;

public class Complaint {
    private int complaint_id;
    private int tenant_id;
    private int property_id;
    private String title;
    private String description;
    private String category;
    private String status;
    private String priority;
    private String created_at;
    private String resolved_at;
    private String owner_notes;

    // Getters and Setters
    public int getComplaint_id() { return complaint_id; }
    public void setComplaint_id(int complaint_id) { this.complaint_id = complaint_id; }

    public int getTenant_id() { return tenant_id; }
    public void setTenant_id(int tenant_id) { this.tenant_id = tenant_id; }

    public int getProperty_id() { return property_id; }
    public void setProperty_id(int property_id) { this.property_id = property_id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getCreated_at() { return created_at; }
    public void setCreated_at(String created_at) { this.created_at = created_at; }

    public String getResolved_at() { return resolved_at; }
    public void setResolved_at(String resolved_at) { this.resolved_at = resolved_at; }

    public String getOwner_notes() { return owner_notes; }
    public void setOwner_notes(String owner_notes) { this.owner_notes = owner_notes; }
}
