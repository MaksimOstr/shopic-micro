package com.orderservice.entity;

import com.orderservice.exception.ApiException;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.orderservice.entity.OrderStatusEnum.CANCELLED;
import static com.orderservice.entity.OrderStatusEnum.COMPLETED;
import static com.orderservice.entity.OrderStatusEnum.PROCESSING;
import static com.orderservice.entity.OrderStatusEnum.SHIPPED;

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
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private OrderStatusEnum status;

    @Column(name = "customer_name", nullable = false, length = 50)
    private String customerName;

    @Column(name = "customer_phone_number", nullable = false)
    private String customerPhoneNumber;

    private String address;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "delivery_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderDeliveryTypeEnum deliveryType;

    @Column(name = "delivery_price", nullable = false)
    @Builder.Default
    private BigDecimal deliveryPrice = BigDecimal.ZERO;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    private String comment;

    @OneToMany(mappedBy = "order", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    @CreatedDate
    private Instant createdAt;


    @Column(name = "updated_at", nullable = false)
    @LastModifiedDate
    private Instant updatedAt;

    @PrePersist
    @PreUpdate
    public void prePersistAndPreUpdate() {
        totalPrice = calculateTotalPrice();
    }

    public BigDecimal calculateTotalPrice() {
        return orderItems.stream()
                .map(OrderItem::calculateTotalPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void addNewOrderItems(List<OrderItem> orderItems) {
        orderItems.forEach(orderItem -> orderItem.setOrder(this));
        this.orderItems.addAll(orderItems);
    }

    public void changeOrderStatus(OrderStatusEnum target) {
        if(!canTransitionTo(target)) {
            throw new ApiException("Cannot transition from" + this.status + " to " + target, HttpStatus.BAD_REQUEST);
        }

        status = target;
    }

    private boolean canTransitionTo(OrderStatusEnum target) {
        return switch (this.status) {
            case PENDING -> target == PROCESSING || target == CANCELLED;
            case PROCESSING -> target == SHIPPED || target == CANCELLED;
            case SHIPPED -> target == COMPLETED;
            case COMPLETED, CANCELLED -> false;
        };
    }
}
