package com.productservice.repository;

import com.productservice.entity.Product;
import com.productservice.projection.ProductDto;
import com.productservice.projection.ProductImageUrlProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    @Query("SELECT p.imageUrl as imageUrl FROM Product p WHERE p.id = :id")
    Optional<ProductImageUrlProjection> getProductImageUrl(long id);

    @EntityGraph(attributePaths = {"category", "brand"})
    Optional<Product> findById(long id);

    @Transactional
    @Modifying
    @Query("UPDATE Product p SET p.imageUrl = :imageUrl WHERE p.id = :id")
    int updateProductImageUrl(long id, String imageUrl);

    Optional<Product> findBySku(UUID sku);

    @Query("SELECT new com.productservice.projection.ProductDto(" +
            "p.id," +
            " p.name," +
            " p.description," +
            " p.sku," +
            " p.price," +
            " p.brand.name," +
            " p.category.name," +
            " p.stockQuantity) FROM Product p")
    Page<ProductDto> getPageOfProducts(Pageable pageable);

    @EntityGraph(attributePaths = {"category", "brand"})
    Page<Product> findAll(@Nullable Specification<Product> spec, Pageable pageable);
}
