package com.authservice.utils;


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

    public static <T, E extends Enum<E>> Specification<T> equalsEnum(String fieldName, E value) {
        if (value == null) {
            return Specification.where(null);
        }
        return (root, query, cb) -> cb.equal(root.get(fieldName), value);
    }

    public static <T> Specification<T> equalsId(Long value) {
        if (value == null) {
            return Specification.where(null);
        }
        return (root, query, cb) -> cb.equal(root.get("id"), value);
    }
}

