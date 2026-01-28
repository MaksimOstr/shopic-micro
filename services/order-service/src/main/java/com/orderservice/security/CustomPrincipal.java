package com.orderservice.security;

import lombok.Getter;

import java.util.UUID;

@Getter
public class CustomPrincipal {
    private final UUID id;

    public CustomPrincipal(UUID id) {
        this.id = id;
    }
}

