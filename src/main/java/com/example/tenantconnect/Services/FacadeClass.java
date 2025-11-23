package com.example.tenantconnect.Services;


import com.example.tenantconnect.Services.NotificationService;
import com.example.tenantconnect.Services.TenantService;
import com.example.tenantconnect.Services.PropertyService;
import com.example.tenantconnect.Services.PaymentService;
import com.example.tenantconnect.Services.ContractService;
import com.example.tenantconnect.Services.ComplaintService;

public class FacadeClass {
    NotificationService notificationService;
    PaymentService paymentService;
    ContractService contractService;
    ComplaintService complaintService;
    PropertyService propertyService;
    TenantService tenantService;
    DashboardService dashboardService;

    public static int CURRENT_USER_ID;

    public static FacadeClass instance;
    private FacadeClass(){
        notificationService = new NotificationService();
        tenantService = new TenantService();
        paymentService = new PaymentService(notificationService, tenantService);
        contractService = new ContractService(notificationService);
        complaintService = new ComplaintService(notificationService);
        propertyService = new PropertyService(contractService,notificationService);
        dashboardService = new DashboardService();
    }

    public static FacadeClass getInstance(){
        if(instance==null){
            instance = new FacadeClass();
        }
        return instance;
    }

    public NotificationService getNotificationService() {
        return notificationService;
    }
    public PaymentService getPaymentService() {
        return paymentService;
    }
    public ContractService getContractService() {
        return contractService;
    }
    public ComplaintService getComplaintService() {
        return complaintService;
    }
    public PropertyService getPropertyService() {
        return propertyService;
    }
    public TenantService getTenantService() {
        return tenantService;
    }
    public DashboardService getDashboardService() {return dashboardService;}
}
