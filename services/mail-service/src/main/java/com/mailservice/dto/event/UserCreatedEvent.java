package com.mailservice.dto.event;

public record UserCreatedEvent(
        String email,
        String code
) {}
