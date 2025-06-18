package com.orderservice.mapper;

import com.orderservice.dto.request.CreateOrderItem;
import com.orderservice.entity.Order;
import com.shopic.grpc.productservice.ProductInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "quantity", source = "orderQuantity")
    CreateOrderItem toCreateOrderItem(ProductInfo productInfo, int orderQuantity, Order order);
}
