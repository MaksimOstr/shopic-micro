package com.productservice.repository;

import com.productservice.entity.Product;
import com.productservice.projection.ProductDto;
import com.productservice.projection.ProductForCartDto;
import com.productservice.projection.ProductPrice;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    @Query("SELECT p.imageUrl FROM Product p WHERE p.id = :id")
    Optional<String> getProductImageUrl(long id);

    @EntityGraph(attributePaths = {"category", "brand"})
    Optional<Product> findById(long id);

    @Transactional
    @Modifying
    @Query("UPDATE Product p SET p.imageUrl = :imageUrl WHERE p.id = :id")
    int updateProductImageUrl(long id, String imageUrl);

    Optional<Product> findBySku(UUID sku);

    @Query("SELECT new com.productservice.projection.ProductPrice(" +
            "p.id," +
            "p.price" +
            ")" +
            "FROM Product p WHERE p.id IN :ids")
    List<ProductPrice> findProductPrices(List<Long> ids);

    @Query("SELECT new com.productservice.projection.ProductDto(" +
            "p.id, " +
            "p.name, " +
            "p.description, " +
            "p.imageUrl," +
            "p.sku," +
            "p.price, " +
            "b.name, " +
            "c.name, " +
            "p.stockQuantity," +
            "p.enabled) " +
            "FROM Product p " +
            "JOIN p.brand b " +
            "JOIN p.category c")
    Page<ProductDto> getPageOfProducts(Pageable pageable);

    @Query("SELECT new com.productservice.projection.ProductDto(" +
            "p.id, " +
            "p.name, " +
            "p.description, " +
            "p.imageUrl," +
            "p.sku," +
            "p.price, " +
            "b.name, " +
            "c.name, " +
            "p.stockQuantity," +
            "p.enabled) " +
            "FROM Product p " +
            "JOIN p.brand b " +
            "JOIN p.category c WHERE p.id IN :productIds")
    List<ProductDto> findProductsByIds(@Param("productIds") Set<Long> productIds);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id IN :productIds")
    List<Product> findProductsForUpdate(List<Long> productIds);

    @Query("SELECT new com.productservice.projection.ProductForCartDto(" +
            "p.price," +
            "p.stockQuantity" +
            ")" +
            "FROM Product p WHERE p.id = :id")
    Optional<ProductForCartDto> getProductForCartById(long id);

    @EntityGraph(attributePaths = {"category", "brand"})
    Page<Product> findAll(@Nullable Specification<Product> spec, Pageable pageable);

    @Transactional
    @Modifying
    @Query("DELETE FROM Product p WHERE p.id = :id")
    int deleteProductById(long id);
}
