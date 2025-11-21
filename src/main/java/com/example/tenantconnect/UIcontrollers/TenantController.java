package com.example.tenantconnect.UIcontrollers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class TenantController {

    @FXML private VBox contentArea;
    @FXML private VBox sidebar;

    private Button dashboardBtn, fileComplaintBtn, payRentBtn;
    private Button propertyAssignmentBtn, rentHistoryBtn, rentStatusBtn, requestExtensionBtn;

    private Button currentActive; // For active highlight

    @FXML
    private void initialize() {
        // Lookup all buttons
        dashboardBtn          = (Button) sidebar.lookup("#dashboardBtn");
        fileComplaintBtn      = (Button) sidebar.lookup("#fileComplaintBtn");
        payRentBtn            = (Button) sidebar.lookup("#payRentBtn");
        propertyAssignmentBtn = (Button) sidebar.lookup("#propertyAssignmentBtn");
        rentHistoryBtn        = (Button) sidebar.lookup("#rentHistoryBtn");
        rentStatusBtn         = (Button) sidebar.lookup("#rentStatusBtn");
        requestExtensionBtn   = (Button) sidebar.lookup("#requestExtensionBtn");

        // Set actions with active highlight
        dashboardBtn.setOnAction(e -> setActiveAndLoad(dashboardBtn, "Dashboard.fxml"));
        fileComplaintBtn.setOnAction(e -> setActiveAndLoad(fileComplaintBtn, "FileComplaint.fxml"));
        payRentBtn.setOnAction(e -> setActiveAndLoad(payRentBtn, "PayRent.fxml"));
        propertyAssignmentBtn.setOnAction(e -> setActiveAndLoad(propertyAssignmentBtn, "PropertyAssignment.fxml"));
        rentHistoryBtn.setOnAction(e -> setActiveAndLoad(rentHistoryBtn, "RentHistory.fxml"));
        rentStatusBtn.setOnAction(e -> setActiveAndLoad(rentStatusBtn, "RentStatus.fxml"));
        requestExtensionBtn.setOnAction(e -> setActiveAndLoad(requestExtensionBtn, "RequestExtension.fxml"));

        // Load Dashboard by default and make it active
        setActiveAndLoad(dashboardBtn, "Dashboard.fxml");
    }

    private void setActiveAndLoad(Button button, String fxml) {
        if (currentActive != null) {
            currentActive.getStyleClass().remove("active-menu-btn");
        }
        button.getStyleClass().add("active-menu-btn");
        currentActive = button;
        loadPage(fxml);
    }

    private void loadPage(String fxmlFileName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/tenantconnect/Tenant/" + fxmlFileName
            ));
            Node page = loader.load();

            // THIS IS THE MAGIC FIX
            ScrollPane scrollPane = new ScrollPane(page);
            scrollPane.setFitToWidth(true);        // Makes content fit horizontally
            scrollPane.setFitToHeight(false);      // Allows vertical growth → shows scrollbar
            scrollPane.setStyle("-fx-background-color: transparent;"); // Optional: clean look

            contentArea.getChildren().clear();
            contentArea.getChildren().add(scrollPane);
            VBox.setVgrow(scrollPane, Priority.ALWAYS);  // Important!

        } catch (IOException e) {
            e.printStackTrace();
            contentArea.getChildren().add(new Label("Page not found: " + fxmlFileName));
        }
    }
}