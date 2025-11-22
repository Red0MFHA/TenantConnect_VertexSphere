package com.example.tenantconnect.UIcontrollers;


import com.example.tenantconnect.Domain.Contract;
import com.example.tenantconnect.Services.ContractService;
import com.example.tenantconnect.Services.FacadeClass;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class ContractsController {

    @FXML private TableView<Contract> contractsTable;
    @FXML private TableColumn<Contract, Integer> contractIdColumn;
    @FXML private TableColumn<Contract, Integer> tenantColumn;
    @FXML private TableColumn<Contract, Integer> propertyColumn;
    @FXML private TableColumn<Contract, String> startDateColumn;
    @FXML private TableColumn<Contract, String> statusColumn;

    @FXML private Button btnAddContract;
    @FXML private Button btnTerminateContract;
    @FXML private Button btnDeleteContract;
    @FXML private TextField txtContractId;

    private ContractService contractService;
    private int ownerId = FacadeClass.CURRENT_USER_ID; // Example owner id, replace with logged-in owner

    @FXML
    public void initialize() {
        // Initialize ContractService
        contractService = FacadeClass.getInstance().getContractService();

        // Bind table columns
        contractIdColumn.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getContract_id()).asObject()
        );

        tenantColumn.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getTenant_id()).asObject()
        );

        propertyColumn.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getProperty_id()).asObject()
        );
        startDateColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getStart_date()));
        statusColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getContract_status()));

        // Load initial data
        loadContracts();

        // Button actions
        btnAddContract.setOnAction(e -> openAddContractForm());
        btnTerminateContract.setOnAction(e -> terminateSelectedContract());
        btnDeleteContract.setOnAction(e -> deleteSelectedContract());
    }

    private void loadContracts() {
        List<Contract> contracts = contractService.getContractsByOwner(ownerId);
        ObservableList<Contract> data = FXCollections.observableArrayList(contracts);
        contractsTable.setItems(data);
    }

    private void openAddContractForm() {
        // TODO: Open a separate form/window to add contract
        System.out.println("Open Add Contract form");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tenantconnect/Owner/AddContractForm.fxml"));
            VBox formRoot = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Add Contract");
            stage.setScene(new Scene(formRoot));
            stage.initModality(Modality.APPLICATION_MODAL); // blocks main window
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
        loadContracts();
    }
//    private void openAddContractForm() {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tenantconnect/Owner/AddContractForm.fxml"));
//
//            // Set parent controller for the loaded FXML
//            loader.setController(this);
//
//            VBox formRoot = loader.load();
//
//            // Get the fields from the form
//            TextField txtPropertyId = (TextField) formRoot.lookup("#txtPropertyId");
//            TextField txtTenantId = (TextField) formRoot.lookup("#txtTenantId");
//            DatePicker datePickerEndDate = (DatePicker) formRoot.lookup("#datePickerEndDate");
//            TextField txtMonthlyRent = (TextField) formRoot.lookup("#txtMonthlyRent");
//            TextField txtSecurityDeposit = (TextField) formRoot.lookup("#txtSecurityDeposit");
//            Button btnSaveContract = (Button) formRoot.lookup("#btnSaveContract");
//            Button btnCancel = (Button) formRoot.lookup("#btnCancel");
//
//            Stage stage = new Stage();
//            stage.setTitle("Add Contract");
//            stage.setScene(new Scene(formRoot));
//            stage.initModality(Modality.APPLICATION_MODAL);
//
//            // Save button handler
//            btnSaveContract.setOnAction(event -> {
//                try {
//                    int propertyId = Integer.parseInt(txtPropertyId.getText());
//                    int tenantId = Integer.parseInt(txtTenantId.getText());
//                    String endDate = datePickerEndDate.getValue().toString();
//                    double monthlyRent = Double.parseDouble(txtMonthlyRent.getText());
//                    double securityDeposit = Double.parseDouble(txtSecurityDeposit.getText());
//
//                    // Create Contract object
//                    Contract newContract = new Contract();
//                    newContract.setProperty_id(propertyId);
//                    newContract.setTenant_id(tenantId);
//                    newContract.setEnd_date(endDate);
//                    newContract.setMonthly_rent(monthlyRent);
//                    newContract.setSecurity_deposit(securityDeposit);
//                    newContract.setContract_status("pending"); // always start as pending
//
//                    // Call your service to add contract
//                    int newContractId = contractService.createNewContract(ownerId, newContract);
//
//                    if (newContractId != -1) {
//                        showAlert("Contract created successfully with ID: " + newContractId);
//                        loadContracts(); // refresh main table
//                        stage.close();
//                    } else {
//                        showAlert("Failed to create contract.");
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    showAlert("Invalid input. Please check your fields.");
//                }
//            });
//
//            // Cancel button handler
//            btnCancel.setOnAction(e -> stage.close());
//
//            stage.showAndWait();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private void terminateSelectedContract() {
        String contractIdText = txtContractId.getText();
        if(contractIdText.isEmpty()) {
            showAlert("Please enter a Contract ID");
            return;
        }

        int contractId = Integer.parseInt(contractIdText);
        Contract contract = contractService.getContractDetails(contractId);
        if(contract != null) {
            contractService.terminateContract(ownerId, contract);
            loadContracts();
        } else {
            showAlert("Contract not found");
        }
    }

    private void deleteSelectedContract() {
        String contractIdText = txtContractId.getText();
        if(contractIdText.isEmpty()) {
            showAlert("Please enter a Contract ID");
            return;
        }

        int contractId = Integer.parseInt(contractIdText);
        boolean success = contractService.DeleteContract(ownerId, contractId);
        if(success) {
            loadContracts();
        } else {
            showAlert("Failed to delete contract. You may not be authorized.");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Contract Management");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
