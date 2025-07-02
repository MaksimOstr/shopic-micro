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

    @Query("SELECT p.orderId FROM Payment p WHERE p.paymentId = :paymentId ")
    Optional<Long> getOrderIdByPaymentId(String paymentId);

    @Transactional
    @Modifying
    @Query("UPDATE Payment p SET p.status = :paymentStatus WHERE p.paymentId = :paymentId")
    int updatePaymentStatus(String paymentId, PaymentStatus paymentStatus);

    List<Payment> findByStatusAndCreatedAtBefore(PaymentStatus status, Instant date);
}
