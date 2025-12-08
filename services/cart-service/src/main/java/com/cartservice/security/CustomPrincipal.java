package com.cartservice.security;

import lombok.Getter;

import java.util.UUID;

@Getter
public class CustomPrincipal {
    private final UUID id;

    public CustomPrincipal(String id) {
        this.id = UUID.fromString(id);
    }
}

