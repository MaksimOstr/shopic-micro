package com.paymentservice.entity;

import jakarta.persistence.*;
import lombok.*;
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

    @Column(name = "stripe_refund_id", nullable = false, unique = true)
    private String stripeRefundId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
