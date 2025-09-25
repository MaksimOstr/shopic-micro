package com.productservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.productservice.exceptions.NotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CategoryAdminSortByEnum {
    ID("id");

    private final String field;

    @JsonCreator
    public static CategoryAdminSortByEnum fromString(String name) {
        try {
            String uppercaseName = name.toUpperCase();
            return CategoryAdminSortByEnum.valueOf(uppercaseName);
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("Provided sort by param " + name + " is not supported");
        }
    }
}
