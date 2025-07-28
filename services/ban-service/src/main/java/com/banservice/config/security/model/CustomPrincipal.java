package com.banservice.config.security.model;

import lombok.Getter;

@Getter
public class CustomPrincipal {
    private final long id;

    public CustomPrincipal(String id) {
        this.id = Long.parseLong(id);
    }
}
