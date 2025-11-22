package com.example.tenantconnect.UIcontrollers;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox; // <-- MISSING: You need this import for the 'sidebar' VBox.
import javafx.scene.control.Alert; // <-- MISSING: Needed for showAlert helper
import javafx.scene.control.Alert.AlertType; // <-- MISSING: Needed for AlertType
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class AppLayoutController {

    @FXML
    private StackPane mainContentArea;

    // 1. Controller Instance (Injected automatically)
    @FXML
    private SidebarController sidebarController;

    // 2. Root Node of the included Sidebar FXML (Matches fx:id="sidebar" in AppLayout.fxml)
    // The compiler cannot resolve the symbol 'sidebar' because it was not declared.
    @FXML
    private VBox sidebar; // <-- ADDED: Declaration for the sidebar VBox.

    @FXML
    private TopBarController topBarController;

    // Assuming the topBar FXML root is an HBox/VBox, declare the root node here too.
    @FXML
    private HBox topBar; // <-- ADDED: Declaration for the topBar HBox.

    @FXML
    public void initialize() {
        // Now 'sidebar' is declared and can be passed as the second argument.
        if (sidebarController != null && sidebar != null) {
            // FIX: Passing the second argument, 'sidebar'
            sidebarController.setAppLayoutController(this, sidebar);
        } else {
            System.err.println("Error: Sidebar or SidebarController failed to inject. Check fx:id and variable name.");
        }

        // Load the initial screen
        navigateTo("dashboard"); // Changed to dashboard, as complaints might not be ready yet.
    }

    /**
     * Loads the FXML for the given screen ID and swaps the content in the center pane.
     */
    public void navigateTo(String screenId) {
        String fxmlPath;

        // Note: The path depends on your resource hierarchy
        switch (screenId) {
            case "dashboard":
                fxmlPath = "/com/example/tenantconnect/Owner/Dashboard.fxml";
                break;
            case "listings":
                fxmlPath = "/com/example/tenantconnect/Owner/Listings.fxml";
                break;
            case "assign-tenants":
                fxmlPath = "/com/example/tenantconnect/Owner/AssignTenants.fxml";
                break;
            case "rent":
            case "rent-tracking": // Accept both IDs
                fxmlPath = "/com/example/tenantconnect/Owner/RentTracking.fxml";
                break;
            case "complaints":
                fxmlPath = "/com/example/tenantconnect/Owner/Complaints.fxml";
                break;
            case "reports":
                fxmlPath = "/com/example/tenantconnect/Owner/Reports.fxml";
                break;
            case "extensions":
                fxmlPath = "/com/example/tenantconnect/Owner/Extensions.fxml";
                break;
            case "contracts":
                fxmlPath = "/com/example/tenantconnect/Owner/Contracts.fxml";
                break;
            case "notifications":
                // Using mock paths for files we haven't created FXML for yet
                fxmlPath = "/com/example/tenantconnect/Owner/" + capitalize(screenId) + ".fxml";
                break;
            default:
                showAlert("Navigation Error", "Unknown screen ID " + screenId, AlertType.ERROR);
                return;
        }

        try {
            // Load the new FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Clear the existing content and set the new content
            mainContentArea.getChildren().setAll(root);

            System.out.println("Navigated to: " + screenId);

        } catch (IOException e) {
            showAlert("Loading Error", "Failed to load screen: " + fxmlPath + ". Does the FXML and its controller exist?", AlertType.ERROR);
            e.printStackTrace();
        }
    }

    public void handleLogout() {
        showAlert("Logout", "User logged out. Application exit simulated.", AlertType.INFORMATION);
        System.out.println("User logged out.");
    }

    private void showAlert(String title, String message, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        // Handle hyphenated names like assign-tenants
        s = s.replace("-", "");
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
