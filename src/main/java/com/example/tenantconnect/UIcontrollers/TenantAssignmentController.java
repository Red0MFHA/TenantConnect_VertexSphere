package com.example.tenantconnect.UIcontrollers;


import com.example.tenantconnect.Domain.Contract;
import com.example.tenantconnect.Services.ContractService;
import com.example.tenantconnect.Services.NotificationService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.example.tenantconnect.Services.FacadeClass;
import java.util.List;

public class TenantAssignmentController {

    @FXML private TableView<Contract> contractsTable;
    @FXML private TableColumn<Contract, Number> colContractId;
    @FXML private TableColumn<Contract, Number> colPropertyId;
    @FXML private TableColumn<Contract, String> colCreatedAt;

    @FXML private TextField contractIdField;
    @FXML private TextArea messageField;

    @FXML private Button btnAccept;
    @FXML private Button btnReject;
    @FXML private Button btnNegotiate;

    private ContractService contractService;
    private NotificationService notificationService;
    private FacadeClass f;
    private int loggedInTenantId;   // <-- Set from login controller

    @FXML
    public void initialize() {
        f=FacadeClass.getInstance();
        notificationService = f.getNotificationService();
        contractService = f.getContractService();
        loggedInTenantId=FacadeClass.CURRENT_USER_ID;

        setupTable();
        loadPendingContracts();
        contractsTable.setOnMouseClicked(e -> {
            Contract selected = contractsTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                contractIdField.setText(String.valueOf(selected.getContract_id()));
            }
        });

        btnAccept.setOnAction(e -> acceptContract());
        btnReject.setOnAction(e -> rejectContract());
        btnNegotiate.setOnAction(e -> negotiateContract());
    }

    private void setupTable() {
        colContractId.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getContract_id()));
        colPropertyId.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getProperty_id()));
        colCreatedAt.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCreated_at()));
    }

    private void loadPendingContracts() {
        List<Contract> list = contractService.getPendingAssignments(loggedInTenantId);
        ObservableList<Contract> data = FXCollections.observableArrayList(list);
        contractsTable.setItems(data);
    }

    private Contract getSelectedContract() {
        try {
            int id = Integer.parseInt(contractIdField.getText().trim());
            return contractService.getContractDetails(id);
        } catch (Exception ex) {
            showAlert("Invalid Contract ID");
            return null;
        }
    }

    private void acceptContract() {
        Contract con = getSelectedContract();
        if (con == null) return;

        contractService.acceptContract(loggedInTenantId, con);
        showAlert("Contract Accepted Successfully !");
        loadPendingContracts();
    }

    private void rejectContract() {
        Contract con = getSelectedContract();
        if (con == null) return;

        contractService.rejectContract(loggedInTenantId, con);
        showAlert("Contract Rejected.");
        loadPendingContracts();
    }

    private void negotiateContract() {
        Contract con = getSelectedContract();
        if (con == null) return;

        String msg = messageField.getText().trim();
        if (msg.isEmpty()) {
            showAlert("Please write a message to negotiate terms.");
            return;
        }

        int ownerId = contractService.getContractDetails(con.getContract_id()).getTenant_id();
        notificationService.sendContractUpdateionNotification(ownerId, con.getContract_id(), msg);

        showAlert("Negotiation message sent to the owner.");
        messageField.clear();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("TenantConnect");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public void setTenantId(int tenantId) {
        this.loggedInTenantId = tenantId;
        loadPendingContracts();
    }
}
