package com.example.tenantconnect.Services;

import com.example.tenantconnect.Domain.DashboardData;
import com.example.tenantconnect.Repositories.DashboardRepository;
import javafx.scene.chart.XYChart;
import java.util.List;

public class DashboardService {
    private DashboardRepository dashboardRepository;

    public DashboardService() {
        this.dashboardRepository = new DashboardRepository();
    }

    public DashboardData getStats(int ownerId) {
        return dashboardRepository.getOwnerStats(ownerId);
    }

    public List<XYChart.Data<String, Number>> getRevenueChartData(int ownerId) {
        // Fetch data for the last 6 months
        return dashboardRepository.getMonthlyRevenue(ownerId, 6);
    }
}