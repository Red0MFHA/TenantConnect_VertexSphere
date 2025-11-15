package com.example.tenantconnect.Domain;

public class Contract {

    private int contract_id;
    private int property_id;
    private int tenant_id;
    private String start_date;
    private String end_date;
    private double monthly_rent;
    private double security_deposit;
    private String contract_status;
    private String created_at;

    public Contract() {}

    // Getters and Setters
    public int getContract_id() { return contract_id; }
    public void setContract_id(int contract_id) { this.contract_id = contract_id; }

    public int getProperty_id() { return property_id; }
    public void setProperty_id(int property_id) { this.property_id = property_id; }

    public int getTenant_id() { return tenant_id; }
    public void setTenant_id(int tenant_id) { this.tenant_id = tenant_id; }

    public String getStart_date() { return start_date; }
    public void setStart_date(String start_date) { this.start_date = start_date; }

    public String getEnd_date() { return end_date; }
    public void setEnd_date(String end_date) { this.end_date = end_date; }

    public double getMonthly_rent() { return monthly_rent; }
    public void setMonthly_rent(double monthly_rent) { this.monthly_rent = monthly_rent; }

    public double getSecurity_deposit() { return security_deposit; }
    public void setSecurity_deposit(double security_deposit) { this.security_deposit = security_deposit; }

    public String getContract_status() { return contract_status; }
    public void setContract_status(String contract_status) { this.contract_status = contract_status; }

    public String getCreated_at() { return created_at; }
    public void setCreated_at(String created_at) { this.created_at = created_at; }
}
