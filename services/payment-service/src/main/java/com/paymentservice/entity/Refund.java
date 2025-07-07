package com.paymentservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "refunds")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Refund {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "refunds_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Enumerated(EnumType.STRING)
    private RefundStatus status;

    private String currency;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private RefundReason reason;

    @Column(name = "stripe_refund_id", nullable = false, unique = true)
    private String stripeRefundId;

    @Column(name = "failureReason")
    private String failureReason;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "refunded_at")
    private Instant refundedAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
