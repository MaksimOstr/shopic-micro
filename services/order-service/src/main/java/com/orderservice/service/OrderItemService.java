package com.orderservice.service;


import com.orderservice.dto.request.CreateOrderItem;
import com.orderservice.entity.OrderItem;
import com.orderservice.mapper.OrderItemMapper;
import com.orderservice.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;


    public void saveAllOrderItems(List<CreateOrderItem> orderItems) {
        List<OrderItem> orderItemList = orderItems.stream().map(orderItemMapper::toOrderItem).toList();
        orderItemRepository.saveAll(orderItemList);
    }



}
