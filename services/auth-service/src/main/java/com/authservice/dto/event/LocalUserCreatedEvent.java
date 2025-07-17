package com.authservice.dto.event;

public record LocalUserCreatedEvent(
        String email,
        String code,
        long userId,
        String firstName,
        String lastName
) {}
