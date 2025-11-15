package com.example.tenantconnect.Domain;

public class PaymentExtension {
    private int extension_id;
    private int payment_id;
    private int tenant_id;
    private String current_due_date;
    private String requested_due_date;
    private String reason;
    private String status;
    private String created_at;

    // Getters and Setters
    public int getExtension_id() { return extension_id; }
    public void setExtension_id(int extension_id) { this.extension_id = extension_id; }

    public int getPayment_id() { return payment_id; }
    public void setPayment_id(int payment_id) { this.payment_id = payment_id; }

    public int getTenant_id() { return tenant_id; }
    public void setTenant_id(int tenant_id) { this.tenant_id = tenant_id; }

    public String getCurrent_due_date() { return current_due_date; }
    public void setCurrent_due_date(String current_due_date) { this.current_due_date = current_due_date; }

    public String getRequested_due_date() { return requested_due_date; }
    public void setRequested_due_date(String requested_due_date) { this.requested_due_date = requested_due_date; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreated_at() { return created_at; }
    public void setCreated_at(String created_at) { this.created_at = created_at; }
}
