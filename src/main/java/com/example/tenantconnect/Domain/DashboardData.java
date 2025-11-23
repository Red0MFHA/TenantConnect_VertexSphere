 package com.example.tenantconnect.Domain;
public class DashboardData {
    private int totalProperties;
    private int occupiedProperties;
    private int vacantProperties;
    private double overdueRentAmount;
    private int openComplaints;

    // Optional: revenue data map/list can be added here later

    // Constructor, Getters, and Setters (Implement these)
    // ...

    // Example constructor for simplicity
    public DashboardData(int totalProperties, int occupiedProperties, int vacantProperties, double overdueRentAmount, int openComplaints) {
        this.totalProperties = totalProperties;
        this.occupiedProperties = occupiedProperties;
        this.vacantProperties = vacantProperties;
        this.overdueRentAmount = overdueRentAmount;
        this.openComplaints = openComplaints;
    }

    public int getTotalProperties() { return totalProperties; }
    public int getOccupiedProperties() { return occupiedProperties; }
    public int getVacantProperties() { return vacantProperties; }
    public double getOverdueRentAmount() { return overdueRentAmount; }
    public int getOpenComplaints() { return openComplaints; }

    // No setters needed if data is calculated by the service/repo
}