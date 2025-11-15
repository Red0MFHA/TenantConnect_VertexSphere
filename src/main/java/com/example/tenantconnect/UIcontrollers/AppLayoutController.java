package com.example.tenantconnect.UIcontrollers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import com.example.tenantconnect.UIcontrollers.SidebarController; // Adjust package name if necessary
import com.example.tenantconnect.UIcontrollers.TopBarController;   // Adjust package name if necessary
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
public class AppLayoutController {

    @FXML
    private StackPane mainContentArea;

    // --- CRUCIAL: Must be fx:id + "Controller" ---
    @FXML
    private SidebarController sidebarController; // Correct variable name

    @FXML
    private TopBarController topBarController; // Correct variable name

    // The previous error trace suggested your packages might be different.
    // Ensure the imports (SidebarController and TopBarController) match the actual location.
    // If your controllers are inside a UIcontrollers package, use that path.
    // E.g., import com.example.tenantconnect.UIcontrollers.SidebarController;

    @FXML
    public void initialize() {
        // This is the line that failed because sidebarController was null:
        if (sidebarController != null) {
            sidebarController.setAppLayoutController(this);
        } else {
            // This indicates a missing fx:id or a mismatch in naming/package structure.
            System.err.println("Error: SidebarController failed to inject. Check fx:id and variable name.");
        }

        // Load the initial screen
        navigateTo("complaints");
    }

    /**
     * Loads the FXML for the given screen ID and swaps the content in the center pane.
     * @param screenId The ID of the screen to load (e.g., "assign-tenants", "listings")
     */
    public void navigateTo(String screenId) {
        String fxmlPath;

        // Note: The path depends on your resource hierarchy
        switch (screenId) {
            case "assign-tenants":
                fxmlPath = "/com/example/tenantconnect/Owner/AssignTenants.fxml"; // FXML created previously
                break;
            case "complaints":
                fxmlPath = "/com/example/tenantconnect/Owner/Complaints.fxml";
                break;
            default:
                // Handle unhandled screens
                System.err.println("Navigation error: Unknown screen ID " + screenId);
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
            System.err.println("Failed to load screen: " + fxmlPath);
            e.printStackTrace();
        }
    }

    public void handleLogout() {
        // Add your application's logout logic here (e.g., closing window, going to login screen)
        System.out.println("User logged out.");
    }
}