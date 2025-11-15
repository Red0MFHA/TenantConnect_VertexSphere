package com.example.tenantconnect.Domain;

public class Payment {
    private int payment_id;
    private int contract_id;
    private String payment_date;
    private String due_date;
    private float amount_due;
    private float amount_paid;
    private String payment_status;
    private String created_at;

    // Getters and Setters
    public int getPayment_id() { return payment_id; }
    public void setPayment_id(int payment_id) { this.payment_id = payment_id; }

    public int getContract_id() { return contract_id; }
    public void setContract_id(int contract_id) { this.contract_id = contract_id; }

    public String getPayment_date() { return payment_date; }
    public void setPayment_date(String payment_date) { this.payment_date = payment_date; }

    public String getDue_date() { return due_date; }
    public void setDue_date(String due_date) { this.due_date = due_date; }

    public float getAmount_due() { return amount_due; }
    public void setAmount_due(float amount_due) { this.amount_due = amount_due; }

    public float getAmount_paid() { return amount_paid; }
    public void setAmount_paid(float amount_paid) { this.amount_paid = amount_paid; }

    public String getPayment_status() { return payment_status; }
    public void setPayment_status(String payment_status) { this.payment_status = payment_status; }

    public String getCreated_at() { return created_at; }
    public void setCreated_at(String created_at) { this.created_at = created_at; }
}
