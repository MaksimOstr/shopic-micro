package com.orderservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orders_seq")
    private Long id;

    @Enumerated(EnumType.STRING)
    private OrderStatusEnum status;

    @Column(name = "user_id", nullable = false)
    private long userId;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @OneToMany(mappedBy = "order", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<OrderItem> orderItems;

    @Column(name = "created_at", nullable = false)
    @CreatedDate
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private Instant updatedAt;

    public BigDecimal calculateTotalPrice() {
        return orderItems.stream()
                .map(OrderItem::calculateTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
