package com.productservice.entity;

import com.productservice.exceptions.InvalidEnumArgException;

public enum ProductCategoryEnum {
    ELECTRONICS, CLOTHES, BOOKS, COSMETICS;

    public static ProductCategoryEnum fromString(String name) {
       try {
           String uppercaseName = name.toUpperCase();
           return ProductCategoryEnum.valueOf(uppercaseName);
       } catch (IllegalArgumentException e) {
           throw new InvalidEnumArgException("Invalid product category " + name);
       }
    }
}
