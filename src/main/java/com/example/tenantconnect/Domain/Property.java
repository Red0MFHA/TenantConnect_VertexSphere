package com.example.tenantconnect.Domain;

public class Property {
    int property_id ;
    int owner_id;
    String property_name ;
    String address;
    String city;
    String state ;
    String zip_code ;
    String property_type;
    double rent_amount ;
    double security_deposit ;
    String status ;
    String created_at;


    public Property() {}

    // --- Getters and Setters ---
    public int getProperty_id() { return property_id; }
    public void setProperty_id(int property_id) { this.property_id = property_id; }

    public int getOwner_id() { return owner_id; }
    public void setOwner_id(int owner_id) { this.owner_id = owner_id; }

    public String getProperty_name() { return property_name; }
    public void setProperty_name(String property_name) { this.property_name = property_name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getZip_code() { return zip_code; }
    public void setZip_code(String zip_code) { this.zip_code = zip_code; }

    public String getProperty_type() { return property_type; }
    public void setProperty_type(String property_type) { this.property_type = property_type; }

    public double getRent_amount() { return rent_amount; }
    public void setRent_amount(double rent_amount) { this.rent_amount = rent_amount; }

    public double getSecurity_deposit() { return security_deposit; }
    public void setSecurity_deposit(double security_deposit) { this.security_deposit = security_deposit; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreated_at() { return created_at; }
    public void setCreated_at(String created_at) { this.created_at = created_at; }
}
