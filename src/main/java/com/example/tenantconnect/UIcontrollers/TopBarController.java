package com.example.tenantconnect.UIcontrollers;
import com.example.tenantconnect.Domain.User;

import com.example.tenantconnect.Repositories.UserRepository;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.Node;
import javafx.stage.Stage; // Needed for closing the application on logout
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;

public class TopBarController {

    // FXML IDs
    @FXML
    private Label dateLabel;
    @FXML
    private Label welcomeLabel; // New ID for "Welcome back, ..." label
    @FXML
    private Label userNameLabel; // New ID for the user's name

    // Repositories and Managers
    private final UserRepository userRepository = new UserRepository();
    private final SessionManager sessionManager = SessionManager.getInstance();

    /**
     * Initializes the controller. Called automatically after the FXML file has been loaded.
     */
    @FXML
    public void initialize() {
        updateDateLabel();
        updateUserDetails();
    }

    /**
     * Sets the current date in the label.
     */
    private void updateDateLabel() {
        LocalDate currentDate = LocalDate.now();
        // Format: EEEE, MMMM dd, yyyy (e.g., Wednesday, November 12, 2025)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy");
        String formattedDate = currentDate.format(formatter);
        dateLabel.setText(formattedDate);
    }

    /**
     * Fetches and displays the logged-in user's name and type.
     */
    private void updateUserDetails() {
        int userId = sessionManager.getLoggedInUserId();
        if (userId != -1) {
            User user = userRepository.getUserObjectById(userId);
            if (user != null) {
                // Set the user's full name in the profile badge
                userNameLabel.setText(user.full_name);

                // Set the welcome message
                String userTypeDisplay = user.user_type.substring(0, 1).toUpperCase() + user.user_type.substring(1); // Capitalize first letter
                welcomeLabel.setText("Welcome back, " + userTypeDisplay);
            } else {
                // Handle case where user ID is in session but not in DB
                userNameLabel.setText("Guest");
                welcomeLabel.setText("Welcome back");
            }
        } else {
            // Handle case where no user is logged in (e.g., development/testing scenario)
            userNameLabel.setText("Not Logged In");
            welcomeLabel.setText("Welcome back");
        }
    }

    /**
     * Handles the action when the notification button is clicked.
     * This should trigger navigation to the notification page/view.
     */
    @FXML
    private void handleNotificationClick() {
        System.out.println("Notification button clicked! Navigating to Notification Page...");
        // **TODO:** Implement the logic to switch the main application view/scene
        // to the Notification Page. This typically involves calling a method
        // in your MainApplication or a SceneManager class.
        // Example: sceneManager.loadPage("NotificationView.fxml");
    }

    /**
     * Handles the logout action.
     * Clears the session and closes the application (or navigates to the login screen).
     * @param event The action event, used to get the window to close.
     */
    @FXML
    private void handleLogout(javafx.event.ActionEvent event) {
        sessionManager.logout();
        System.out.println("Logout successful. Closing application.");

        // **TODO:** Replace closing the application with navigating back to your login screen.
        // For demonstration, we'll close the current window.
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
}