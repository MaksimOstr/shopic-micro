package com.authservice.dto.event;

public record UserCreatedEvent(
        String email,
        String code
) {}
