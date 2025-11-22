package com.example.tenantconnect.UIcontrollers;

import com.example.tenantconnect.Domain.Contract;
import com.example.tenantconnect.Services.ContractService;
import com.example.tenantconnect.Services.FacadeClass;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddContractController {

    @FXML private TextField txtPropertyId;
    @FXML private TextField txtTenantId;
    @FXML private DatePicker datePickerEndDate;
    @FXML private TextField txtMonthlyRent;
    @FXML private TextField txtSecurityDeposit;
    @FXML private Button btnSaveContract;
    @FXML private Button btnCancel;

    private ContractService contractService;
    private int ownerId;
    private Runnable onSuccessCallback;

    @FXML
    public void initialize() {
        contractService = FacadeClass.getInstance().getContractService();
        ownerId = FacadeClass.CURRENT_USER_ID;

        setupEventHandlers();
    }

    public void setOnSuccessCallback(Runnable callback) {
        this.onSuccessCallback = callback;
    }

    private void setupEventHandlers() {
        btnSaveContract.setOnAction(event -> saveContract());
        btnCancel.setOnAction(event -> closeForm());
    }

    private void saveContract() {
        try {
            // Validate inputs
            if (!validateInputs()) {
                return;
            }

            int propertyId = Integer.parseInt(txtPropertyId.getText());
            int tenantId = Integer.parseInt(txtTenantId.getText());
            String endDate = datePickerEndDate.getValue() != null ?
                    datePickerEndDate.getValue().toString() : "";
            double monthlyRent = Double.parseDouble(txtMonthlyRent.getText());
            double securityDeposit = Double.parseDouble(txtSecurityDeposit.getText());

            // Validate end date
            if (endDate.isEmpty()) {
                showAlert("Error", "Please select an end date");
                return;
            }

            // Create Contract object
            Contract newContract = new Contract();
            newContract.setProperty_id(propertyId);
            newContract.setTenant_id(tenantId);
            newContract.setEnd_date(endDate);
            newContract.setMonthly_rent(monthlyRent);
            newContract.setSecurity_deposit(securityDeposit);
            newContract.setContract_status("pending");

            // Call service to add contract
            int newContractId = contractService.createNewContract(ownerId, newContract);

            if (newContractId != -1) {
                showAlert("Success", "Contract created successfully with ID: " + newContractId);
                if (onSuccessCallback != null) {
                    onSuccessCallback.run();
                }
                closeForm();
            } else {
                showAlert("Error", "Failed to create contract.");
            }

        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid input. Please check that all numeric fields contain valid numbers.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred: " + e.getMessage());
        }
    }

    private boolean validateInputs() {
        if (txtPropertyId.getText().isEmpty() ||
                txtTenantId.getText().isEmpty() ||
                datePickerEndDate.getValue() == null ||
                txtMonthlyRent.getText().isEmpty() ||
                txtSecurityDeposit.getText().isEmpty()) {

            showAlert("Error", "Please fill in all fields");
            return false;
        }

        // Validate numeric fields
        try {
            Integer.parseInt(txtPropertyId.getText());
            Integer.parseInt(txtTenantId.getText());
            Double.parseDouble(txtMonthlyRent.getText());
            Double.parseDouble(txtSecurityDeposit.getText());
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter valid numbers in numeric fields");
            return false;
        }

        return true;
    }

    private void closeForm() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}