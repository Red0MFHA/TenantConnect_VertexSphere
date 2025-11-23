package com.example.tenantconnect.UIcontrollers;

import com.example.tenantconnect.Services.FacadeClass;
import com.example.tenantconnect.Services.NotificationService;
import com.example.tenantconnect.Domain.Notification;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import java.util.List;

/**
 * Controller for the Notifications view (notifications.fxml).
 * Handles fetching and displaying notifications for the current user.
 */
public class NotificationsController {

    @FXML
    private ListView<Notification> notificationsListView;

    private NotificationService notificationService;
    // Uses the static field from FacadeClass to get the currently logged-in user ID
    private final int CURRENT_USER_ID = FacadeClass.CURRENT_USER_ID;

    @FXML
    public void initialize() {
        // 1. Get the NotificationService instance via the Facade
        notificationService = FacadeClass.getInstance().getNotificationService();

        // 2. Load and display notifications
        loadNotifications();

        // 3. Set up listener to mark notification as read on click
        setupReadListener();
    }

    private void loadNotifications() {
        // Fetch all notifications for the current user (Owner 6)
        List<Notification> notificationList = notificationService.getNotificationsForUser(CURRENT_USER_ID);

        // Convert to ObservableList for JavaFX
        ObservableList<Notification> data = FXCollections.observableArrayList(notificationList);

        // Set the data to the ListView
        notificationsListView.setItems(data);

        // Set placeholder for empty list
        if (data.isEmpty()) {
            notificationsListView.setPlaceholder(new Label("You have no notifications."));
        }
    }

    private void setupReadListener() {
        notificationsListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null && !newValue.isIs_read()) {
                        // Mark as read in DB
                        notificationService.readNotification(newValue.getNotification_id());

                        // Update the UI object's state (optional, but good practice)
                        newValue.setIs_read(true);

                        // You may need to visually update the list item (e.g., change its style)
                        // If complex styling is needed, you would use a custom CellFactory,
                        // but for basic display, simply marking as read in the DB is sufficient.
                    }
                }
        );
    }
}