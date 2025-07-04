package com.paymentservice.service;

import com.paymentservice.dto.CreateRefundDto;
import com.paymentservice.dto.request.RefundRequest;
import com.paymentservice.entity.Payment;
import com.paymentservice.entity.Refund;
import com.paymentservice.entity.RefundStatus;
import com.paymentservice.exception.NotFoundException;
import com.paymentservice.repository.RefundRepository;
import com.stripe.param.RefundCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefundService {
    private final RefundRepository refundRepository;

    public void createRefund(CreateRefundDto dto) {
        Refund refund = Refund.builder()
                .currency(dto.currency())
                .amount(dto.amount())
                .reason(dto.reason())
                .status(RefundStatus.PENDING)
                .stripeRefundId(dto.stripeRefundId())
                .payment(dto.payment())
                .build();

        refundRepository.save(refund);
    }

    public Refund getRefundByStripeRefundId(String stripeRefundId) {
        return refundRepository.findRefundByStripeRefundId(stripeRefundId)
                .orElseThrow(() -> new NotFoundException("Refund not found"));
    }
}
