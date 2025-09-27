package com.orderservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderAdminSortByEnum {
    ID("id"),
    CREATED_AT("created_at"),
    UPDATED_AT("updated_at"),
    TOTAL_PRICE("total_price");

    private final String field;
}
