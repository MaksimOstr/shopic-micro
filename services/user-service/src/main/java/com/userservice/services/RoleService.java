package com.userservice.services;

import com.userservice.entity.Role;
import com.userservice.exceptions.EntityDoesNotExist;
import com.userservice.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public Role getDefaultUserRole() {
        return roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new EntityDoesNotExist("User role not found"));
    }

}
