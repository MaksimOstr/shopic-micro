package com.authservice.dto.event;

public record OAuthUserCreated(
        long userId,
        String fistName,
        String lastName
) {
}
