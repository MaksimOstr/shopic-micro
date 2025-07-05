package com.paymentservice.service;

import com.paymentservice.dto.CreatePaymentDto;
import com.paymentservice.entity.Payment;
import com.paymentservice.entity.PaymentStatus;
import com.paymentservice.exception.NotFoundException;
import com.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final KafkaService kafkaService;

    private final static String PAYMENT_NOT_FOUND = "Payment Not Found";

    public void createPayment(CreatePaymentDto dto) {
        Payment payment = Payment.builder()
                .userId(dto.userId())
                .sessionId(dto.sessionId())
                .orderId(dto.orderId())
                .amount(dto.amount())
                .currency(dto.currency())
                .totalInSmallestUnit(dto.totalInSmallestUnit())
                .status(PaymentStatus.PENDING)
                .build();

        paymentRepository.save(payment);
    }

    public void save(Payment payment) {
        paymentRepository.save(payment);
    }

    public Payment getPaymentBySessionId(String sessionId) {
        return paymentRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new NotFoundException(PAYMENT_NOT_FOUND));

    }

    public void changePaymentStatus(String paymentId, PaymentStatus status) {
        int updated = paymentRepository.updatePaymentStatus(paymentId, status);

        if(updated == 0) {
            throw new NotFoundException(PAYMENT_NOT_FOUND);
        }
    }

    public Payment getPaymentByOrderId(long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NotFoundException(PAYMENT_NOT_FOUND));
    }

    @Scheduled(fixedDelay = 1000 * 60)
    @Transactional
    public void checkUnpaidPayments() {
        Instant thirtyMinutesAgo = Instant.now().minus(Duration.ofMinutes(30));
        paymentRepository.findByStatusAndCreatedAtBefore(PaymentStatus.PENDING, thirtyMinutesAgo)
                .forEach(payment -> {
                    payment.setStatus(PaymentStatus.FAILED);
                    kafkaService.sendUnpaidPaymentEvent(payment.getOrderId());
                });
    }
}
