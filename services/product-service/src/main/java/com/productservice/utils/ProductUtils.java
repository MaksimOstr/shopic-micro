package com.productservice.utils;

import com.productservice.dto.request.AdminProductParams;
import com.productservice.dto.request.UserProductParams;
import com.productservice.entity.Product;
import com.productservice.entity.ProductStatusEnum;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.productservice.utils.SpecificationUtils.*;
import static com.productservice.utils.SpecificationUtils.hasChild;

@UtilityClass
public class ProductUtils {
    public static final String PRODUCT_NOT_FOUND = "Product not found";

    public static Specification<Product> buildUserProductSpec(UserProductParams params) {
        return SpecificationUtils.<Product>iLike("name", params.productName())
                .and(equalsField("isDeleted", false))
                .and(hasChild("category", "isActive", true))
                .and(hasChild("brand", "isActive", true))
                .and(lte("price", params.toPrice()))
                .and(gte("price", params.fromPrice()))
                .and(hasChild("category", "id", params.categoryId()))
                .and(hasChild("brand", "id", params.brandId()));
    }

    public static Specification<Product> buildAdminProductSpec(AdminProductParams params) {
        return SpecificationUtils.<Product>iLike("name", params.productName())
                .and(equalsField("isDeleted", params.isDeleted()))
                .and(lte("price", params.toPrice()))
                .and(gte("price", params.fromPrice()))
                .and(hasChild("category", "id", params.categoryId()))
                .and(hasChild("brand", "id", params.brandId()));
    }

    public static Map<UUID, Product> toProductMap(List<Product> products) {
        return products.stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));
    }
}
