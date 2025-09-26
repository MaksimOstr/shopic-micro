package com.productservice.repository;

import com.productservice.dto.AdminProductDto;
import com.productservice.dto.LikedProductDto;
import com.productservice.dto.UserProductDto;
import com.productservice.entity.Product;
import com.productservice.dto.ProductBasicInfoDto;
import com.productservice.entity.ProductStatusEnum;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    @Query("SELECT p.imageUrl FROM Product p WHERE p.id = :id")
    Optional<String> getProductImageUrl(long id);

    @Query("SELECT new com.productservice.dto.UserProductDto(" +
            "p.id, " +
            "p.name, " +
            "p.description, " +
            "p.imageUrl," +
            "p.sku," +
            "p.price, " +
            "b.name, " +
            "c.name," +
            "p.stockQuantity) " +
            "FROM Product p " +
            "JOIN p.brand b " +
            "JOIN p.category c WHERE p.id = :id AND p.status = com.productservice.entity.ProductStatusEnum.ACTIVE")
    Optional<UserProductDto> getActiveUserProductById(long id);

    int countByBrand_IdAndStatus(int brandId, ProductStatusEnum status);

    int countByCategory_IdAndStatus(int categoryId, ProductStatusEnum status);

    @Modifying
    @Query("UPDATE Product p SET p.status = com.productservice.entity.ProductStatusEnum.ARCHIVED WHERE p.brand.id = :brandId AND p.status = com.productservice.entity.ProductStatusEnum.ACTIVE")
    int deactivateAllActiveProductsByBrandId(int brandId);

    @Modifying
    @Query("UPDATE Product p SET p.status = com.productservice.entity.ProductStatusEnum.ARCHIVED WHERE p.category.id = :categoryId AND p.status = com.productservice.entity.ProductStatusEnum.ACTIVE")
    int deactivateAllActiveProductsByCategoryId(int categoryId);

    @Query("SELECT new com.productservice.dto.AdminProductDto(" +
            "p.id, " +
            "p.name, " +
            "p.description, " +
            "p.imageUrl," +
            "p.sku," +
            "p.price, " +
            "b.name, " +
            "c.name," +
            "p.status," +
            "p.stockQuantity," +
            "p.createdAt) " +
            "FROM Product p " +
            "JOIN p.brand b " +
            "JOIN p.category c WHERE p.id = :id")
    Optional<AdminProductDto> getAdminProduct(long id);

    @Query("SELECT new com.productservice.dto.AdminProductDto(" +
            "p.id, " +
            "p.name, " +
            "p.description, " +
            "p.imageUrl," +
            "p.sku," +
            "p.price, " +
            "b.name, " +
            "c.name," +
            "p.status," +
            "p.stockQuantity," +
            "p.createdAt) " +
            "FROM Product p " +
            "JOIN p.brand b " +
            "JOIN p.category c WHERE p.sku = :sku")
    Optional<AdminProductDto> getAdminProduct(UUID sku);

    @Query("SELECT p FROM Product p JOIN FETCH p.category c JOIN FETCH p.brand b WHERE p.id = :id")
    Optional<Product> getProductWithCategoryAndBrand(long id);

    @Query("SELECT new com.productservice.dto.ProductBasicInfoDto(" +
            "p.id," +
            "p.price," +
            "p.imageUrl," +
            "p.name," +
            "p.stockQuantity" +
            ")" +
            "FROM Product p WHERE p.id IN :ids AND p.status = com.productservice.entity.ProductStatusEnum.ACTIVE")
    List<ProductBasicInfoDto> findActiveProductsBasicInfoByIds(List<Long> ids);

    @Query("SELECT new com.productservice.dto.ProductBasicInfoDto(" +
            "p.id," +
            "p.price," +
            "p.imageUrl," +
            "p.name," +
            "p.stockQuantity" +
            ")" +
            "FROM Product p WHERE p.id = :id AND p.status = com.productservice.entity.ProductStatusEnum.ACTIVE")
    Optional<ProductBasicInfoDto> findActiveProductBasicInfoById(Long id);


    @Query("SELECT new com.productservice.dto.LikedProductDto(" +
            "p.id, " +
            "p.name, " +
            "p.imageUrl," +
            "p.price) " +
            "FROM Product p " +
            "WHERE p.id IN :productIds")
    List<LikedProductDto> findProductsByIds(@Param("productIds") Set<Long> productIds);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id IN :productIds")
    List<Product> findProductsForUpdate(List<Long> productIds);

    @EntityGraph(attributePaths = {"category", "brand"})
    Page<Product> findAll(@Nullable Specification<Product> spec, Pageable pageable);
}
