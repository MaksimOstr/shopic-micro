package com.orderservice.service;

import com.orderservice.dto.UserOrderDto;
import com.orderservice.dto.UserOrderPreviewDto;
import com.orderservice.dto.request.CreateOrderRequest;
import com.orderservice.dto.request.OrderParams;
import com.orderservice.entity.*;
import com.orderservice.mapper.GrpcMapper;
import com.orderservice.mapper.OrderItemMapper;
import com.orderservice.mapper.OrderMapper;
import com.orderservice.service.grpc.CartGrpcService;
import com.orderservice.service.grpc.PaymentGrpcService;
import com.orderservice.service.grpc.ProductGrpcService;
import com.shopic.grpc.cartservice.CartItem;
import com.shopic.grpc.cartservice.CartResponse;
import com.shopic.grpc.productservice.ProductInfo;
import com.shopic.grpc.productservice.ProductInfoList;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

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


    @Transactional
    public String createOrder(long userId, CreateOrderRequest dto) {
        CartResponse cartInfo = cartGrpcService.getCartInfo(userId);
        List<CartItem> cartItems = cartInfo.getCartItemsList();
        ProductInfoList response = productGrpcService.getProductInfoList(
                cartItems.stream().map(CartItem::getProductId).toList()
        );
        List<ProductInfo> productInfoList = response.getProductsList();
        Map<Long, Integer> productQuantityMap = grpcMapper.getProductQuantityMap(cartItems);
        Order order = createAndSaveOrderWithOrderItems(userId, dto, productInfoList, productQuantityMap);

        productGrpcService.reserveProduct(cartItems, order.getId());
        return paymentGrpcService.createPayment(order.getId(), userId, productInfoList, productQuantityMap).getCheckoutUrl();
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
        List<OrderItem> orderItems = orderItemMapper.toOrderItemList(order, productInfoList, productQuantityMap);

        order.setOrderItems(orderItems);

        return orderService.save(order);
    }
}
