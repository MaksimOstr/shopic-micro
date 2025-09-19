package com.productservice.utils;

import com.productservice.entity.Product;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@UtilityClass
public class ProductUtils {
    public static final String PRODUCT_NOT_FOUND = "Product not found";

    public static Map<Long, Product> toProductMap(List<Product> products) {
        return products.stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));
    }
}
