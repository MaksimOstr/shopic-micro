package com.banservice.dto.event;

public record UserBannedEvent(
        String email,
        String reason
) {
}
