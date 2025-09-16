package com.cartservice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.math.BigDecimal;


@Entity
@Table(name = "cart_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cart_items_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cart_id")
    @JsonBackReference
    private Cart cart;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_image_url", nullable = false)
    private String productImageUrl;

    @Column(name = "productName", nullable = false)
    private String productName;

    @Column(nullable = false)
    @Min(1)
    private int quantity;

    @Column(name = "price_at_add", nullable = false)
    @DecimalMin(value = "0.0", message = "Price cannot be negative")
    private BigDecimal priceAtAdd;

}
