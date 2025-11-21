package com.example.tenantconnect.Services;

import com.example.tenantconnect.Domain.Tenant;
import com.example.tenantconnect.Repositories.UserRepository;
public class TenantService {
    UserRepository userRepository;
    public TenantService() {
        userRepository = new UserRepository();
    }

    public int getTenantKey(String email){
        return userRepository.getUserByEmail(email);
    }
    public boolean validateTenantKey(String email){
        return userRepository.getUserByEmail(email) != -1;
    }
    public boolean isTenant(int tenant_id){
        return userRepository.isTenant(tenant_id);
    }
}
