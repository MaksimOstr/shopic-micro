package com.banservice.utils;


import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public class SpecificationUtils {
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

    public static <T> Specification<T> hasChild(String field, Long value) {
        if (value == null) {
            return Specification.where(null);
        }
        return (root, query, cb) -> cb.equal(root.get(field).get("id"), value);
    }

    public static <T> Specification<T> is(String field, Boolean value) {
        if (value == null) {
            return Specification.where(null);
        }

        return (root, query, cb) -> cb.equal(root.get(field), value);
    }
}
