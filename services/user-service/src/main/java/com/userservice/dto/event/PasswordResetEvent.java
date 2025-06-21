package com.userservice.dto.event;

public record PasswordResetEvent(
        String email,
        String code
) {}
