package com.authservice.utils;


import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
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

    public static <T, J> Specification<T> iLikeNested(String joinField, String targetField, String value, JoinType joinType) {
        if (value == null || value.isBlank()) {
            return Specification.where(null);
        }

        return (root, query, cb) -> {
            Join<T, J> joinedEntity = root.join(joinField, joinType);

            return cb.like(cb.lower(joinedEntity.get(targetField)), "%" + value.toLowerCase() + "%");
        };
    }

    public static <T> Specification<T> equalsId(Long value) {
        if (value == null) {
            return Specification.where(null);
        }
        return (root, query, cb) -> cb.equal(root.get("id"), value);
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

