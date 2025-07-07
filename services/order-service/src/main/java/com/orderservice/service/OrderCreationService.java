package com.orderservice.service;

import com.orderservice.dto.request.CreateOrderRequest;
import com.orderservice.entity.*;
import com.orderservice.mapper.OrderItemClassMapper;
import com.orderservice.repository.OrderRepository;
import com.orderservice.service.grpc.CartGrpcService;
import com.orderservice.service.grpc.PaymentGrpcService;
import com.orderservice.service.grpc.ProductGrpcService;
import com.shopic.grpc.cartservice.CartResponse;
import com.shopic.grpc.cartservice.CartItem;
import com.shopic.grpc.productservice.ActualProductInfoResponse;
import com.shopic.grpc.productservice.ProductInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class OrderCreationService {
    private final OrderRepository orderRepository;
    private final CartGrpcService cartGrpcService;
    private final ProductGrpcService productGrpcService;
    private final OrderItemClassMapper orderItemClassMapper;
    private final PaymentGrpcService paymentGrpcService;


    @Transactional
    public String createOrder(long userId, CreateOrderRequest dto) {
        CartResponse cartInfo = cartGrpcService.getCartInfo(userId);
        List<CartItem> cartItems = cartInfo.getCartItemsList();
        ActualProductInfoResponse response = productGrpcService.getActualProductInfo(
                cartItems.stream().map(CartItem::getProductId).toList()
        );
        List<ProductInfo> productInfoList = response.getProductsList();
        Map<Long, Integer> productQuantityMap = getProductQuantityMap(cartItems);
        Order order = createAndSaveOrderWithOrderItems(userId, dto, productInfoList, productQuantityMap);

        productGrpcService.reserveProduct(cartItems, order.getId());

        return paymentGrpcService.createPayment(order.getId(), userId, productInfoList, productQuantityMap).getCheckoutUrl();
    }

    private Order createAndSaveOrderWithOrderItems(long userId, CreateOrderRequest dto, List<ProductInfo> productInfoList, Map<Long, Integer> productQuantityMap) {
        OrderCustomer customer = new OrderCustomer(
                dto.firstName(),
                dto.lastName(),
                dto.phoneNumber()
        );
        Address address = new Address(
                dto.country(),
                dto.street(),
                dto.city(),
                dto.postalCode(),
                dto.houseNumber()
        );
        Order order = Order.builder()
                .customer(customer)
                .comment(dto.comment())
                .status(OrderStatusEnum.CREATED)
                .userId(userId)
                .address(address)
                .build();
        List<OrderItem> orderItems = orderItemClassMapper.mapToOrderItems(order, productInfoList, productQuantityMap);

        order.setOrderItems(orderItems);
        order.setTotalPrice(order.calculateTotalPrice());

        return orderRepository.save(order);
    }

    private Map<Long, Integer> getProductQuantityMap(List<CartItem> cartItems) {
        return cartItems.stream()
                .collect(Collectors.toMap(
                        CartItem::getProductId,
                        CartItem::getQuantity
                ));
    }
}
