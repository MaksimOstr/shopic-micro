package com.orderservice.utils;


import com.orderservice.entity.Order;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public class SpecificationUtils {
    public static Specification<Order> iLikeNested(String field, String nested, String value) {
        if (value == null || value.isBlank()) {
            return Specification.where(null);
        }
        return (root, query, cb) ->
                cb.like(cb.lower(root.get(nested).get(field)), "%" + value.toLowerCase() + "%");
    }

    public static Specification<Order> hasId(String field, Long value) {
        if (value == null) {
            return Specification.where(null);
        }

        return (root, query, cb) -> cb.equal(root.get(field), value);
    }

    public static <T, E extends Enum<E>> Specification<Order> equalsEnum(String fieldName, E value) {
        if (value == null) {
            return Specification.where(null);
        }
        return (root, query, cb) -> cb.equal(root.get(fieldName), value);
    }

    public static <T extends Comparable<? super T>> Specification<Order> gte(String field, T value) {
        if (value == null) {
            return Specification.where(null);
        }
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get(field), value);
    }

    public static <T extends Comparable<? super T>> Specification<Order> lte(String field, T value) {
        if (value == null) {
            return Specification.where(null);
        }
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get(field), value);
    }

}
