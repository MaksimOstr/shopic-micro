package com.mailservice.dto.event;

public record UserBannedEvent(
        String email,
        String reason
) {}
