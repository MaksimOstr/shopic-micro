package com.authservice.services.user;

import com.authservice.entity.Role;
import com.authservice.exceptions.NotFoundException;
import com.authservice.repositories.RoleRepository;
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