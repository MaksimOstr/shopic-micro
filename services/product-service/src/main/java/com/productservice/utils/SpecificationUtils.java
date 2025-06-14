package com.productservice.utils;

import com.productservice.entity.Product;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpecificationUtils {
    public static Specification<Product> iLike(String field, String value) {
        if (value == null || value.isBlank()) {
            return Specification.where(null);
        }
        return (root, query, cb) ->
                cb.like(cb.lower(root.get(field)), "%" + value.toLowerCase() + "%");
    }

    public static Specification<Product> hasChild(String field, Number value) {
        if (value == null) {
            return Specification.where(null);
        }

        return (root, query, cb) -> cb.equal(root.get(field).get("id"), value);
    }

    public static Specification<Product> hasActiveStatus(String field, Boolean value) {
        if (value == null) {
            return Specification.where(null);
        }

        return (root, query, cb) -> cb.equal(root.get(field), value);
    }

    public static <T extends Comparable<? super T>> Specification<Product> gte(String field, T value) {
        if (value == null) {
            return Specification.where(null);
        }
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get(field), value);
    }

    public static <T extends Comparable<? super T>> Specification<Product> lte(String field, T value) {
        if (value == null) {
            return Specification.where(null);
        }
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get(field), value);
    }
}
