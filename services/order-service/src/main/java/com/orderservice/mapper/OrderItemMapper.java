package com.orderservice.mapper;

import com.orderservice.dto.OrderItemDto;
import com.orderservice.entity.OrderItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    OrderItemDto toOrderItemDto(OrderItem orderItem);
}
