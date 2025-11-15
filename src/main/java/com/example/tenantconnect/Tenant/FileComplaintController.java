package com.example.tenantconnect.Tenant;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FileComplaintController {

    @FXML
    private ComboBox<String> categoryComboBox;

    @FXML
    private TextField titleTextField;

    @FXML
    private TextArea descriptionTextArea;

    @FXML
    public void initialize() {
        // Add items to ComboBox
        categoryComboBox.getItems().addAll(
                "Plumbing",
                "Electrical",
                "HVAC",
                "General Maintenance",
                "Noise Complaint",
                "Other"
        );
    }
}