package com.paymentservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payments_seq")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private long userId;

    @Column(name = "order_id", nullable = false)
    private long orderId;

    @Column(name = "payment_id", unique = true)
    private String paymentId;

    @Column(name = "session_id", nullable = false, unique = true)
    private String sessionId;

    private String currency;

    @Column(name = "total_in_smallest_unit", nullable = false)
    private Long totalInSmallestUnit;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
