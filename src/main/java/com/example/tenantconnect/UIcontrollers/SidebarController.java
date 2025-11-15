package com.example.tenantconnect.UIcontrollers;


import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;

public class SidebarController {

    // Reference to the main layout controller for navigation
    private AppLayoutController appLayoutController;

    public void setAppLayoutController(AppLayoutController appLayoutController) {
        this.appLayoutController = appLayoutController;
    }

    // Generic handler for all sidebar buttons
    @FXML
    private void handleNavigation(ActionEvent event) {
        if (appLayoutController == null) {
            System.err.println("Error: AppLayoutController not set.");
            return;
        }

        // Get the ID of the button that was clicked
        Button clickedButton = (Button) event.getSource();
        String screenId = clickedButton.getId(); // We use the 'id' attribute in the FXML

        if (screenId != null) {
            appLayoutController.navigateTo(screenId);
        }

        // Optionally, update button styling here to show which one is active
        // (This would involve iterating through all buttons and applying/removing the .sidebar-button-active style)
    }

    @FXML
    private void handleLogout() {
        if (appLayoutController != null) {
            appLayoutController.handleLogout();
        }
    }
}