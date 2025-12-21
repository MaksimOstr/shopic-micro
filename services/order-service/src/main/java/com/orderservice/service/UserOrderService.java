package com.orderservice.service;

import com.orderservice.dto.UserOrderDto;
import com.orderservice.dto.UserOrderPreviewDto;
import com.orderservice.dto.CreateOrderRequest;
import com.orderservice.dto.OrderParams;
import com.orderservice.entity.*;
import com.orderservice.exception.NotFoundException;
import com.orderservice.mapper.GrpcMapper;
import com.orderservice.mapper.OrderItemMapper;
import com.orderservice.mapper.OrderMapper;
import com.orderservice.service.calculator.DeliveryPriceCalculator;
import com.orderservice.service.grpc.CartGrpcService;
import com.orderservice.service.grpc.PaymentGrpcService;
import com.orderservice.service.grpc.ProductGrpcService;
import com.shopic.grpc.cartservice.CartItem;
import com.shopic.grpc.cartservice.CartResponse;
import com.shopic.grpc.productservice.Product;
import com.shopic.grpc.productservice.ProductInfo;
import com.shopic.grpc.productservice.ProductInfoList;
import com.shopic.grpc.productservice.ProductListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.orderservice.utils.SpecificationUtils.*;
import static com.orderservice.utils.SpecificationUtils.equalsEnum;
import static com.orderservice.utils.SpecificationUtils.lte;

@Service
@RequiredArgsConstructor
public class UserOrderService {
    private final OrderService orderService;
    private final CartGrpcService cartGrpcService;
    private final ProductGrpcService productGrpcService;
    private final PaymentGrpcService paymentGrpcService;
    private final GrpcMapper grpcMapper;
    private final OrderItemMapper orderItemMapper;
    private final OrderMapper orderMapper;
    private final List<DeliveryPriceCalculator> deliveryPriceCalculatorList;


    @Transactional
    public String createOrder(UUID userId, CreateOrderRequest dto) {
        CartResponse cartInfo = cartGrpcService.getCart(userId);
        List<CartItem> cartItems = cartInfo.getCartItemsList();
        ProductListResponse response = productGrpcService.getProductList(
                cartItems.stream().map(CartItem::getProductId).toList()
        );
        List<Product> productList = response.getProductsList();
        Map<String, Integer> productQuantityMap = grpcMapper.getProductQuantityMap(cartItems);
        Order order = createAndSaveOrderWithOrderItems(userId, dto, productList, productQuantityMap);

        productGrpcService.reserveProduct(cartItems, order.getId());
        return paymentGrpcService.createPayment(order.getId(), userId, productList, productQuantityMap, order.getDeliveryPrice()).getCheckoutUrl();
    }

    public Page<UserOrderPreviewDto> getOrdersByUserId(long userId, Pageable pageable, OrderParams params) {
        Specification<Order> spec = equalsEnum("status", params.status())
                .and(hasId("userId", userId))
                .and(gte("totalPrice", params.fromPrice()))
                .and(equalsEnum("status", params.status()))
                .and(lte("totalPrice", params.toPrice()));
        Page<Order> orderPage = orderService.getOrdersBySpec(spec, pageable);
        List<Order> orderList = orderPage.getContent();
        List<UserOrderPreviewDto> orderSummaryList = orderMapper.toOrderSummaryDto(orderList);

        return new PageImpl<>(orderSummaryList, pageable, orderPage.getTotalElements());
    }

    public UserOrderDto getUserOrderDtoById(long orderId) {
        Order order = orderService.getOrderWithItems(orderId);

        return orderMapper.toOrderDto(order);
    }

    private Order createAndSaveOrderWithOrderItems(UUID userId, CreateOrderRequest dto, List<Product> productInfoList, Map<Long, Integer> productQuantityMap) {

        Order order = Order.builder()
                .deliveryType(dto.deliveryType())
                .comment(dto.comment())
                .status(OrderStatusEnum.PENDING)
                .userId(userId)
                .address(dto.address())
                .build();
        List<OrderItem> orderItems = orderItemMapper.toOrderItemList(order, productInfoList, productQuantityMap);

        order.setOrderItems(orderItems);

        DeliveryPriceCalculator deliveryPriceCalculator = getDeliveryPriceCalculator(dto.deliveryType());
        order.setDeliveryPrice(deliveryPriceCalculator.calculateDeliveryPrice(dto, order.calculateTotalPrice()));

        return orderService.save(order);
    }

    private DeliveryPriceCalculator getDeliveryPriceCalculator(OrderDeliveryTypeEnum type) {
        return deliveryPriceCalculatorList.stream().filter(calculator -> calculator.getDeliveryType() == type).findFirst()
                .orElseThrow(() -> new NotFoundException("Provided delivery type is not supported"));
    }
}
