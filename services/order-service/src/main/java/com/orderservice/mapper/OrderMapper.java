package com.orderservice.mapper;

import com.orderservice.dto.*;
import com.orderservice.entity.Order;
import com.shopic.grpc.paymentservice.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


import java.util.List;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public interface OrderMapper {
    @Mapping(source = "id", target = "orderId")
    UserOrderPreviewDto toOrderSummaryDto(Order order);

    List<UserOrderPreviewDto> toOrderSummaryDto(List<Order> orders);

    @Mapping(source = "id", target = "orderId")
    AdminOrderDto toAdminOrderDto(Order order);

    @Mapping(source = "id", target = "orderId")
    UserOrderDto toOrderDto(Order order);

    @Mapping(source = "id", target = "orderId")
    @Mapping(source = "customer.firstName", target = "firstName")
    @Mapping(source = "customer.lastName", target = "lastName")
    AdminOrderPreviewDto toAdminOrderSummaryDto(Order order);

    List<AdminOrderPreviewDto> toAdminOrderSummaryDto(List<Order> order);
}
