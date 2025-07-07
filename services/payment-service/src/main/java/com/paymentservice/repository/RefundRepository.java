package com.paymentservice.repository;

import com.paymentservice.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefundRepository extends JpaRepository<Refund, Long> {
    Optional<Refund> findRefundByStripeRefundId(String stripeRefundId);

    @Query("SELECT r FROM Refund r JOIN FETCH r.payment JOIN FETCH r.payment.refunds WHERE r.stripeRefundId = :stripeRefundId")
    Optional<Refund> findRefundWithPaymentByStripeRefundId(String stripeRefundId);
}
