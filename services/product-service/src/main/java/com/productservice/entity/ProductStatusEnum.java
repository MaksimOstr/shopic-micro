package com.productservice.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.productservice.exceptions.NotFoundException;

public enum ProductStatusEnum {
    ACTIVE,
    ARCHIVED,;

    @JsonCreator
    public static ProductStatusEnum fromString(String name) {
        try {
            String uppercaseName = name.toUpperCase();
            return ProductStatusEnum.valueOf(uppercaseName);
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("Provided status " + name + " is not supported");
        }
    }
}
