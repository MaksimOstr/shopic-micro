package com.productservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.productservice.exceptions.NotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductAdminSortByEnum {
    ID("id"),
    PRICE("price"),
    CREATED_AT("createdAt");

    private final String field;

    @JsonCreator
    public static ProductAdminSortByEnum fromString(String name) {
        try {
            String uppercaseName = name.toUpperCase();
            return ProductAdminSortByEnum.valueOf(uppercaseName);
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("Provided sort by param " + name + " is not supported");
        }
    }
}
