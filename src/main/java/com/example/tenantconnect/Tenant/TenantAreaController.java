package com.example.tenantconnect;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;
import javafx.scene.Parent;

import java.io.IOException;

public class TenantAreaController {

    @FXML private StackPane contentArea;

    @FXML private Button dashboardBtn;
    @FXML private Button fileComplaintBtn;
    @FXML private Button payRentBtn;

    @FXML
    public void initialize() {
        loadPage("Dashboard.fxml"); // default page

        dashboardBtn.setOnAction(e -> loadPage("Dashboard.fxml"));
        fileComplaintBtn.setOnAction(e -> loadPage("fileComplaint.fxml"));
        payRentBtn.setOnAction(e -> loadPage("PayRent.fxml"));
    }

    private void loadPage(String fxml) {
        try {
            Parent page = FXMLLoader.load(getClass().getResource("/com/example/tenantconnect/" + fxml));
            contentArea.getChildren().setAll(page);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
