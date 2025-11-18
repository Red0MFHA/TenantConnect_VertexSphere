package com.example.tenantconnect.Services;

import com.example.tenantconnect.Domain.Contract;
import com.example.tenantconnect.Repositories.ContractRepository;

import java.util.List;

public class ContractService {
    ContractRepository contractRepository;
    public ContractService() {
        this.contractRepository = new ContractRepository();
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
    public int createContract(Contract contract){
        boolean b=contractRepository.addContract(contract.getProperty_id(),contract.getTenant_id(),contract.getStart_date(),contract.getEnd_date(),contract.getMonthly_rent(),contract.getSecurity_deposit(),"pending");
        if(b){
            List<Contract> activeContracts = contractRepository.getContractsByPropertyAndTenant(contract.getProperty_id(),contract.getTenant_id());
            for (Contract con : activeContracts) {
                if(con.getContract_status().equals("pending")){
                    return con.getContract_id();
                }
            }
        }
        return -1;
    }

}
