package com.example.tenantconnect.Services;

import com.example.tenantconnect.Domain.Property;
import com.example.tenantconnect.Repositories.PropertyRepository;
import com.example.tenantconnect.Services.ContractService;
import java.util.ArrayList;
import java.util.List;

public class PropertyService {
    PropertyRepository propertyRepository;
    ContractService contractService;
    public PropertyService() {
        this.propertyRepository =new PropertyRepository() ; // to see if singelton need to be applied here
        this.contractService = new ContractService();
    }
    public List<Property> getOwnerProperties(int ownerId) {
        return propertyRepository.getPropertiesByOwnerId(ownerId);
    }
    public boolean addProperty(int ownerId,Property p) {
        return propertyRepository.addProperty(ownerId,p.getProperty_name(),p.getAddress(),p.getCity(),p.getState(),p.getZip_code(),p.getProperty_type(),p.getRent_amount(),p.getSecurity_deposit(),p.getStatus());
    }
    public boolean deleteProperty(int ownerId,int propertyId) {
        if(contractService.isContractActive(propertyId)){
            return false;
        }
        return propertyRepository.deleteProperty(ownerId,propertyId);
    }
    public boolean updateProperty(int ownerId,Property p) {
        if(contractService.isContractActive(p.getProperty_id())) {
            return false;
        }
        return propertyRepository.updateStatus(p.getStatus(),p.getProperty_id(),ownerId);
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
