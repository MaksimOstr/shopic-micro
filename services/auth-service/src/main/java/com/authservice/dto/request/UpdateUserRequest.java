package com.authservice.dto.request;

public record UpdateUserRequest(
        String lastName,
        String firstName
) {
}
