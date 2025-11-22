package com.example.tenantconnect.Services;

import com.example.tenantconnect.Domain.Complaint;
import com.example.tenantconnect.Repositories.ComplaintRepository;
import com.example.tenantconnect.Services.NotificationService;
import java.util.*;
import java.util.stream.Collectors;

public class ComplaintService {

    private ComplaintRepository complaintRepo;
    private NotificationService notificationService;
    public ComplaintService(NotificationService notificationService) {
        this.complaintRepo = new ComplaintRepository();
        this.notificationService = notificationService;
    }

    public List<Complaint> getDueComplaintsByOwner(int ownerId) {

        // STEP 1: Get all complaints for this owner
        List<Complaint> complaints = complaintRepo.getComplaintsByOwner(ownerId);

        // STEP 2: Filter only unresolved complaints
        List<Complaint> unresolved = complaints.stream()
                .filter(c -> !c.getStatus().equalsIgnoreCase("resolved")
                        && !c.getStatus().equalsIgnoreCase("rejected"))
                .collect(Collectors.toList());

        // STEP 3: Sort by priority (custom order)
        unresolved.sort(Comparator.comparingInt(c -> priorityRank(c.getPriority())));

        return unresolved;
    }

    // Priority Ranking Helper Method
    private int priorityRank(String priority) {
        return switch (priority.toLowerCase()) {
            case "urgent" -> 1;
            case "high"   -> 2;
            case "medium" -> 3;
            case "low"    -> 4;
            default       -> 5;
        };
    }

    public boolean updateComplaintStatus(int tenantID,int complaintId, String new_status,String resolutionNotes) {
        boolean b=  complaintRepo.updateStatus(complaintId,new_status);
        if(b){
            notificationService.sendCOmplaintUpdationNotification(tenantID,complaintId,resolutionNotes);
        }
        return b;
    }

    public boolean FileComplaint(int tenantID,Complaint complaint) {
        if(complaint==null){
            return false;
        }
        complaintRepo.addComplaint(complaint);
        return true;
    }
}
