package com.example.tenantconnect.Services;
import com.example.tenantconnect.Repositories.NotificationRepository;
import com.example.tenantconnect.Domain.Notification;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class NotificationService {
    NotificationRepository nr;
    public NotificationService(){
        nr=new NotificationRepository();
    }

    public NotificationService(NotificationRepository nr){
        this.nr=nr;
    }
    public void  readNotification(int notificationID){
        nr.markAsRead(notificationID);
    }
    public void sendAssignmentNotification(int tenantId,int contractId){
        Notification n=new Notification();
        n.setUser_id(tenantId);
        n.setTitle("Assignment Notification");
        n.setMessage("You have recieved an Assignment Request for a Property. Kindly Check Contract Approval Bar");
        n.setNotification_type("assignment");
        n.setIs_read(Boolean.FALSE);
        n.setRelated_entity_type("Contract");
        n.setRelated_entity_id(contractId);

        // Get the current LocalDateTime
        LocalDateTime currentDateTime = LocalDateTime.now();
        // Define the desired format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // Format the LocalDateTime to a String
        String formattedDate = currentDateTime.format(formatter);

        n.setCreated_at(formattedDate);

        nr.addNotification(n);
    }


    public void sendPaymentNotification(int tenantId,int payment_id,String message){
        Notification n=new Notification();
        n.setUser_id(tenantId);
        n.setTitle("Assignment Notification");
        n.setMessage(message);
        n.setNotification_type("Reminder");
        n.setIs_read(Boolean.FALSE);
        n.setRelated_entity_type("Payment");
        n.setRelated_entity_id(payment_id);

        // Get the current LocalDateTime
        LocalDateTime currentDateTime = LocalDateTime.now();
        // Define the desired format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // Format the LocalDateTime to a String
        String formattedDate = currentDateTime.format(formatter);

        n.setCreated_at(formattedDate);

        nr.addNotification(n);
    }
    public void sendPropertyNotification(int ownerID,int propertyID,String message){
        Notification n=new Notification();
        n.setUser_id(ownerID);
        n.setTitle("Assignment Notification");
        n.setMessage(message);
        n.setNotification_type("Notify DB");
        n.setIs_read(Boolean.FALSE);
        n.setRelated_entity_type("Property");
        n.setRelated_entity_id(propertyID);

        // Get the current LocalDateTime
        LocalDateTime currentDateTime = LocalDateTime.now();
        // Define the desired format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // Format the LocalDateTime to a String
        String formattedDate = currentDateTime.format(formatter);

        n.setCreated_at(formattedDate);

        nr.addNotification(n);
    }
    public void sendPaymentPaidNotification(int tenantId,int paymentID,String message){
        Notification n=new Notification();
        n.setUser_id(tenantId);
        n.setTitle("Assignment Notification");
        n.setMessage(message);
        n.setNotification_type("Payment Notify");
        n.setIs_read(Boolean.FALSE);
        n.setRelated_entity_type("Payment");
        n.setRelated_entity_id(paymentID);

        // Get the current LocalDateTime
        LocalDateTime currentDateTime = LocalDateTime.now();
        // Define the desired format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // Format the LocalDateTime to a String
        String formattedDate = currentDateTime.format(formatter);

        n.setCreated_at(formattedDate);

        nr.addNotification(n);
    }


    public void sendCOmplaintUpdationNotification(int tenantId,int complaintID,String msg){
        Notification n=new Notification();
        n.setUser_id(tenantId);
        n.setTitle("Assignment Notification");
        n.setMessage(msg);
        n.setNotification_type("Update");
        n.setIs_read(Boolean.FALSE);
        n.setRelated_entity_type("Complaint");
        n.setRelated_entity_id(complaintID);

        // Get the current LocalDateTime
        LocalDateTime currentDateTime = LocalDateTime.now();
        // Define the desired format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // Format the LocalDateTime to a String
        String formattedDate = currentDateTime.format(formatter);

        n.setCreated_at(formattedDate);

        nr.addNotification(n);
    }

    public void sendExtensionUpdationNotification(int tenantId,int extensionID,String msg){
        Notification n=new Notification();
        n.setUser_id(tenantId);
        n.setTitle("Assignment Notification");
        n.setMessage("Your request for Payment extension has been "+msg);
        n.setNotification_type("Notify");
        n.setIs_read(Boolean.FALSE);
        n.setRelated_entity_type("PaymentExtension");
        n.setRelated_entity_id(extensionID);

        // Get the current LocalDateTime
        LocalDateTime currentDateTime = LocalDateTime.now();
        // Define the desired format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // Format the LocalDateTime to a String
        String formattedDate = currentDateTime.format(formatter);

        n.setCreated_at(formattedDate);

        nr.addNotification(n);
    }
    public void sendContractUpdateionNotification(int tenantId,int contractID,String msg){
        Notification n=new Notification();
        n.setUser_id(tenantId);
        n.setTitle("Updation Notification");
        n.setMessage("Your Contract has been updated "+msg);
        n.setNotification_type("Notify");
        n.setIs_read(Boolean.FALSE);
        n.setRelated_entity_type("Contract");
        n.setRelated_entity_id(contractID);

        // Get the current LocalDateTime
        LocalDateTime currentDateTime = LocalDateTime.now();
        // Define the desired format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // Format the LocalDateTime to a String
        String formattedDate = currentDateTime.format(formatter);

        n.setCreated_at(formattedDate);

        nr.addNotification(n);
    }
    public void sendContractCreationionNotificationToOwner(int owner_id,int contractID){
        Notification n=new Notification();
        n.setUser_id(owner_id);
        n.setTitle("Updation Notification");
        n.setMessage("Your Contract has been Created ");
        n.setNotification_type("Notify");
        n.setIs_read(Boolean.FALSE);
        n.setRelated_entity_type("Contract");
        n.setRelated_entity_id(contractID);

        // Get the current LocalDateTime
        LocalDateTime currentDateTime = LocalDateTime.now();
        // Define the desired format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // Format the LocalDateTime to a String
        String formattedDate = currentDateTime.format(formatter);

        n.setCreated_at(formattedDate);

        nr.addNotification(n);
    }

    public void sendContractCreationionNotificationToTenant(int tenant_id,int contractID){
        Notification n=new Notification();
        n.setUser_id(tenant_id);
        n.setTitle("Updation Notification");
        n.setMessage("You have been invited to sign contract for a property.");
        n.setNotification_type("Notify");
        n.setIs_read(Boolean.FALSE);
        n.setRelated_entity_type("Contract");
        n.setRelated_entity_id(contractID);

        // Get the current LocalDateTime
        LocalDateTime currentDateTime = LocalDateTime.now();
        // Define the desired format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // Format the LocalDateTime to a String
        String formattedDate = currentDateTime.format(formatter);

        n.setCreated_at(formattedDate);

        nr.addNotification(n);
    }

    public void sendDeletionNotificationCOntract(int owner_id,int contractID){
        Notification n=new Notification();
        n.setUser_id(owner_id);
        n.setTitle("Deletion Notification");
        n.setMessage("Your Contract has been Deleted ");
        n.setNotification_type("Notify");
        n.setIs_read(Boolean.FALSE);
        n.setRelated_entity_type("Contract");
        n.setRelated_entity_id(contractID);

        // Get the current LocalDateTime
        LocalDateTime currentDateTime = LocalDateTime.now();
        // Define the desired format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // Format the LocalDateTime to a String
        String formattedDate = currentDateTime.format(formatter);

        n.setCreated_at(formattedDate);

        nr.addNotification(n);
    }



}
