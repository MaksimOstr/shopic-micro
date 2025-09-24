package com.productservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.productservice.exceptions.NotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReservationAdminSortByEnum {
    UPDATED_AT("updated_at"),
    CREATED_AT("createdAt");

    private final String field;

    @JsonCreator
    public static ReservationAdminSortByEnum fromString(String name) {
        try {
            String uppercaseName = name.toUpperCase();
            return ReservationAdminSortByEnum.valueOf(uppercaseName);
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("Provided sort by param " + name + " is not supported");
        }
    }
}
