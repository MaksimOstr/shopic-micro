package com.productservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;


@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "products_seq")
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    private String description;

    private UUID sku;

    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Brand brand;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "category_id")
    private Category category;

    @Column(name = "stock_quantity", nullable = false)
    private int stockQuantity;


    public Product(String name, String description, UUID sku, BigDecimal price, String imageUrl, Category category, int stockQuantity, Brand brand) {
        this.name = name;
        this.description = description;
        this.sku = sku;
        this.price = price;
        this.imageUrl = imageUrl;
        this.category = category;
        this.stockQuantity = stockQuantity;
        this.brand = brand;
    }
}
