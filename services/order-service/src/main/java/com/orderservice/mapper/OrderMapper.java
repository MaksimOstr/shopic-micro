package com.orderservice.mapper;

import com.orderservice.dto.*;
import com.orderservice.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


import java.util.List;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public interface OrderMapper {
    @Mapping(source = "id", target = "orderId")
    OrderSummaryDto toOrderSummaryDto(Order order);

    List<OrderSummaryDto> toOrderSummaryDto(List<Order> orders);

    @Mapping(target = "country", source = "address.country")
    @Mapping(target = "street", source = "address.street")
    @Mapping(target = "city", source = "address.city")
    @Mapping(target = "postalCode", source = "address.postalCode")
    @Mapping(target = "houseNumber", source = "address.houseNumber")
    @Mapping(source = "id", target = "orderId")
    AdminOrderDto toAdminOrderDto(Order order);

    @Mapping(target = "country", source = "address.country")
    @Mapping(target = "street", source = "address.street")
    @Mapping(target = "city", source = "address.city")
    @Mapping(target = "postalCode", source = "address.postalCode")
    @Mapping(target = "houseNumber", source = "address.houseNumber")
    @Mapping(source = "id", target = "orderId")
    OrderDto toOrderDto(Order order);

    @Mapping(source = "id", target = "orderId")
    @Mapping(source = "customer.firstName", target = "firstName")
    @Mapping(source = "customer.lastName", target = "lastName")
    AdminOrderSummaryDto toAdminOrderSummaryDto(Order order);

    List<AdminOrderSummaryDto> toAdminOrderSummaryDto(List<Order> order);
}
