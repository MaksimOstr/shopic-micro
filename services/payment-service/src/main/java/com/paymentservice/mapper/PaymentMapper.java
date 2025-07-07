package com.paymentservice.mapper;

import com.paymentservice.dto.PaymentDto;
import com.paymentservice.dto.PaymentSummaryDto;
import com.paymentservice.entity.Payment;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {RefundMapper.class})
public interface PaymentMapper {

    PaymentDto toPaymentDto(Payment payment);

    PaymentSummaryDto toPaymentSummaryDto(Payment payment);

    List<PaymentSummaryDto> toPaymentSummaryDtoList(List<Payment> payments);
}
