package com.orderservice.service;

import com.orderservice.dto.CreateOrderRequest;
import com.orderservice.dto.OrderParams;
import com.orderservice.dto.UserOrderDto;
import com.orderservice.dto.UserOrderPreviewDto;
import com.orderservice.entity.Order;
import com.orderservice.entity.OrderItem;
import com.orderservice.entity.OrderStatusEnum;
import com.orderservice.exception.ApiException;
import com.orderservice.mapper.GrpcMapper;
import com.orderservice.mapper.OrderItemMapper;
import com.orderservice.mapper.OrderMapper;
import com.orderservice.service.grpc.CartGrpcService;
import com.orderservice.service.grpc.PaymentGrpcService;
import com.orderservice.service.grpc.ProductGrpcService;
import com.shopic.grpc.cartservice.CartItem;
import com.shopic.grpc.cartservice.CartResponse;
import com.shopic.grpc.productservice.Product;
import com.shopic.grpc.productservice.ProductListResponse;
import com.shopic.grpc.productservice.ReserveProductsResponse;
import com.shopic.grpc.productservice.ReservedProduct;
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

import static com.orderservice.utils.SpecificationUtils.equalsEnum;
import static com.orderservice.utils.SpecificationUtils.gte;
import static com.orderservice.utils.SpecificationUtils.hasId;
import static com.orderservice.utils.SpecificationUtils.lte;

@Service
@RequiredArgsConstructor
public class UserOrderFacade {
    private final OrderService orderService;
    private final CartGrpcService cartGrpcService;
    private final ProductGrpcService productGrpcService;
    private final PaymentGrpcService paymentGrpcService;
    private final GrpcMapper grpcMapper;
    private final OrderItemMapper orderItemMapper;
    private final OrderMapper orderMapper;
    private final KafkaService kafkaService;


    @Transactional
    public String placeOrder(CreateOrderRequest dto, UUID userId) {
        CartResponse cartInfo = cartGrpcService.getCart(userId);
        List<CartItem> cartItems = cartInfo.getCartItemsList();
        Order order = orderService.createOrder(dto, userId);
        Map<String, CartItem> cartItemMap = grpcMapper.getCartItemMap(cartItems);
        ReserveProductsResponse reservedProducts = productGrpcService.reserveProducts(
                cartItems,
                cartItemMap,
                order.getId()
        );
        List<ReservedProduct> productList = reservedProducts.getProductsList();
        List<OrderItem> orderItems = grpcMapper.toOrderItemList(productList, cartItemMap);
        order.addNewOrderItems(orderItems);

        try {
            return paymentGrpcService.createPayment(userId, order).getCheckoutUrl();
        } catch (ApiException e) {
            kafkaService.sendOrderFailedEvent(order.getId());

            throw e;
        }
    }

    public Page<UserOrderPreviewDto> getOrdersByUserId(UUID userId, Pageable pageable, OrderParams params) {
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

    public UserOrderDto getUserOrderDtoById(UUID orderId) {
        Order order = orderService.getOrderWithItems(orderId);

        return orderMapper.toOrderDto(order);
    }
}
