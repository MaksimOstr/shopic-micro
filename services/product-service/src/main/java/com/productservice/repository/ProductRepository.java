package com.productservice.repository;

import com.productservice.entity.Product;
import com.productservice.entity.ProductStatusEnum;
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
    Optional<Product> findByIsDeletedAndId(boolean isDeleted, UUID id);

    @Query("SELECT p FROM Product p JOIN FETCH p.category c JOIN FETCH p.brand b WHERE p.id = :id")
    Optional<Product> getProductWithCategoryAndBrand(UUID id);

    @Query("SELECT p FROM Product p WHERE p.id IN :ids")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Product> findByIdInWithLock(@Param("ids") Collection<UUID> ids);

    List<Product> findByIdInAndIsDeleted(Collection<UUID> ids, boolean isDeleted);

    @EntityGraph(attributePaths = {"category", "brand"})
    Page<Product> findAll(@Nullable Specification<Product> spec, Pageable pageable);
}
