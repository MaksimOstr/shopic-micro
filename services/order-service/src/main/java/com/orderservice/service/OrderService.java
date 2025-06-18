package com.orderservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.orderservice.dto.request.CreateOrderItem;
import com.orderservice.entity.Order;
import com.orderservice.entity.OrderStatusEnum;
import com.orderservice.exception.InsufficientStockException;
import com.orderservice.mapper.OrderMapper;
import com.orderservice.repository.OrderRepository;
import com.orderservice.service.grpc.CartGrpcService;
import com.orderservice.service.grpc.ProductGrpcService;
import com.shopic.grpc.cartservice.CartResponse;
import com.shopic.grpc.cartservice.CartItem;
import com.shopic.grpc.productservice.GetProductInfoBatchResponse;
import com.shopic.grpc.productservice.ProductInfo;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.orderservice.utils.OrderUtils.calculateTotalPrice;
import static com.orderservice.utils.OrderUtils.getProductIds;


@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemService orderItemService;
    private final CartGrpcService cartGrpcService;
    private final ProductGrpcService productGrpcService;
    private final KafkaEventProducer kafkaEventProducer;
    private final OrderMapper orderMapper;


    @Transactional
    public void createOrder(long userId) throws JsonProcessingException {
        CartResponse cartInfo = cartGrpcService.getCartInfo(userId);
        List<CartItem> cartItems = cartInfo.getCartItemsList();
        List<Long> productIds = getProductIds(cartItems);

        Map<Long, ProductInfo> productInfoMap = getProductInfoMap(productIds);

        validateStock(productInfoMap, cartItems);

        Order savedOrder = createAndSaveOrderEntity(userId, productInfoMap);
        saveOrderItems(cartItems, savedOrder, productInfoMap);

        kafkaEventProducer.sendOrderCreatedEvent();
    }


    private Order createAndSaveOrderEntity(long userId, Map<Long, ProductInfo> productInfoMap) {
        BigDecimal totalPrice = calculateTotalPrice(productInfoMap.values());
        Order order = Order.builder()
                .status(OrderStatusEnum.CREATED)
                .totalPrice(totalPrice)
                .userId(userId)
                .build();

        return orderRepository.save(order);
    }

    private Map<Long, ProductInfo> getProductInfoMap(List<Long> productIds) {
        GetProductInfoBatchResponse response = productGrpcService.getProductInfoBatch(productIds);

        return response.getProductInfoListList().stream()
                .collect(Collectors.toMap(ProductInfo::getProductId, Function.identity()));
    }

    private void validateStock(Map<Long, ProductInfo> productInfoMap, List<CartItem> cartItems) {
        for (CartItem cartItem : cartItems) {
            ProductInfo productInfo = productInfoMap.get(cartItem.getProductId());

            if (productInfo == null) {
                throw new NotFoundException("Product not found");
            }

            if (cartItem.getQuantity() > productInfo.getAvailableQuantity()) {
                throw new InsufficientStockException("Insufficient stock for product " + cartItem.getProductId());
            }
        }

    }

    private List<CreateOrderItem> createOrderItems(List<CartItem> cartItems, Order order, Map<Long, ProductInfo> productInfoMap) {
        return cartItems.parallelStream()
                .map(item -> {
                    ProductInfo productInfo = productInfoMap.get(item.getProductId());
                    return orderMapper.toCreateOrderItem(productInfo, item.getQuantity(), order);
                }).toList();
    }

    private void saveOrderItems(List<CartItem> cartItems, Order order, Map<Long, ProductInfo> productInfoMap) {
        List<CreateOrderItem> createOrderItems = createOrderItems(cartItems, order, productInfoMap);

        orderItemService.saveAllOrderItems(createOrderItems);
    }
}
