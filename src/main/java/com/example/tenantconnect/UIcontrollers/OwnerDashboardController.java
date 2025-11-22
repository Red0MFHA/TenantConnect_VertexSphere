package com.example.tenantconnect.UIcontrollers;

import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.Arrays;
import java.util.List;

public class OwnerDashboardController {

    // FXML Injections
    @FXML private GridPane statsGrid;
    @FXML private AreaChart<String, Number> revenueChart;
    // @FXML private GridPane actionsGrid; // REMOVED: Quick Actions FXML element

    // Instance of the main controller for navigation
    private AppLayoutController appLayoutController;

    /**
     * Sets the main application layout controller for navigation purposes.
     * This is called by AppLayoutController after loading this FXML.
     */
    public void setAppLayoutController(AppLayoutController appLayoutController) {
        this.appLayoutController = appLayoutController;
    }

    /**
     * Initializes the dashboard components after the FXML is loaded.
     */
    @FXML
    public void initialize() {
        populateStatsGrid();
        populateRevenueChart();
        // Removed populateActionsGrid();
    }

    // =========================================================================
    // STATS GRID POPULATION
    // =========================================================================

    private void populateStatsGrid() {
        // Data structure to hold the dashboard statistics
        record DashboardStat(String title, String value, String styleClass) {}

        List<DashboardStat> stats = Arrays.asList(
                new DashboardStat("Total Properties", "24", "stat-blue"),
                new DashboardStat("Occupied", "18", "stat-green"),
                new DashboardStat("Vacant", "6", "stat-yellow"),
                new DashboardStat("Overdue Rent", "$12,450", "stat-red"),
                new DashboardStat("Open Complaints", "7", "stat-purple")
        );

        statsGrid.getChildren().clear();
        for (int i = 0; i < stats.size(); i++) {
            DashboardStat stat = stats.get(i);
            VBox statBox = createStatBox(stat.title(), stat.value(), stat.styleClass());
            // Arrange stats in a single row
            statsGrid.add(statBox, i, 0);
        }
    }

    /**
     * Helper method to create a VBox representing a single statistic card.
     */
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
    // REVENUE CHART POPULATION
    // =========================================================================

    private void populateRevenueChart() {
        // Configure the Area Chart
        revenueChart.setLegendVisible(false);
        revenueChart.getXAxis().setLabel("Month");
        revenueChart.getYAxis().setLabel("Revenue (USD)");

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        // Dummy data for revenue
        series.getData().add(new XYChart.Data<>("Jan", 45000));
        series.getData().add(new XYChart.Data<>("Feb", 52000));
        series.getData().add(new XYChart.Data<>("Mar", 48500));
        series.getData().add(new XYChart.Data<>("Apr", 61000));
        series.getData().add(new XYChart.Data<>("May", 58500));
        series.getData().add(new XYChart.Data<>("Jun", 64000));

        revenueChart.getData().clear();
        revenueChart.getData().add(series);
    }

    // Quick action related methods and structures were removed.
}