package com.productservice.utils;

import com.productservice.dto.request.AdminProductParams;
import com.productservice.dto.request.UserProductParams;
import com.productservice.entity.Product;
import com.productservice.entity.ProductStatusEnum;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.productservice.utils.SpecificationUtils.*;
import static com.productservice.utils.SpecificationUtils.hasChild;

@UtilityClass
public class ProductUtils {
    public static final String PRODUCT_NOT_FOUND = "Product not found";

    public static Specification<Product> buildUserProductSpec(UserProductParams params) {
        return SpecificationUtils.<Product>iLike("name", params.productName())
                .and(equalsEnum("status", ProductStatusEnum.ACTIVE))
                .and(lte("price", params.toPrice()))
                .and(gte("price", params.fromPrice()))
                .and(hasChild("category", params.categoryId()))
                .and(hasChild("brand", params.brandId()));
    }

    public static Specification<Product> buildAdminProductSpec(AdminProductParams params) {
        return SpecificationUtils.<Product>iLike("name", params.productName())
                .and(equalsEnum("status", params.status()))
                .and(lte("price", params.toPrice()))
                .and(gte("price", params.fromPrice()))
                .and(hasChild("category", params.categoryId()))
                .and(hasChild("brand", params.brandId()));
    }

    public static Map<Long, Product> toProductMap(List<Product> products) {
        return products.stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));
    }
}
