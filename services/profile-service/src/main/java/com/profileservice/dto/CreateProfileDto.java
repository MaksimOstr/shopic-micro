package com.profileservice.dto;

public record CreateProfileDto (
        String firstName,
        String lastName,
        long userId
) {}