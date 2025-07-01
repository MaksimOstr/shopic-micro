package com.orderservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.orderservice.dto.request.CreateOrderRequest;
import com.orderservice.entity.Order;
import com.orderservice.entity.OrderItem;
import com.orderservice.entity.OrderStatusEnum;
import com.orderservice.mapper.OrderItemMapper;
import com.orderservice.repository.OrderRepository;
import com.orderservice.service.grpc.CartGrpcService;
import com.orderservice.service.grpc.PaymentGrpcService;
import com.orderservice.service.grpc.ProductGrpcService;
import com.shopic.grpc.cartservice.CartResponse;
import com.shopic.grpc.cartservice.CartItem;
import com.shopic.grpc.productservice.ActualProductInfoResponse;
import com.shopic.grpc.productservice.ProductInfo;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class OrderCreationService {
    private final OrderRepository orderRepository;
    private final CartGrpcService cartGrpcService;
    private final ProductGrpcService productGrpcService;
    private final OrderItemMapper orderItemMapper;
    private final PaymentGrpcService paymentGrpcService;


    @Transactional
    public String createOrder(long userId, CreateOrderRequest dto) {
        CartResponse cartInfo = cartGrpcService.getCartInfo(userId);
        List<CartItem> cartItems = cartInfo.getCartItemsList();
        ActualProductInfoResponse response = productGrpcService.getActualProductInfo(
                cartItems.stream().map(CartItem::getProductId).toList()
        );
        Map<Long, BigDecimal> productPriceMap = getProductPriceMap(response.getProductsList());
        Order order = createAndSaveOrderWithOrderItems(userId, productPriceMap, cartItems);

        productGrpcService.reserveProduct(cartItems, order.getId());

        return paymentGrpcService.createPayment(order.getId(), userId, productPriceMap, cartItems).getCheckoutUrl();
    }

    private Order createAndSaveOrderWithOrderItems(long userId, Map<Long, BigDecimal> priceMap, List<CartItem> cartItems) {
        Order order = Order.builder()
                .status(OrderStatusEnum.CREATED)
                .userId(userId)
                .build();
        List<OrderItem> orderItems = orderItemMapper.mapToOrderItems(cartItems, order, priceMap);

        order.setOrderItems(orderItems);
        order.setTotalPrice(order.calculateTotalPrice());

        return orderRepository.save(order);
    }

    private Map<Long, BigDecimal> getProductPriceMap(List<ProductInfo> productInfoList) {
        return productInfoList.stream()
                .collect(Collectors.toMap(
                        ProductInfo::getProductId,
                        info -> new BigDecimal(info.getPrice())
                ));
    }
}
