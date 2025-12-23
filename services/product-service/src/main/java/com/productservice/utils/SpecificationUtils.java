package com.productservice.utils;

import com.productservice.entity.Product;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpecificationUtils {
    public static <T> Specification<T> iLike(String field, String value) {
        if (value == null || value.isBlank()) {
            return Specification.where(null);
        }
        return (root, query, cb) ->
                cb.like(cb.lower(root.get(field)), "%" + value.toLowerCase() + "%");
    }

    public static <T> Specification<T> hasChild(String field, Number value) {
        if (value == null) {
            return Specification.where(null);
        }

        return (root, query, cb) -> cb.equal(root.get(field).get("id"), value);
    }

    public static <T> Specification<T> equalsField(String fieldName, Object value) {
        return (root, query, cb) -> value != null ? cb.equal(root.get(fieldName), value) : null;
    }


    public static <T, E extends Comparable<? super E>> Specification<T> gte(String field, E value) {
        if (value == null) {
            return Specification.where(null);
        }
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get(field), value);
    }

    public static <T, E extends Comparable<? super E>> Specification<T> lte(String field, E value) {
        if (value == null) {
            return Specification.where(null);
        }
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get(field), value);
    }
}
