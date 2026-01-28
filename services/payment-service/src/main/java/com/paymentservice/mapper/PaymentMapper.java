package com.paymentservice.mapper;

import com.paymentservice.dto.PaymentDto;
import com.paymentservice.dto.PaymentSummaryDto;
import com.paymentservice.entity.Payment;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    PaymentDto toDto(Payment payment);

    PaymentSummaryDto toSummaryDto(Payment payment);

    List<PaymentSummaryDto> toSummaryDtoList(List<Payment> payments);
}
