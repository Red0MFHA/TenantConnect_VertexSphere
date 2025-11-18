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
}
