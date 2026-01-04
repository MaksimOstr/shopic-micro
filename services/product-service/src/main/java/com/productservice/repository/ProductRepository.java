package com.productservice.repository;

import com.productservice.entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {
    @Query("""
                SELECT p
                FROM Product p
                LEFT JOIN FETCH p.brand b
                JOIN FETCH p.category c
                WHERE p.id IN :ids
                  AND p.isDeleted = false
                  AND (b IS NULL OR b.isActive = true)
                  AND c.isActive = true
            """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Product> findActiveByIdInWithLock(@Param("ids") Collection<UUID> ids);

    @Query("SELECT p FROM Product p " +
            "LEFT JOIN p.brand b " +
            "JOIN p.category c " +
            "WHERE p.id IN :ids " +
            "AND p.isDeleted = false " +
            "AND (b IS NULL OR b.isActive = true)" +
            "AND c.isActive = true")
    List<Product> findAllActiveByIdList(Collection<UUID> ids);

    @Query("SELECT p FROM Product p " +
            "LEFT JOIN FETCH p.brand b " +
            "JOIN FETCH p.category c " +
            "WHERE p.id = :id " +
            "AND p.isDeleted = false " +
            "AND (b IS NULL OR b.isActive = true)" +
            "AND c.isActive = true")
    Optional<Product> findActiveWithCategoryAndBrandById(UUID id);


    @EntityGraph(attributePaths = {"category", "brand"})
    Page<Product> findAll(@Nullable Specification<Product> spec, Pageable pageable);
}
