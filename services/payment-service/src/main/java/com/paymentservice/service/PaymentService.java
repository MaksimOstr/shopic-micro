package com.paymentservice.service;

import com.paymentservice.dto.CreatePaymentDto;
import com.paymentservice.dto.PaymentDto;
import com.paymentservice.dto.PaymentSummaryDto;
import com.paymentservice.dto.request.PaymentParams;
import com.paymentservice.entity.Payment;
import com.paymentservice.entity.PaymentStatus;
import com.paymentservice.entity.Refund;
import com.paymentservice.exception.NotFoundException;
import com.paymentservice.mapper.PaymentMapper;
import com.paymentservice.repository.PaymentRepository;
import com.paymentservice.utils.SpecificationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static com.paymentservice.utils.SpecificationUtils.*;
import static com.paymentservice.utils.SpecificationUtils.iLike;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final KafkaService kafkaService;
    private final PaymentMapper paymentMapper;

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

    public PaymentDto getPayment(long id) {
        Payment payment = paymentRepository.findByIdWithRefunds(id)
                .orElseThrow(() -> new NotFoundException(PAYMENT_NOT_FOUND));

        return paymentMapper.toPaymentDto(payment);
    }

    public Page<PaymentSummaryDto> getPayments(PaymentParams params, Pageable pageable) {
        Specification<Payment> spec = SpecificationUtils.<Payment>equalsLong("userId", params.userId())
                .and(iLike("paymentMethod", params.paymentMethod()))
                .and(equalsEnum("status", params.status()))
                .and(gte("amount", params.amountFrom()))
                .and(lte("amount", params.amountTo()))
                .and(iLike("currency", params.currency()));

        Page<Payment> payments = paymentRepository.findAll(spec, pageable);
        List<Payment> paymentList = payments.getContent();
        List<PaymentSummaryDto> paymentSummaryDtoList = paymentMapper.toPaymentSummaryDtoList(paymentList);

        return new PageImpl<>(paymentSummaryDtoList, pageable, payments.getTotalElements());
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

    public Payment getPaymentWithRefundsByOrderId(long orderId) {
        return paymentRepository.findByOrderIdWithRefunds(orderId)
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
