package com.authservice.dto;

public record UpdateUserRequest(
        String lastName,
        String firstName
) {
}
