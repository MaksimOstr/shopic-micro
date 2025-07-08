package com.paymentservice.service;

import com.paymentservice.dto.CreateRefundDto;
import com.paymentservice.dto.RefundDto;
import com.paymentservice.dto.RefundSummaryDto;
import com.paymentservice.dto.request.RefundParams;
import com.paymentservice.entity.Refund;
import com.paymentservice.entity.RefundStatus;
import com.paymentservice.exception.NotFoundException;
import com.paymentservice.mapper.RefundMapper;
import com.paymentservice.repository.RefundRepository;
import com.paymentservice.utils.SpecificationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.InternalException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.paymentservice.utils.SpecificationUtils.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefundService {
    private final RefundRepository refundRepository;
    private final RefundMapper refundMapper;

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

        try {
            refundRepository.save(refund);
        } catch (Exception e) {
            log.error("Refund service error: {}", e.getMessage());
            throw new InternalException("Database constraint violation");
        }
    }

    public RefundDto getRefund(long id) {
        Refund refund = refundRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(REFUND_NOT_FOUND));

        return refundMapper.toRefundDto(refund);
    }

    public Page<RefundSummaryDto> getRefunds(RefundParams params, Pageable pageable) {
        Specification<Refund> spec = SpecificationUtils.<Refund>hasChild("payment", params.paymentId())
                .and(equalsEnum("status", params.status()))
                .and(equalsEnum("reason", params.reason()))
                .and(gte("amount", params.amountFrom()))
                .and(lte("amount", params.amountTo()))
                .and(iLike("stripeRefundId", params.stripeRefundId()));
        Page<Refund> refunds = refundRepository.findAll(spec, pageable);
        List<Refund> refundList = refunds.getContent();
        List<RefundSummaryDto> refundDtoList = refundMapper.toRefundSummaryDtoList(refundList);

        return new PageImpl<>(refundDtoList, pageable, refunds.getTotalElements());
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
