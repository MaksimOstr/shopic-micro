package com.userservice.utils;

import com.userservice.entity.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;


@UtilityClass
public class SpecificationUtils {
    public static Specification<User> iLike(String field, String value) {
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

    public static Specification<User> equalsId(Long value) {
        if (value == null) {
            return Specification.where(null);
        }
        return (root, query, cb) -> cb.equal(root.get("id"), value);
    }

    public static Specification<User> is(String field, Boolean value) {
        if (value == null) {
            return Specification.where(null);
        }

        return (root, query, cb) -> cb.equal(root.get(field), value);
    }
}
