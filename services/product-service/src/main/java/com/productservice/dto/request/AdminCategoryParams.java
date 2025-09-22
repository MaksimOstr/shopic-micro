package com.productservice.dto.request;

public record AdminCategoryParams(
        Boolean isActive,
        String name
) {}
