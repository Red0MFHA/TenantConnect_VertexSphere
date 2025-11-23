package com.example.tenantconnect.UIcontrollers;

import com.example.tenantconnect.Domain.DashboardData;
import com.example.tenantconnect.Services.DashboardService;
import com.example.tenantconnect.Services.FacadeClass;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OwnerDashboardController {

    // FXML Injections
    @FXML private GridPane statsGrid;
    @FXML private AreaChart<String, Number> revenueChart;

    private DashboardService dashboardService;
    private final int CURRENT_OWNER_ID = FacadeClass.CURRENT_USER_ID;

    // ... (setAppLayoutController method) ...

    /**
     * Initializes the dashboard components after the FXML is loaded.
     */
    @FXML
    public void initialize() {
        dashboardService = FacadeClass.getInstance().getDashboardService();
        populateStatsGrid();
        populateRevenueChart();
    }
    // Insert this method into OwnerDashboardController.java
    private VBox createStatBox(String title, String value, String styleClass) {
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("stat-title");

        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("stat-value");

        VBox box = new VBox(5, titleLabel, valueLabel);
        box.getStyleClass().addAll("stat-card", styleClass);
        // Set fixed width for better layout consistency
        box.setPrefWidth(200);
        return box;
    }

    // =========================================================================
    // STATS GRID POPULATION
    // =========================================================================

    private void populateStatsGrid() {
        // Fetch real data from the service
        DashboardData data = dashboardService.getStats(CURRENT_OWNER_ID);

        // Data structure to hold the dashboard statistics
        record DashboardStat(String title, String value, String styleClass) {}

        // Format currency for display
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);

        List<DashboardStat> stats = List.of( // Use List.of for immutable list
                new DashboardStat("Total Properties", String.valueOf(data.getTotalProperties()), "stat-blue"),
                new DashboardStat("Occupied", String.valueOf(data.getOccupiedProperties()), "stat-green"),
                new DashboardStat("Vacant", String.valueOf(data.getVacantProperties()), "stat-yellow"),
                new DashboardStat("Overdue Rent", currencyFormatter.format(data.getOverdueRentAmount()), "stat-red"),
                new DashboardStat("Open Complaints", String.valueOf(data.getOpenComplaints()), "stat-purple")
        );

        statsGrid.getChildren().clear();
        for (int i = 0; i < stats.size(); i++) {
            DashboardStat stat = stats.get(i);
            VBox statBox = createStatBox(stat.title(), stat.value(), stat.styleClass());
            statsGrid.add(statBox, i, 0);
        }
    }

    // ... (createStatBox helper method) ...


    // =========================================================================
    // REVENUE CHART POPULATION
    // =========================================================================

    private void populateRevenueChart() {
        // Configure the Area Chart
        revenueChart.setLegendVisible(false);
        // Note: The X and Y axis labels are already set in the FXML

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        // Fetch real revenue data from the service
        List<XYChart.Data<String, Number>> chartData = dashboardService.getRevenueChartData(CURRENT_OWNER_ID);

        // Add fetched data to the series
        for (XYChart.Data<String, Number> dataPoint : chartData) {
            series.getData().add(dataPoint);
        }

        revenueChart.getData().clear();
        revenueChart.getData().add(series);
    }
}