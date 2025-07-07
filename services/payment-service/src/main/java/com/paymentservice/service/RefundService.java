package com.paymentservice.service;

import com.paymentservice.dto.CreateRefundDto;
import com.paymentservice.entity.Refund;
import com.paymentservice.entity.RefundStatus;
import com.paymentservice.exception.NotFoundException;
import com.paymentservice.repository.RefundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefundService {
    private final RefundRepository refundRepository;

    private static final String REFUND_NOT_FOUND = "Refund Not Found";


    public void createRefund(CreateRefundDto dto) {
        Refund refund = Refund.builder()
                .currency(dto.currency())
                .amount(dto.amount())
                .reason(dto.reason())
                .status(RefundStatus.PENDING)
                .description(dto.description())
                .stripeRefundId(dto.stripeRefundId())
                .payment(dto.payment())
                .build();

        refundRepository.save(refund);
    }

    public Refund getRefundByStripeRefundId(String stripeRefundId) {
        return refundRepository.findRefundByStripeRefundId(stripeRefundId)
                .orElseThrow(() -> new NotFoundException(REFUND_NOT_FOUND));
    }

    public Refund getRefundWithPaymentByStripeRefundId(String stripeRefundId) {
        return refundRepository.findRefundWithPaymentByStripeRefundId(stripeRefundId)
                .orElseThrow(() -> new NotFoundException(REFUND_NOT_FOUND));
    }
}
