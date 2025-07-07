package com.paymentservice.mapper;


import com.paymentservice.dto.RefundDto;
import com.paymentservice.dto.RefundSummaryDto;
import com.paymentservice.entity.Refund;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RefundMapper {

    @Mapping(target = "paymentId", source = "payment.id")
    RefundDto toRefundDto(Refund refund);

    @Mapping(target = "paymentId", source = "payment.id")
    RefundSummaryDto toRefundSummaryDto(Refund refund);

    List<RefundSummaryDto> toRefundSummaryDtoList(List<Refund> refunds);
}
