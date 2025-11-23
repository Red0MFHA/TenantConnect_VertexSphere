package com.example.tenantconnect.UIcontrollers;

import com.example.tenantconnect.Domain.Contract;
import com.example.tenantconnect.Domain.Payment;
import com.example.tenantconnect.Domain.Property;
import com.example.tenantconnect.Services.ContractService;
import com.example.tenantconnect.Services.PaymentService;
import com.example.tenantconnect.Services.PropertyService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import com.example.tenantconnect.Services.FacadeClass;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TenantDashboardController {

    @FXML private Label rentStatusLabel;
    @FXML private Label nextDueDateLabel;
    @FXML private Label openComplaintsLabel;
    @FXML private VBox myPropertyContainer;

    @FXML private VBox recentActivityContainer;

    @FXML private Button payRentButton;
    @FXML private Button fileComplaintButton;
    @FXML private Button requestExtensionButton;
    private FacadeClass f;
    private PaymentService paymentService;
    private ContractService contractService;
    private PropertyService propertyService;

    private int tenantId; // set this when loading dashboard
    private VBox mainContent;

    public void setMainContentContainer(VBox mainContent) {
        this.mainContent = mainContent;
    }
    @FXML
    public void initialize() {
        f = FacadeClass.getInstance();
        this.tenantId = FacadeClass.CURRENT_USER_ID;
        this.paymentService = f.getPaymentService();
        this.contractService = f.getContractService();
        this.propertyService = f.getPropertyService();

        loadDashboardData();
    }

    private void loadDashboardData() {
        loadRentStatus();
        loadNextDueDate();
        loadOpenComplaints();
        loadProperty();
        loadRecentActivity();
    }

    private void loadRentStatus() {
        List<Payment> duePayments = paymentService.getDuePaymentsForTenant(tenantId);
        boolean allPaid = duePayments.stream().allMatch(p -> "paid".equalsIgnoreCase(p.getPayment_status()));
        rentStatusLabel.setText(allPaid ? "Paid" : "Pending");
        rentStatusLabel.setStyle(allPaid ? "-fx-text-fill: #10B981;" : "-fx-text-fill: #F59E0B;"); // green or orange
    }

    private void loadNextDueDate() {
        List<Payment> duePayments = paymentService.getDuePaymentsForTenant(tenantId);
        Payment nextPayment = duePayments.stream()
                .filter(p -> !"paid".equalsIgnoreCase(p.getPayment_status()))
                .sorted((p1, p2) -> p1.getDue_date().compareTo(p2.getDue_date()))
                .findFirst()
                .orElse(null);

        if (nextPayment != null) {
            nextDueDateLabel.setText(nextPayment.getDue_date());
        } else {
            nextDueDateLabel.setText("N/A");
        }
    }

    private void loadOpenComplaints() {
        // TODO: Fetch complaints from your complaint service
        // For now we hardcode
        int openComplaints = 2;
        openComplaintsLabel.setText(String.valueOf(openComplaints));
    }

    private void loadProperty() {
        myPropertyContainer.getChildren().clear();
        List<Property> properties = propertyService.getTenantProperties(tenantId);
        if (!properties.isEmpty()) {
            for (Property p : properties) {
                Label propertyLabel = new Label("• " + p.getProperty_name());
                propertyLabel.getStyleClass().add("text-blue");
                myPropertyContainer.getChildren().add(propertyLabel);
            }
        } else {
            Label naLabel = new Label("N/A");
            naLabel.getStyleClass().add("text-muted");
            myPropertyContainer.getChildren().add(naLabel);
        }
    }

    private void loadRecentActivity() {
        recentActivityContainer.getChildren().clear();

        List<Payment> payments = paymentService.getRentHistory(tenantId, "tenant");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Payment p : payments) {
            VBox item = new VBox();
            Label desc = new Label("Rent payment: " + p.getPayment_id());
            Label date = new Label(p.getPayment_date());
            desc.getStyleClass().add("text-blue");
            date.getStyleClass().add("text-muted");
            item.getChildren().addAll(desc, date);
            recentActivityContainer.getChildren().add(item);
        }

        // TODO: Add complaint activities and extension approvals similarly
    }
    @FXML
    private void payRentNow() {
        // Load PayRent.fxml
        setActiveAndLoad(payRentButton, "PayRent.fxml");
    }

    @FXML
    private void fileComplaint() {
        // Load FileComplaint.fxml
        setActiveAndLoad(fileComplaintButton, "FileComplaint.fxml");
    }

    @FXML
    private void requestExtension() {
        // Load RequestExtension.fxml
        setActiveAndLoad(requestExtensionButton, "RequestExtension.fxml");
    }

    // Add this method (similar to your sidebar one) in the same controller
    private void setActiveAndLoad(Button btn, String fxmlPath) {
        if (mainContent == null) {
            System.err.println("mainContent container is not set!");
            return;
        }
        try {
            mainContent.getChildren().clear();
            VBox newContent = javafx.fxml.FXMLLoader.load(getClass().getResource("/com/example/tenantconnect/UIcontrollers/" + fxmlPath));
            mainContent.getChildren().add(newContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
