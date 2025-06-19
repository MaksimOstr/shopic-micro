package com.orderservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_items_seq")
    private long id;

    @Column(name = "product_id", nullable = false)
    private long productId;

    @Min(value = 0)
    private int quantity;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "product_image_url", nullable = false)
    private String productImageUrl;

    @Column(name = "price_at_purchase", nullable = false)
    @DecimalMin(value = "0.0", message = "Price cannot be negative")
    private BigDecimal priceAtPurchase;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id")
    private Order order;

    public BigDecimal calculateTotalPrice() {
        return priceAtPurchase.multiply(BigDecimal.valueOf(quantity));
    }
}
