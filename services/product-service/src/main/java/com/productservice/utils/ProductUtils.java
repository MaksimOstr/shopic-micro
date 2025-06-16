package com.productservice.utils;

import com.productservice.dto.request.ProductParams;
import com.productservice.entity.Product;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

import static com.productservice.utils.SpecificationUtils.*;
import static com.productservice.utils.SpecificationUtils.gte;
import static com.productservice.utils.SpecificationUtils.hasChild;

@UtilityClass
public class ProductUtils {
    public static final String PRODUCT_NOT_FOUND = "Product Not Found";

    public static Specification<Product> buildSpecification(ProductParams params, Boolean enabled) {
        return iLike("name", params.getName())
                .and(hasActiveStatus("enabled", enabled))
                .and(lte("price", params.getToPrice()))
                .and(gte("price", params.getFromPrice()))
                .and(hasChild("category", params.getCategoryId()))
                .and(hasChild("brand", params.getBrandId()));
    }
}
