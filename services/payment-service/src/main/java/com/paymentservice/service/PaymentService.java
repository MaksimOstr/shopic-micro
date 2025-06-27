package com.paymentservice.service;

import com.paymentservice.dto.CreatePaymentDto;
import com.paymentservice.entity.Payment;
import com.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;

    public void createPayment(CreatePaymentDto dto) {
        Payment payment = Payment.builder()
                .userId(dto.userId())
                .paymentId(dto.paymentId())
                .orderId(dto.orderId())
                .build();

        paymentRepository.save(payment);
    }
}
