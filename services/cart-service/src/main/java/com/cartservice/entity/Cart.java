package com.cartservice.entity;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;


@Entity
@Table(name = "carts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "carts_seq")
    private Long id;

    @Column(nullable = false, unique = true, name = "user_id")
    private Long userId;

    @OneToMany(mappedBy = "cart", cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, orphanRemoval = true)
    @JsonManagedReference
    private List<CartItem> cartItems;

    @CreatedDate
    @Column(nullable = false, name = "created_at")
    private Instant createdAt;

    public BigDecimal calculateTotal() {
        return cartItems.stream().map(cartItem -> cartItem.getPriceAtAdd().multiply(BigDecimal.valueOf(cartItem.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
