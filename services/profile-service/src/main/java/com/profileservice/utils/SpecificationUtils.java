package com.profileservice.utils;

import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public class SpecificationUtils {
    public static <T> Specification<T> equalsLong(String field, Long value) {
        if (value == null) {
            return Specification.where(null);
        }
        return (root, query, cb) -> cb.equal(root.get(field), value);
    }
}
