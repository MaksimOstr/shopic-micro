package com.paymentservice.service;

import com.paymentservice.dto.CreateRefundDto;
import com.paymentservice.dto.request.RefundRequest;
import com.paymentservice.entity.Payment;
import com.paymentservice.entity.PaymentStatus;
import com.paymentservice.entity.RefundReason;
import com.paymentservice.exception.InternalException;
import com.paymentservice.exception.RefundRequestException;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Refund;
import com.stripe.param.RefundCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static com.paymentservice.utils.Utils.toSmallestUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class StripeRefundService {
    @Value("${STRIPE_SECRET_KEY}")
    private String stripeSecretKey;

    private final PaymentService paymentService;
    private final RefundService refundService;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }


    public void processRefund(RefundRequest dto) {
        try {
            Payment payment = paymentService.getPaymentById(dto.paymentId());

            validateRefundRequest(payment, dto.amount());

            RefundCreateParams params = RefundCreateParams.builder()
                    .setPaymentIntent(payment.getStripePaymentId())
                    .setCurrency("usd")
                    .setAmount(toSmallestUnit(dto.amount()).longValue())
                    .setReason(RefundCreateParams.Reason.valueOf(dto.reason().name()))
                    .build();

            Refund refund = Refund.create(params);

            saveRefund(payment, refund.getCurrency(), dto.amount(), dto.reason(), refund.getId());
        } catch (StripeException e) {
            log.error("Stripe error", e);
            throw new InternalException("Internal refund error");
        }
    }


    private void validateRefundRequest(Payment payment, BigDecimal amount) {
        if(payment.getStatus() != PaymentStatus.SUCCEEDED) {
            throw new RefundRequestException("Cannot refund non succeeded payment");
        }

        if(amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RefundRequestException("Cannot refund negative amount");
        }

        BigDecimal availableToRefund = payment.getAmount().subtract(payment.getTotalRefundedAmount());

        if(amount.compareTo(availableToRefund) > 0) {
            throw new RefundRequestException("Cannot refund amount greater than available to refund");
        }
    }

    private void saveRefund(Payment payment, String currency, BigDecimal amount, RefundReason reason, String stripeRefundId) {
        CreateRefundDto dto = new CreateRefundDto(
                payment,
                currency,
                amount,
                reason,
                stripeRefundId
        );

        refundService.createRefund(dto);
    }
}
