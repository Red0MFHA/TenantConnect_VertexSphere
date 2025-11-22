package com.example.tenantconnect.Services;

import com.example.tenantconnect.Domain.Property;
import com.example.tenantconnect.Repositories.PropertyRepository;
import com.example.tenantconnect.Services.ContractService;
import java.util.ArrayList;
import java.util.List;

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
    public boolean addProperty(int ownerId,Property p) {
        boolean b=propertyRepository.addProperty(ownerId,p.getProperty_name(),p.getAddress(),p.getCity(),p.getState(),p.getZip_code(),p.getProperty_type(),p.getRent_amount(),p.getSecurity_deposit(),p.getStatus());
        if(b==true){
            notificationService.sendPropertyNotification(ownerId,p.getProperty_id(),"Property Addded Successfully");
        }
        return b;
    }
    public boolean deleteProperty(int ownerId,int propertyId) {
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
