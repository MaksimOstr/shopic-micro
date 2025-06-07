package com.userservice.dto.response;

import java.util.List;

public record CreateOAuthUserResponseDto(
        long userId,
        String email,
        List<String> roleNames
) {}
