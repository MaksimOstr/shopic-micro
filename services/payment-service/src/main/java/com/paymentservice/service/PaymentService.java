package com.paymentservice.service;

import com.paymentservice.dto.CreatePaymentDto;
import com.paymentservice.entity.Payment;
import com.paymentservice.entity.PaymentStatus;
import com.paymentservice.exception.NotFoundException;
import com.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;

    private final static String PAYMENT_NOT_FOUND = "Payment Not Found";

    public void createPayment(CreatePaymentDto dto) {
        Payment payment = Payment.builder()
                .userId(dto.userId())
                .paymentId(dto.paymentId())
                .orderId(dto.orderId())
                .status(PaymentStatus.PENDING)
                .build();

        paymentRepository.save(payment);
    }

    public long getOrderIdByPaymentId(String paymentId) {
        return paymentRepository.getOrderIdByPaymentId(paymentId)
                .orElseThrow(() -> new NotFoundException(PAYMENT_NOT_FOUND));

    }

    public void changePaymentStatus(String paymentId, PaymentStatus status) {
        int updated = paymentRepository.updatePaymentStatus(paymentId, status);

        if(updated == 0) {
            throw new NotFoundException(PAYMENT_NOT_FOUND);
        }
    }
}
