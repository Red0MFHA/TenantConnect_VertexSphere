package com.example.tenantconnect.UIcontrollers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Alert;
public class OwnerDashboardController {

    // FXML Bindings
    @FXML private GridPane statsGrid;
    @FXML private AreaChart<String, Number> revenueChart;
    @FXML private GridPane actionsGrid;

    // --- Data Classes (Simplified) ---
    private static class StatData {
        String label;
        String value;
        String colorClass;
        String iconSymbol; // Used for simple text icon

        public StatData(String label, String value, String colorClass, String iconSymbol) {
            this.label = label;
            this.value = value;
            this.colorClass = colorClass;
            this.iconSymbol = iconSymbol;
        }
    }

    private static class ActionData {
        String label;
        String colorClass;
        String iconSymbol;
        String actionId;

        public ActionData(String label, String colorClass, String iconSymbol, String actionId) {
            this.label = label;
            this.colorClass = colorClass;
            this.iconSymbol = iconSymbol;
            this.actionId = actionId;
        }
    }

    // --- Mock Data ---
    private final StatData[] statsData = new StatData[] {
            new StatData("Total Properties", "24", "bg-primary-blue", "P"),
            new StatData("Occupied", "18", "bg-green", "U"),
            new StatData("Vacant", "6", "bg-gray", "V"),
            new StatData("Overdue Rent", "$12,450", "bg-red", "$"),
            new StatData("Open Complaints", "7", "bg-orange", "!")
    };

    private final ActionData[] quickActions = new ActionData[] {
            new ActionData("Add Property", "bg-primary-blue", "+", "add-property"),
            new ActionData("Assign Tenant", "bg-orange", "T", "assign-tenant"),
            new ActionData("View Reports", "bg-green", "R", "view-reports"),
            new ActionData("Send Reminder", "bg-yellow text-dark", "B", "send-reminder")
    };

    private final XYChart.Data<String, Number>[] chartDataPoints = new XYChart.Data[] {
            new XYChart.Data<>("Jan", 45000),
            new XYChart.Data<>("Feb", 52000),
            new XYChart.Data<>("Mar", 48000),
            new XYChart.Data<>("Apr", 61000),
            new XYChart.Data<>("May", 58000),
            new XYChart.Data<>("Jun", 65000)
    };

    @FXML
    public void initialize() {
        populateStatsGrid();
        setupRevenueChart();
        populateActionsGrid();
    }

    // --- Stats Grid Population ---
    private void populateStatsGrid() {
        for (int i = 0; i < statsData.length; i++) {
            StatData stat = statsData[i];

            // Icon Container (p-3 rounded-lg)
            Label icon = new Label(stat.iconSymbol);
            icon.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");
            icon.getStyleClass().add(stat.colorClass); // Custom CSS class for background color
            icon.setPadding(new Insets(8));
            icon.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

            // Text Content
            Label label = new Label(stat.label);
            label.setStyle("-fx-font-size: 12px; -fx-text-fill: #6B7280;");
            Label value = new Label(stat.value);
            value.setStyle("-fx-font-size: 24px; -fx-text-fill: #1E3A8A; -fx-font-weight: bold;");

            VBox textContent = new VBox(5, label, value);

            // Card Content (pt-6)
            HBox cardContent = new HBox(15, textContent, icon);
            cardContent.setAlignment(Pos.CENTER_LEFT);
            HBox.setHgrow(textContent, javafx.scene.layout.Priority.ALWAYS);
            HBox.setMargin(icon, new Insets(0, 0, 0, 10)); // Ensure spacing

            // Card (border-2 border-[#E0F2FE])
            VBox card = new VBox(cardContent);
            card.getStyleClass().add("dashboard-stat-card");
            card.setPadding(new Insets(15));

            // Add to Grid (5 columns)
            statsGrid.add(card, i % 5, i / 5);
        }
    }

    // --- Chart Setup ---
    private void setupRevenueChart() {
        revenueChart.getXAxis().setLabel("Month");
        revenueChart.getYAxis().setLabel("Revenue (USD)");
        revenueChart.setLegendVisible(false); // Hide default series legend

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Revenue");
        series.setData(FXCollections.observableArrayList(chartDataPoints));

        revenueChart.getData().add(series);

        // Note: Styling the area fill and line stroke requires custom CSS (chart-styles.css)
        // FXChart default AreaChart uses a standard blue fill.
    }

    // --- Quick Actions Population ---
    private void populateActionsGrid() {
        for (int i = 0; i < quickActions.length; i++) {
            ActionData action = quickActions[i];

            Button button = new Button(action.label);
            button.getStyleClass().add("quick-action-button");
            button.getStyleClass().add(action.colorClass); // Custom CSS class for background color
            button.setPrefSize(180, 80); // Set fixed size for grid appearance
            button.setAlignment(Pos.CENTER);
            button.setContentDisplay(ContentDisplay.TOP); // Stack icon and text
            button.setWrapText(true);

            Label icon = new Label(action.iconSymbol);
            icon.setStyle("-fx-font-size: 20px; -fx-text-fill: white; -fx-font-weight: bold;");
            button.setGraphic(icon);

            // Set up action handler based on actionId (for navigation)
            button.setOnAction(event -> handleQuickAction(action.actionId));

            // Add to Grid (4 columns)
            actionsGrid.add(button, i % 4, i / 4);
            GridPane.setHalignment(button, HPos.CENTER);
        }
    }

    private void handleQuickAction(String actionId) {
        // This is where you would call the main AppLayoutController.navigateTo()
        // For demonstration, we'll use a placeholder alert.
        String targetScreen = switch (actionId) {
            case "add-property" -> "listings"; // Navigate to Listings or open modal
            case "assign-tenant" -> "assign-tenants";
            case "view-reports" -> "reports";
            case "send-reminder" -> "notifications";
            default -> actionId;
        };

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Quick Action");
        alert.setHeaderText(null);
        alert.setContentText("Simulating navigation to: " + targetScreen.toUpperCase());
        alert.show();
    }
}