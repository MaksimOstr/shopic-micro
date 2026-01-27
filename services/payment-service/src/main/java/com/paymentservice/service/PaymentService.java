package com.paymentservice.service;

import ch.qos.logback.core.spi.ContextAware;
import com.paymentservice.dto.PaymentDto;
import com.paymentservice.dto.PaymentSummaryDto;
import com.paymentservice.dto.PaymentParams;
import com.paymentservice.entity.Payment;
import com.paymentservice.entity.PaymentStatus;
import com.paymentservice.exception.NotFoundException;
import com.paymentservice.mapper.PaymentMapper;
import com.paymentservice.repository.PaymentRepository;
import com.paymentservice.utils.SpecificationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.paymentservice.utils.SpecificationUtils.*;
import static com.paymentservice.utils.SpecificationUtils.iLike;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final KafkaService kafkaService;
    private final PaymentMapper paymentMapper;

    public void createPayment(UUID userId, String sessionId, UUID orderId, BigDecimal total) {
        Payment payment = Payment.builder()
                .userId(userId)
                .sessionId(sessionId)
                .orderId(orderId)
                .total(total)
                .status(PaymentStatus.PENDING)
                .build();

        paymentRepository.save(payment);
    }

    public PaymentDto getPaymentDtoById(UUID id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Payment not found"));

        return paymentMapper.toDto(payment);
    }

    public Payment succeedPayment(String sessionId) {
        Payment payment = getBySessionId(sessionId);
        payment.setStatus(PaymentStatus.SUCCEEDED);

        return paymentRepository.save(payment);
    }

    public Payment failPayment(String sessionId) {
        Payment payment = getBySessionId(sessionId);

        payment.setStatus(PaymentStatus.FAILED);

        return paymentRepository.save(payment);
    }

    public Page<PaymentSummaryDto> getPayments(PaymentParams params, Pageable pageable) {
        Specification<Payment> spec = SpecificationUtils.<Payment>equalsField("userId", params.userId())
                .and(iLike("paymentMethod", params.paymentMethod()))
                .and(equalsField("status", params.status()))
                .and(gte("amount", params.amountFrom()))
                .and(lte("amount", params.amountTo()))
                .and(iLike("currency", params.currency()));

        Page<Payment> payments = paymentRepository.findAll(spec, pageable);
        List<Payment> paymentList = payments.getContent();
        List<PaymentSummaryDto> paymentSummaryDtoList = paymentMapper.toSummaryDtoList(paymentList);

        return new PageImpl<>(paymentSummaryDtoList, pageable, payments.getTotalElements());
    }

    private Payment getBySessionId(String sessionId) {
        return paymentRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new NotFoundException("Payment Not Found"));
    }

    @Scheduled(fixedDelay = 1000 * 60)
    @Transactional
    public void checkUnpaidPayments() {
        log.info("Checking unpaid payments");
        Instant thirtyMinutesAgo = Instant.now().minus(Duration.ofMinutes(1));
        paymentRepository.findByStatusAndCreatedAtBefore(PaymentStatus.PENDING, thirtyMinutesAgo)
                .forEach(payment -> {
                    payment.setStatus(PaymentStatus.FAILED);
                    kafkaService.sendUnpaidPaymentEvent(payment.getOrderId());
                });
    }
}
