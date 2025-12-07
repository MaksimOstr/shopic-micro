package com.authservice.services;

import com.authservice.entity.UserRolesEnum;

public interface JwtService {
    String generateToken(String subject, UserRolesEnum userRole);
}
