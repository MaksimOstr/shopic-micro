package com.profileservice.dto.event;

public record ProfileCreationEvent(
        long userId,
        String firstName,
        String lastName
) {
}
