package com.example.tenantconnect.Services;

import com.example.tenantconnect.Domain.Contract;
import com.example.tenantconnect.Repositories.ContractRepository;
import com.example.tenantconnect.Services.NotificationService;

import java.util.List;

public class ContractService {
    ContractRepository contractRepository;
    NotificationService notificationService;
    public ContractService() {
        this.contractRepository = new ContractRepository();
        this.notificationService = new NotificationService();
    }

    public boolean isContractActive(int propertyId){
        List<Contract> activeContracts = contractRepository.getContractsByProperty(propertyId);
        for (Contract contract : activeContracts) {
            if( contract.getProperty_id()==propertyId && contract.getContract_status().equals("active")) {
                return true; //active contract can not remove property
            }
        }
        return false;
    }
    public int createNewContract(int owner_id,Contract contract){
        boolean b=contractRepository.addContract(contract.getProperty_id(),contract.getTenant_id(),contract.getStart_date(),contract.getEnd_date(),contract.getMonthly_rent(),contract.getSecurity_deposit(),"pending");
        if(b){
            List<Contract> activeContracts = contractRepository.getContractsByPropertyAndTenant(contract.getProperty_id(),contract.getTenant_id());
            for (Contract con : activeContracts) {
                if(con.getContract_status().equals("pending")){
                    notificationService.sendContractCreationionNotificationToOwner(owner_id,contract.getContract_id());
                    notificationService.sendContractCreationionNotificationToTenant(contract.getTenant_id(),contract.getContract_id());
                    return con.getContract_id();
                }
            }
        }
        return -1;
    }
    public List<Contract> getContractsByOwner(int owner_id){
        return contractRepository.getContractByOwner(owner_id);
    }

    public Contract getContractDetails(int contract_id){
        return contractRepository.getContractById(contract_id);
    }
    public boolean UpdateContract(Contract contract){
        boolean b=contractRepository.updateContract(contract);
        if(b){
            notificationService.sendContractUpdateionNotification(contract.getTenant_id(),contract.getContract_id(),"_");
        }
        return  b;
    }
    public boolean DeleteContract(int owner_id,int contract_id){
        List<Contract> conts =getContractsByOwner(owner_id);
        boolean notAuthorizedOwner=true;
        for(Contract con : conts){
            if(con.getContract_id()==contract_id){
                notAuthorizedOwner=false;
            }
        }
        if(notAuthorizedOwner){
            return false;
        }
        boolean b=contractRepository.deleteContract(contract_id);
        if(b){
            notificationService.sendDeletionNotificationCOntract(owner_id,contract_id);
        }
        return b;
    }

   public List<Contract> getPendingAssignments(int tenant_id){
        return contractRepository.getPendingContractsByTenant(tenant_id);
    }


   //change contract status
   public void acceptContract(int tenant_id,Contract contract){
       List<Contract> conts =getPendingAssignments(tenant_id);
       boolean notAuthorizedOwner=true;
       for(Contract con : conts){
           if(con.getContract_id()==contract.getContract_id()){
               notAuthorizedOwner=false;
           }
       }
       if(notAuthorizedOwner){
           return;
       }
       int owner_id=contractRepository.getOwnerIdByContractId(contract.getContract_id());
       boolean b= contractRepository.updateContractStatus("active",contract.getContract_id());
       if(b){
           notificationService.sendContractUpdateionNotification(contract.getTenant_id(),contract.getContract_id(),"Contract Accepted");
           if(owner_id!=-1){
               notificationService.sendContractUpdateionNotification(owner_id,contract.getContract_id(),"Contract Accepted");
           }
       }
   }
   public void rejectContract(int tenant_id,Contract contract){
       List<Contract> conts =getPendingAssignments(tenant_id);
       boolean notAuthorizedOwner=true;
       for(Contract con : conts){
           if(con.getContract_id()==contract.getContract_id()){
               notAuthorizedOwner=false;
           }
       }
       if(notAuthorizedOwner){
           return;
       }
       int owner_id=contractRepository.getOwnerIdByContractId(contract.getContract_id());
       boolean b= contractRepository.updateContractStatus("rejected",contract.getContract_id());
       if(b){
           notificationService.sendContractUpdateionNotification(contract.getTenant_id(),contract.getContract_id(),"Contract Rejected");
           if(owner_id!=-1){
               notificationService.sendContractUpdateionNotification(owner_id,contract.getContract_id(),"Contract Rejected");
           }
       }
   }
   public void terminateContract(int owner_id,Contract contract){
       List<Contract> conts =getContractsByOwner(owner_id);
       boolean notAuthorizedOwner=true;
       for(Contract con : conts){
           if(con.getContract_id()==contract.getContract_id()){
               notAuthorizedOwner=false;
           }
       }
       if(notAuthorizedOwner){
           return;
       }
       boolean b= contractRepository.updateContractStatus("terminated",contract.getContract_id());
       if(b){
           notificationService.sendContractUpdateionNotification(contract.getTenant_id(),contract.getContract_id(),"Contract Terminated");
           notificationService.sendContractUpdateionNotification(owner_id,contract.getContract_id(),"Contract Terminated");
       }

   }
}
