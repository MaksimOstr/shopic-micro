package com.paymentservice.repository;

import com.paymentservice.entity.Payment;
import com.paymentservice.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findBySessionId(String sessionId);

    @Transactional
    @Modifying
    @Query("UPDATE Payment p SET p.status = :paymentStatus WHERE p.sessionId = :sessionId")
    int updatePaymentStatus(String sessionId, PaymentStatus paymentStatus);

    Optional<Payment> findByOrderId(long orderId);

    List<Payment> findByStatusAndCreatedAtBefore(PaymentStatus status, Instant date);
}
