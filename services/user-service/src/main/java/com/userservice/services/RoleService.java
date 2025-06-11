package com.userservice.services;

import com.userservice.entity.Role;
import com.userservice.exceptions.NotFoundException;
import com.userservice.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public Role getDefaultUserRole() {
        return roleRepository.findById(1)
                .orElseThrow(() -> new NotFoundException("User role not found"));
    }

}
