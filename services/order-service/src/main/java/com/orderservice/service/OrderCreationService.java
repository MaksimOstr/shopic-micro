package com.orderservice.service;

import com.orderservice.dto.request.CreateOrderItem;
import com.orderservice.entity.Order;
import com.orderservice.entity.OrderStatusEnum;
import com.orderservice.exception.NotFoundException;
import com.orderservice.repository.OrderRepository;
import com.orderservice.service.grpc.CartGrpcService;
import com.orderservice.service.grpc.ProductGrpcService;
import com.shopic.grpc.cartservice.CartResponse;
import com.shopic.grpc.cartservice.CartItem;
import com.shopic.grpc.productservice.CheckAndReserveProductResponse;
import com.shopic.grpc.productservice.ProductInfo;
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
    private final OrderItemService orderItemService;
    private final CartGrpcService cartGrpcService;
    private final ProductGrpcService productGrpcService;
    private final KafkaEventProducer kafkaEventProducer;


    @Transactional
    public void createOrder(long userId) {
        CartResponse cartInfo = cartGrpcService.getCartInfo(userId);
        List<CartItem> cartItems = cartInfo.getCartItemsList();

        CheckAndReserveProductResponse response = productGrpcService.checkAndReserveProduct(cartItems);

        Map<Long, BigDecimal> productPriceMap = getProductPriceMap(response.getProductsList());

        Order savedOrder = createAndSaveOrderEntity(userId, productPriceMap, cartItems);
        saveOrderItems(cartItems, savedOrder, productPriceMap);
    }


    private Order createAndSaveOrderEntity(long userId, Map<Long, BigDecimal> priceMap, List<CartItem> cartItems) {
        BigDecimal totalPrice = calculateTotalPrice(priceMap, cartItems);
        Order order = Order.builder()
                .status(OrderStatusEnum.CREATED)
                .totalPrice(totalPrice)
                .userId(userId)
                .build();

        return orderRepository.save(order);
    }

    private BigDecimal calculateTotalPrice(Map<Long, BigDecimal> products, List<CartItem> cartItems) {
        return cartItems.stream().map(item -> {
            BigDecimal price = products.get(item.getProductId());

            if(price == null) {
                throw new NotFoundException("Product not found");
            }

            return price.multiply(new BigDecimal(item.getQuantity()));
        }).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Map<Long, BigDecimal> getProductPriceMap(List<ProductInfo> productInfoList) {

        return productInfoList.stream()
                .collect(Collectors.toMap(
                        ProductInfo::getProductId,
                        info -> new BigDecimal(info.getPrice())
                ));
    }

    private List<CreateOrderItem> createOrderItems(List<CartItem> cartItems, Order order, Map<Long, BigDecimal> priceMap) {
        return cartItems.parallelStream()
                .map(item -> {
                    BigDecimal price = priceMap.get(item.getProductId());
                    return new CreateOrderItem(
                            item.getProductId(),
                            item.getQuantity(),
                            price,
                            order.getId(),
                            item.getProductName(),
                            item.getProductImageUrl()
                    );
                }).toList();
    }

    private void saveOrderItems(List<CartItem> cartItems, Order order, Map<Long, BigDecimal> productInfoMap) {
        List<CreateOrderItem> createOrderItems = createOrderItems(cartItems, order, productInfoMap);

        orderItemService.saveAllOrderItems(createOrderItems);
    }
}
