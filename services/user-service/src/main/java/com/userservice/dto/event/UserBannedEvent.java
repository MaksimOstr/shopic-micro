package com.userservice.dto.event;

public record UserBannedEvent(
        String email,
        String reason
) {
}
