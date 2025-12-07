package com.authservice.entity;

import com.authservice.exception.NotFoundException;

public enum AuthProviderEnum {
    LOCAL, GOOGLE;

    public static AuthProviderEnum fromString(String name) {
        try {
            String uppercaseName = name.toUpperCase();
            return AuthProviderEnum.valueOf(uppercaseName);
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("Provided provider " + name + " is not supported");
        }
    }
}
