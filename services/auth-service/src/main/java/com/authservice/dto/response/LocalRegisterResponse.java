package com.authservice.dto.response;

import com.authservice.enums.AuthProviderEnum;


public record LocalRegisterResponse (
        long userId,
        String email,
        String firstName,
        String lastName
) {}
