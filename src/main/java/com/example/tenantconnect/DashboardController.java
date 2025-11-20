package com.example.tenantconnect;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class DashboardController {

    @FXML
    private StackPane mainContent;

    private Pane screen1;
    private Pane screen2;

    @FXML
    public void initialize() {
        try {
            screen1 = FXMLLoader.load(getClass().getResource("/com/example/app/screens/fileComplaint.fxml"));
            screen2 = FXMLLoader.load(getClass().getResource("/com/example/app/screens/Screen2.fxml"));

            // Show screen1 by default
            mainContent.getChildren().add(screen1);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showScreen1() {
        mainContent.getChildren().clear();
        mainContent.getChildren().add(screen1);
    }

    @FXML
    private void showScreen2() {
        mainContent.getChildren().clear();
        mainContent.getChildren().add(screen2);
    }
}
