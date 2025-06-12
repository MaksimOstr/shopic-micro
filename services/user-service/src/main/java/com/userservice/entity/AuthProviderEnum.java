package com.userservice.entity;


import com.userservice.exceptions.NotFoundException;

public enum AuthProviderEnum {
    LOCAL, GOOGLE;

    public static AuthProviderEnum fromString(String name) {
        try {
            String uppercaseName = name.toUpperCase();
            return AuthProviderEnum.valueOf(uppercaseName);
        } catch (IllegalArgumentException e) {
            throw new NotFoundException(e.getMessage());
        }
    }
}
