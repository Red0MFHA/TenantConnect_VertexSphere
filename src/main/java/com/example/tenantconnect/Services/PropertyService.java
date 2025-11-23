package com.example.tenantconnect.Services;

import com.example.tenantconnect.Domain.Contract;
import com.example.tenantconnect.Domain.Property;
import com.example.tenantconnect.Domain.PropertyAssignment;
import com.example.tenantconnect.Repositories.PropertyRepository;
import com.example.tenantconnect.Services.ContractService;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PropertyService {
    PropertyRepository propertyRepository;
    ContractService contractService;
    NotificationService notificationService;

    public PropertyService(ContractService contractService,NotificationService notificationService) {
        this.propertyRepository =new PropertyRepository() ; // to see if singelton need to be applied here
        this.contractService =contractService;
        this.notificationService =notificationService;
    }
    public List<Property> getOwnerProperties(int ownerId) {
        return propertyRepository.getPropertiesByOwnerId(ownerId);
    }


    public List<Property> getTenantProperties(int tenantId) {
        // Get all assignments for this tenant
        List<PropertyAssignment> assignments = contractService.getTenantAssignments(tenantId);

        // Extract the property IDs assigned to this tenant
        Set<Integer> tenantPropertyIds = assignments.stream()
                .map(a -> a.propertyId) // access public field directly
                .collect(Collectors.toSet());

        // Get all properties
        List<Property> allProperties = propertyRepository.getAllProperties();

        // Filter properties whose IDs are in tenantPropertyIds
        List<Property> tenantProperties = allProperties.stream()
                .filter(p -> tenantPropertyIds.contains(p.getProperty_id())) // use getter if field is private
                .collect(Collectors.toList());

        return tenantProperties;
    }

    public boolean addProperty(int ownerId,Property p) {
        boolean b=propertyRepository.addProperty(ownerId,p.getProperty_name(),p.getAddress(),p.getCity(),p.getState(),p.getZip_code(),p.getProperty_type(),p.getRent_amount(),p.getSecurity_deposit(),p.getStatus());
        if(b==true){
            notificationService.sendPropertyNotification(ownerId,p.getProperty_id(),"Property Addded Successfully");
        }
        return b;
    }
    public boolean deleteProperty(int propertyId, int ownerId) {
        if(contractService.isContractActive(propertyId)){
            return false;
        }
        boolean b=propertyRepository.deleteProperty(ownerId,propertyId);
        if(b==true){
            notificationService.sendPropertyNotification(ownerId,propertyId,"Property Deleted Successfully");
        }
        return b;
    }

    public boolean updateProperty(int ownerId,Property p) {
        if(contractService.isContractActive(p.getProperty_id())) {
            return false;
        }
        boolean b=propertyRepository.updateStatus(p.getStatus(),p.getProperty_id(),ownerId);
        if(b==true){
            notificationService.sendPropertyNotification(ownerId,p.getProperty_id(),"Property Update Successfully");
        }
        return  b;
    }
    public List<Property> getAvailableProperties(int ownerId) {
        return propertyRepository.getAllVacantProperties(ownerId);
    }
    public boolean validatePropertyAvailability(int ownerId,int propertyId) {
        List<Property> availableProperties = getAvailableProperties(ownerId);
        for (Property property : availableProperties) {
            if(property.getProperty_id() == propertyId) {
                return true;
            }
        }
        return false;
    }

}
