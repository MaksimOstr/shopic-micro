package com.cartservice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.UUID;


@Entity
@Table(name = "cart_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {
    @Id
    @GeneratedValue(strategy =  GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cart_id", nullable = false)
    @JsonBackReference
    private Cart cart;

    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "product_image_url", nullable = false)
    private String productImageUrl;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(nullable = false)
    @Min(1)
    private int quantity;

    @Column(name = "price_at_add", nullable = false)
    @DecimalMin(value = "0.0", message = "Price cannot be negative")
    private BigDecimal priceAtAdd;

}
