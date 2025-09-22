package com.productservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductAdminSortEnum {
    PRICE("price"),
    CREATED_AT("createdAt");

    private final String field;
}
