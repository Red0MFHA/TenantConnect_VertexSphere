package com.example.tenantconnect.UIcontrollers;



import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;

public class TopBarController {

    // FXML ID defined in TopBar.fxml
    @FXML
    private Label dateLabel;

    /**
     * Initializes the controller. This method is called automatically
     * after the FXML file has been loaded.
     */
    @FXML
    public void initialize() {
        // Set the current date, matching the format from the original TypeScript component
        updateDateLabel();
    }

    /**
     * Sets the current date in the label.
     * Format: Wednesday, November 12, 2025
     */
    private void updateDateLabel() {
        LocalDate currentDate = LocalDate.now();
        // Uses the locale-specific formatting to match the full date display
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy");
        String formattedDate = currentDate.format(formatter);
        dateLabel.setText(formattedDate);
    }

    /**
     * Handles the action when the notification button is clicked.
     * This mimics showing a dropdown menu or notification panel.
     */
    @FXML
    private void showNotifications() {
        // In a full JavaFX application, you would launch a pop-up, a ContextMenu,
        // or toggle a VBox visibility here to show the notifications dropdown.
        System.out.println("Notification button clicked! (Simulating notification dropdown)");
        // Since we are simulating, we can print a mock notification count
        System.out.println("Mock: 2 unread notifications available.");
    }
}