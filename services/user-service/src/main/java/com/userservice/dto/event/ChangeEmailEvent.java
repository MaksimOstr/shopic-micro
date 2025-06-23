package com.userservice.dto.event;

public record ChangeEmailEvent(
        String code,
        String email
) {}
