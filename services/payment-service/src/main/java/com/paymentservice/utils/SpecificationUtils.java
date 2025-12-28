package com.paymentservice.utils;

import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public class SpecificationUtils {


    public static <T> Specification<T> iLike(String field, String value) {
        if (value == null || value.isBlank()) {
            return Specification.where(null);
        }
        return (root, query, cb) ->
                cb.like(cb.lower(root.get(field)), "%" + value.toLowerCase() + "%");
    }

    public static <T> Specification<T> equalsField(String fieldName, Object value) {
        return (root, query, cb) -> value != null ? cb.equal(root.get(fieldName), value) : null;
    }


    public static <T, V extends Comparable<? super V>> Specification<T> gte(String field, V value) {
        if (value == null) {
            return Specification.where(null);
        }
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get(field), value);
    }

    public static <T, V extends Comparable<? super V>> Specification<T> lte(String field, V value) {
        if (value == null) {
            return Specification.where(null);
        }
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get(field), value);
    }
}
