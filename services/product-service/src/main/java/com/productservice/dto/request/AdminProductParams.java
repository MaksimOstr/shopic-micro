package com.productservice.dto.request;

import lombok.Getter;

import java.math.BigDecimal;


@Getter
public class AdminProductParams extends ProductParams {

    private final Boolean enabled;

    public AdminProductParams(String name, BigDecimal fromPrice, BigDecimal toPrice, Integer brandId, Integer categoryId, Boolean enabled) {
        super(name, fromPrice, toPrice, brandId, categoryId);
        this.enabled = enabled;
    }
}
