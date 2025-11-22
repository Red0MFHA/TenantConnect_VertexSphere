package com.example.tenantconnect.UIcontrollers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.Node;

public class SidebarController {

    private AppLayoutController appLayoutController;
    private final List<Button> navButtons = new ArrayList<>();
    private Button currentActiveButton = null;

    // Matches the fx:id="navigationVBox" in Sidebar.fxml (Note: we don't strictly need this FXML injection
    // since we're using the VBox passed from AppLayoutController, but it's kept for completeness.)
    @FXML private VBox navigationVBox;

    @FXML
    public void initialize() {
        // Initialization logic here
    }

    /**
     * Called by AppLayoutController after injection to establish the link and set initial state.
     * This method collects all buttons for style management and sets the initial active screen.
     */
    public void setAppLayoutController(AppLayoutController appLayoutController, VBox sidebarVBox) {
        this.appLayoutController = appLayoutController;

        // Collect all buttons for style management
        if (sidebarVBox != null && sidebarVBox.getChildren().size() >= 3) {

            // CRITICAL FIX: The main navigation VBox is at index 1 (Header is at 0)
            VBox navBox = (VBox) sidebarVBox.getChildren().get(1);
            for (Node node : navBox.getChildren()) {
                if (node instanceof Button) {
                    navButtons.add((Button) node);
                }
            }

            // CRITICAL FIX: The footer VBox is at index 2
            VBox footerBox = (VBox) sidebarVBox.getChildren().get(2);
            for (Node node : footerBox.getChildren()) {
                if (node instanceof Button) {
                    navButtons.add((Button) node); // Add logout button to list for style if needed
                }
            }
        } else {
            System.err.println("Sidebar structure is incomplete. Cannot initialize button listeners.");
        }


        // Set initial screen (e.g., Dashboard)
        Button initialButton = getButtonById("dashboard");
        if (initialButton != null) {
            setActiveButton(initialButton);
            // Ensure the main content area loads the default dashboard view
            appLayoutController.navigateTo("dashboard");
        }
    }

    /**
     * Finds a button in the list by its ID (from the FXML).
     */
    private Button getButtonById(String id) {
        for (Button btn : navButtons) {
            if (btn.getId() != null && btn.getId().equals(id)) {
                return btn;
            }
        }
        return null;
    }

    /**
     * Handles clicks on any sidebar navigation button (now linked directly via FXML onAction).
     */
    @FXML
    private void handleNavigationClick(ActionEvent event) {
        Button sourceButton = (Button) event.getSource();
        String screenId = sourceButton.getId();

        if (screenId == null) {
            System.err.println("Button clicked without an ID. Navigation failed.");
            return;
        }

        if ("logout".equals(screenId)) {
            if (appLayoutController != null) {
                appLayoutController.handleLogout();
            }
            return;
        }

        if (appLayoutController != null) {
            // Handle navigation and style update
            String navId = screenId.replace("-tracking", ""); // Convert rent-tracking to rent

            appLayoutController.navigateTo(navId);
            setActiveButton(sourceButton);
        }
    }

    /**
     * Sets the CSS style for the currently active button and removes it from others.
     */
    private void setActiveButton(Button active) {
        // Remove active class from the previously active button
        if (currentActiveButton != null) {
            currentActiveButton.getStyleClass().remove("sidebar-button-active");
            // Re-add the base style if needed (if it was removed when made active)
            if (!currentActiveButton.getStyleClass().contains("sidebar-button")) {
                currentActiveButton.getStyleClass().add("sidebar-button");
            }
        }

        // Set active class on the new button
        active.getStyleClass().add("sidebar-button-active");
        active.getStyleClass().remove("sidebar-button"); // Ensure it doesn't have both styles

        currentActiveButton = active;
    }
}