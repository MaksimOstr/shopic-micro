package com.orderservice.service;

import com.orderservice.dto.CreateOrderRequest;
import com.orderservice.dto.UpdateContactInfoRequest;
import com.orderservice.dto.UpdateOrderStatusRequest;
import com.orderservice.entity.Order;
import com.orderservice.entity.OrderDeliveryTypeEnum;
import com.orderservice.entity.OrderItem;
import com.orderservice.entity.OrderStatusEnum;
import com.orderservice.exception.NotFoundException;
import com.orderservice.repository.OrderRepository;
import com.orderservice.service.calculator.DeliveryPriceCalculator;
import com.shopic.grpc.productservice.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final List<DeliveryPriceCalculator> deliveryPriceCalculatorList;

    private static final String ORDER_NOT_FOUND = "Order not found";

    @Transactional
    public Order createOrder(CreateOrderRequest dto, UUID userId) {
        Order order = Order.builder()
                .customerName(dto.name())
                .customerPhoneNumber(dto.phoneNumber())
                .deliveryType(dto.deliveryType())
                .comment(dto.comment())
                .status(OrderStatusEnum.PENDING)
                .userId(userId)
                .address(dto.address())
                .build();

        DeliveryPriceCalculator deliveryPriceCalculator = getDeliveryPriceCalculator(dto.deliveryType());
        order.setDeliveryPrice(deliveryPriceCalculator.calculateDeliveryPrice(dto, order.calculateTotalPrice()));

        return orderRepository.save(order);
    }

    @Transactional
    public Order updateOrderContactInfo(UUID orderId, UpdateContactInfoRequest dto) {
        Order order = getOrderById(orderId);

        Optional.ofNullable(dto.customerName()).ifPresent(order::setCustomerName);
        Optional.ofNullable(dto.address()).ifPresent(order::setAddress);

        return order;
    }

    public Order updateOrderStatus(UUID orderId, OrderStatusEnum targetStatus) {
        Order order = getOrderById(orderId);

        order.changeOrderStatus(targetStatus);

        return orderRepository.save(order);
    }

    public Order getOrderWithItems(UUID orderId) {
        return orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new NotFoundException(ORDER_NOT_FOUND));
    }

    public Order getOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(ORDER_NOT_FOUND));
    }

    public Page<Order> getOrdersBySpec(Specification<Order> spec, Pageable pageable) {
        return orderRepository.findAll(spec, pageable);
    }

    private DeliveryPriceCalculator getDeliveryPriceCalculator(OrderDeliveryTypeEnum type) {
        return deliveryPriceCalculatorList.stream().filter(calculator -> calculator.getDeliveryType() == type).findFirst()
                .orElseThrow(() -> new NotFoundException("Provided delivery type is not supported"));
    }
}
