package com.example.tenantconnect.UIcontrollers;

import com.example.tenantconnect.Domain.Payment;
import com.example.tenantconnect.Domain.Property;
import com.example.tenantconnect.Services.ContractService;
import com.example.tenantconnect.Services.NotificationService;
import com.example.tenantconnect.Services.PaymentService;
import com.example.tenantconnect.Services.PropertyService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.DatePicker;
import com.example.tenantconnect.Services.FacadeClass;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportsController {

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private BarChart<String, Number> barChart;
    @FXML private PieChart pieChart;
    FacadeClass f;
    private PaymentService paymentService;
    private PropertyService propertyService;

//    public void initialize() {
//        f=FacadeClass.getInstance();
//
//        NotificationService notificationService = f.getNotificationService();
//        ContractService contractService = f.getContractService();
//        propertyService = f.getPropertyService();
//        paymentService = f.getPaymentService(); // Pass TenantService if needed
//
//        startDatePicker.setValue(LocalDate.now().withDayOfMonth(1));
//        endDatePicker.setValue(LocalDate.now());
//
//        loadCharts();
//
//        // Update charts when date range changes
//        startDatePicker.valueProperty().addListener((obs, oldDate, newDate) -> loadCharts());
//        endDatePicker.valueProperty().addListener((obs, oldDate, newDate) -> loadCharts());
//        loadCharts();
//    }
public void initialize() {
    f = FacadeClass.getInstance();
    propertyService = f.getPropertyService();
    paymentService = f.getPaymentService();

    startDatePicker.setValue(LocalDate.now().withDayOfMonth(1));
    endDatePicker.setValue(LocalDate.now());

    if (propertyService != null && paymentService != null) {
        loadCharts();
    } else {
        System.out.println("Services not initialized yet. Charts will load later.");
    }

    startDatePicker.valueProperty().addListener((obs, oldDate, newDate) -> safeLoad());
    endDatePicker.valueProperty().addListener((obs, oldDate, newDate) -> safeLoad());
}

    private void safeLoad() {
        if (propertyService != null && paymentService != null) {
            loadCharts();
        }
    }
//
//    private void loadCharts() {
//        LocalDate startDate = startDatePicker.getValue();
//        LocalDate endDate = endDatePicker.getValue();
//        int ownerId = FacadeClass.CURRENT_USER_ID; // Replace with logged-in owner ID dynamically
//
//        List<Property> properties = propertyService.getOwnerProperties(ownerId);
//
//        // --- BarChart: Expected vs Collected ---
//        barChart.getData().clear();
//        XYChart.Series<String, Number> collectedSeries = new XYChart.Series<>();
//        collectedSeries.setName("Collected Rent");
//        XYChart.Series<String, Number> expectedSeries = new XYChart.Series<>();
//        expectedSeries.setName("Expected Rent");
//
//        for (Property property : properties) {
//            double expected = property.getRent_amount();
//            List<Payment> payments = paymentService.getRentHistory(ownerId, "owner").stream()
//                    .filter(p -> p.getContract_id() == property.getProperty_id())
//                    .filter(p -> {
//                        LocalDate paymentDate = LocalDate.parse(p.getPayment_date());
//                        return !paymentDate.isBefore(startDate) && !paymentDate.isAfter(endDate);
//                    }).toList();
//
//            double collected = payments.stream().mapToDouble(Payment::getAmount_paid).sum();
//
//            expectedSeries.getData().add(new XYChart.Data<>(property.getProperty_name(), expected));
//            collectedSeries.getData().add(new XYChart.Data<>(property.getProperty_name(), collected));
//        }
//        barChart.getData().addAll(expectedSeries, collectedSeries);
//
//        // --- PieChart: Payment Status Distribution ---
//        pieChart.getData().clear();
//        List<Payment> allPayments = paymentService.getRentHistory(ownerId, "owner").stream()
//                .filter(p -> {
//                    LocalDate paymentDate = LocalDate.parse(p.getPayment_date());
//                    return !paymentDate.isBefore(startDate) && !paymentDate.isAfter(endDate);
//                }).toList();
//
//        Map<String, Long> statusCounts = allPayments.stream()
//                .collect(Collectors.groupingBy(Payment::getPayment_status, Collectors.counting()));
//
//        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
//        statusCounts.forEach((status, count) -> pieData.add(new PieChart.Data(status, count)));
//        pieChart.setData(pieData);
//    }

    private LocalDate safeParseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return null;
        try {
            return LocalDate.parse(dateStr);
        } catch (Exception e) {
            System.out.println("Invalid date format: " + dateStr);
            return null;
        }
    }
    private void loadCharts() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        int ownerId = FacadeClass.CURRENT_USER_ID;

        List<Property> properties = propertyService.getOwnerProperties(ownerId);
        List<Payment> rentHistory = paymentService.getRentHistory(ownerId, "owner");

        // ---------------- BAR CHART ----------------
        barChart.getData().clear();

        XYChart.Series<String, Number> expectedSeries = new XYChart.Series<>();
        expectedSeries.setName("Expected Rent");

        XYChart.Series<String, Number> collectedSeries = new XYChart.Series<>();
        collectedSeries.setName("Collected Rent");

        for (Property property : properties) {

            double expected = property.getRent_amount();

            List<Payment> payments = rentHistory.stream()
                    .filter(p -> p.getContract_id() == property.getProperty_id()) // match
                    .filter(p -> {
                        LocalDate paymentDate = safeParseDate(p.getPayment_date());
                        if (paymentDate == null) return false;

                        return !paymentDate.isBefore(startDate)
                                && !paymentDate.isAfter(endDate);
                    })
                    .toList();

            double collected = payments.stream()
                    .mapToDouble(Payment::getAmount_paid)
                    .sum();

            expectedSeries.getData().add(new XYChart.Data<>(property.getProperty_name(), expected));
            collectedSeries.getData().add(new XYChart.Data<>(property.getProperty_name(), collected));
        }

        barChart.getData().addAll(expectedSeries, collectedSeries);

        // ---------------- PIE CHART ----------------
        pieChart.getData().clear();

        List<Payment> filteredPayments = rentHistory.stream()
                .filter(p -> {
                    LocalDate paymentDate = safeParseDate(p.getPayment_date());
                    if (paymentDate == null) return false;

                    return !paymentDate.isBefore(startDate)
                            && !paymentDate.isAfter(endDate);
                })
                .toList();

        Map<String, Long> statusCounts = filteredPayments.stream()
                .collect(Collectors.groupingBy(
                        Payment::getPayment_status,
                        Collectors.counting()
                ));

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        statusCounts.forEach((status, count) ->
                pieData.add(new PieChart.Data(status, count))
        );

        pieChart.setData(pieData);
    }


}

