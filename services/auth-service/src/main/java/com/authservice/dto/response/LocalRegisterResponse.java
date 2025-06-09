package com.authservice.dto.response;


public record LocalRegisterResponse (
        long userId,
        String email,
        String firstName,
        String lastName
) {}
