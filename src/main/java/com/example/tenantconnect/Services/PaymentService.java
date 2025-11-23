package com.example.tenantconnect.Services;

import com.example.tenantconnect.Domain.Payment;
import com.example.tenantconnect.Domain.PaymentExtension;
import com.example.tenantconnect.Repositories.PaymentRepository;
import com.example.tenantconnect.Services.TenantService;
import com.example.tenantconnect.Services.NotificationService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.List;
import com.example.tenantconnect.UIcontrollers.RentTrackingController;
public class PaymentService
{
    PaymentRepository paymentRepository ;
    TenantService tenantService;
    NotificationService notificationService;
    public PaymentService(NotificationService notificationService, TenantService tenantService){
        this.paymentRepository = new PaymentRepository();
        this.tenantService = tenantService;
        this.notificationService =notificationService;
    }
    public List<Payment> getDuePaymentsForOwner(int owner_id)
    {
        return paymentRepository.getDuePaymentsByOwner(owner_id);
    }

    public List<Payment> getDuePaymentsForTenant(int tenant_id)
    {
        return paymentRepository.getDuePaymentsByTenant(tenant_id);
    }

    public boolean updatePaymentToPaid(int tenantId, int paymentId) {
        boolean b=paymentRepository.markPaymentAsPaid(tenantId, paymentId);
        int ownerID = paymentRepository.getOwnerIdByPaymentId(paymentId);
        if(b){
            notificationService.sendPaymentPaidNotification(tenantId,paymentId,"Payment paid successfully");
            if(ownerID!=-1){
                notificationService.sendPaymentPaidNotification(ownerID,paymentId,"Rent's payment Recieved successfully");

            }
        }
        else{
            notificationService.sendPaymentPaidNotification(tenantId,paymentId,"Payment failed");
        }
        return b;
    }




    public List<Payment> getOwnerRentPayments(int owner_id)
    {
        List<Payment> payments = getDuePaymentsForOwner(owner_id);

        // Getting current month
        String currentMonth = LocalDate.now()
                .getMonth()
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Remove payments that are NOT in the current month
        payments.removeIf(payment -> {
            try {
                String createdAt = payment.getCreated_at();

                LocalDateTime dateTime = LocalDateTime.parse(createdAt, formatter);

                String paymentMonth = dateTime.getMonth()
                        .getDisplayName(TextStyle.FULL, Locale.ENGLISH);

                // return TRUE if we want to remove this payment
                return !currentMonth.equalsIgnoreCase(paymentMonth);

            } catch (Exception e) {
                // If date is invalid, safely remove it
                return true;
            }
        });

        return payments;
    }

    public List<Payment> reqApplyFilter(int owner_id,String Filter)
    {
        return paymentRepository.getDuePaymentsByOwner(owner_id); //to reapply
    }

    public boolean sendReminder(int tenant_id,String Message){
        //first CHecking if the tennat is valid
        if(!tenantService.isTenant(tenant_id))
            return false;
        //then checking if the tenant has a due payment or payments
        List<Payment> payments= paymentRepository.getDuePaymentsByTenant(tenant_id);
        for(Payment payment:payments){
            if(payment.getPayment_status().equals("overdue") || payment.getPayment_status().equals("pending")){
                notificationService.sendPaymentNotification(tenant_id,payment.getPayment_id(),Message);
            }
        }
        return true;
    }

    // new function
    public List<RentTrackingController.RentTableItem> getRentTrackingDataForOwner(int owner_id) {
        // The repository handles the mapping into the Controller's inner class
        return paymentRepository.getOwnerRentTrackingData(owner_id);
    }
    public List<Payment> getRentHistory(int user_id,String usrType){
        if(usrType.equals("owner")){
            return paymentRepository.getHistoryPaymentsByOwner(user_id);
        }
        else{
            return paymentRepository.getHistoryPaymentsByTenant(user_id);
        }
    }
    public List<Payment> applyFilter(int user_id,String usrType,String Filter){
        return getRentHistory(user_id,usrType);//to modify
    }

    public List<PaymentExtension> getPendingExtensionRequests(int user_id,String usrType){
        if(usrType.equals("owner")){
            return paymentRepository.getUnresolvedExtensionsByOwner(user_id);
        }
        else{
            return paymentRepository.getUnresolvedExtensionsByTenant(user_id);
        }
    }

//    public Boolean approveExtentionRequest(int extension_ID,String Message){
//        PaymentExtension paymentExtension = paymentRepository.getPaymentExtensionByExtensionID(extension_ID);
//        if(paymentExtension!=null){
//            return false;
//        }
//        paymentExtension.setStatus("approved");
//        //now adding new due date
//        paymentExtension.setCurrent_due_date(paymentExtension.getRequested_due_date());
//        //now sending the notification to tenant
//        notificationService.sendPaymentNotification(paymentExtension.getTenant_id(),paymentExtension.getPayment_id(),"approved : "+Message);
//        return true;
//    }
//    public Boolean rejectExtentionRequest(int extension_ID,String Message){
//        PaymentExtension paymentExtension = paymentRepository.getPaymentExtensionByExtensionID(extension_ID);
//        if(paymentExtension!=null){
//            return false;
//        }
//        paymentExtension.setStatus("rejected");
//        //now sending the notification to tenant
//        notificationService.sendPaymentNotification(paymentExtension.getTenant_id(),paymentExtension.getPayment_id(),"rejected : "+Message);
//        return true;
//    }
public Boolean approveExtentionRequest(int extension_ID,String Message){
    PaymentExtension paymentExtension = paymentRepository.getPaymentExtensionByExtensionID(extension_ID);

    if(paymentExtension == null){
        return false;
    }

    // 1. Update the extension status to 'approved'
    boolean extStatusUpdated = paymentRepository.updatePaymentExtensionStatus(extension_ID, "approved");

    // 2. Update the due date in the main 'payments' table
    boolean paymentDueUpdated = paymentRepository.updatePaymentDueDate(
            paymentExtension.getPayment_id(),
            paymentExtension.getRequested_due_date()
    );

    if(extStatusUpdated && paymentDueUpdated){
        notificationService.sendPaymentNotification(
                paymentExtension.getTenant_id(),
                paymentExtension.getPayment_id(),
                "Extension Approved: " + Message
        );
        return true;
    }
    return false;
}

    public Boolean rejectExtentionRequest(int extension_ID,String Message){
        PaymentExtension paymentExtension = paymentRepository.getPaymentExtensionByExtensionID(extension_ID);

        if(paymentExtension == null){
            return false;
        }

        // 1. Update the extension status to 'rejected'
        boolean extStatusUpdated = paymentRepository.updatePaymentExtensionStatus(extension_ID, "rejected");

        if(extStatusUpdated){
            notificationService.sendPaymentNotification(
                    paymentExtension.getTenant_id(),
                    paymentExtension.getPayment_id(),
                    "Extension Rejected: " + Message
            );
            // Note: No change to the main 'payments' due date is needed for rejection.
            return true;
        }
        return false;
    }
    public void requestExtension(int tenantID,PaymentExtension ext){
        List<Payment> pays=getDuePaymentsForTenant(ext.getTenant_id());
        for(Payment payment:pays){
            if((payment.getPayment_status().equals("pending") || payment.getPayment_status().equals("overdue")) && ext.getTenant_id()==tenantID && payment.getPayment_id()==ext.getPayment_id()){
                paymentRepository.addPaymentExtension(ext);
                notificationService.sendExtensionUpdationNotification(tenantID, ext.getExtension_id(), "sent");
                int ownerID = paymentRepository.getOwnerIdByPaymentId(ext.getPayment_id());
                if(ownerID!=-1){
                    notificationService.sendExtensionUpdationNotification(ownerID, ext.getExtension_id(), "recieved : "+tenantID+"has requested an extension in payment.");
                }
            }
        }

    }

}
