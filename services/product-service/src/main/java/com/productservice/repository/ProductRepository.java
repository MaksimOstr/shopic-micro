package com.productservice.repository;

import com.productservice.entity.Product;
import com.productservice.projection.ProductImageUrlProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySellerIdAndId(long sellerId, long id);

    @Query("SELECT p.imageUrl as imageUrl FROM Product p WHERE p.id = :id AND p.sellerId = :sellerId")
    Optional<ProductImageUrlProjection> getProductImageUrl(long id, long sellerId);

    @Transactional
    @Modifying
    @Query("UPDATE Product p SET p.imageUrl = :imageUrl WHERE p.id = :id")
    int updateProductImageUrl(long id, String imageUrl);


    Page<Product> findBySellerId(long sellerId, Pageable pageable);
}
