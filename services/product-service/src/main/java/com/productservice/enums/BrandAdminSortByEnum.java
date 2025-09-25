package com.productservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.productservice.exceptions.NotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BrandAdminSortByEnum {
    ID("id");

    private final String field;

    @JsonCreator
    public static BrandAdminSortByEnum fromString(String name) {
        try {
            String uppercaseName = name.toUpperCase();
            return BrandAdminSortByEnum.valueOf(uppercaseName);
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("Provided sort by param " + name + " is not supported");
        }
    }
}
