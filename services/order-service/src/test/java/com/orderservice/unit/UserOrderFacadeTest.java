package com.orderservice.unit;

import com.orderservice.dto.CreateOrderRequest;
import com.orderservice.entity.Order;
import com.orderservice.entity.OrderDeliveryTypeEnum;
import com.orderservice.entity.OrderItem;
import com.orderservice.exception.ApiException;
import com.orderservice.mapper.GrpcMapper;
import com.orderservice.mapper.OrderMapper;
import com.orderservice.service.KafkaService;
import com.orderservice.service.OrderService;
import com.orderservice.service.UserOrderFacade;
import com.orderservice.service.grpc.CartGrpcService;
import com.orderservice.service.grpc.PaymentGrpcService;
import com.orderservice.service.grpc.ProductGrpcService;
import com.shopic.grpc.cartservice.CartItem;
import com.shopic.grpc.cartservice.CartResponse;
import com.shopic.grpc.paymentservice.CreatePaymentResponse;
import com.shopic.grpc.productservice.ReserveProductsResponse;
import com.shopic.grpc.productservice.ReservedProduct;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserOrderFacadeTest {

    @Mock
    private OrderService orderService;
    @Mock
    private CartGrpcService cartGrpcService;
    @Mock
    private ProductGrpcService productGrpcService;
    @Mock
    private PaymentGrpcService paymentGrpcService;
    @Mock
    private GrpcMapper grpcMapper;

    @Mock
    private KafkaService kafkaService;

    @InjectMocks
    private UserOrderFacade userOrderFacade;

    @Test
    void placeOrder_returnsCheckoutUrl_whenAllServicesSucceed() {
        UUID userId = UUID.randomUUID();
        CreateOrderRequest request = new CreateOrderRequest(
                "John", "+12345", OrderDeliveryTypeEnum.COURIER, "Addr", "Comm"
        );
        String expectedUrl = "http://checkout.url";

        CartResponse cartResponse = mock(CartResponse.class);
        List<CartItem> cartItems = List.of(mock(CartItem.class));
        Map<String, CartItem> cartItemMap = new HashMap<>();

        Order order = new Order();
        order.setId(UUID.randomUUID());

        ReserveProductsResponse reserveResponse = mock(ReserveProductsResponse.class);
        List<ReservedProduct> reservedProducts = List.of(mock(ReservedProduct.class));

        OrderItem validItem = new OrderItem();
        validItem.setPriceAtPurchase(BigDecimal.TEN);
        validItem.setQuantity(1);
        List<OrderItem> orderItems = List.of(validItem);

        CreatePaymentResponse paymentResponse = mock(CreatePaymentResponse.class);

        when(cartGrpcService.getCart(userId)).thenReturn(cartResponse);
        when(cartResponse.getCartItemsList()).thenReturn(cartItems);
        when(orderService.createOrder(request, userId)).thenReturn(order);
        when(grpcMapper.getCartItemMap(cartItems)).thenReturn(cartItemMap);
        when(productGrpcService.reserveProducts(cartItems, cartItemMap, order.getId())).thenReturn(reserveResponse);
        when(reserveResponse.getProductsList()).thenReturn(reservedProducts);
        when(grpcMapper.toOrderItemList(reservedProducts, cartItemMap)).thenReturn(orderItems);
        when(paymentGrpcService.createPayment(userId, order)).thenReturn(paymentResponse);
        when(paymentResponse.getCheckoutUrl()).thenReturn(expectedUrl);

        String result = userOrderFacade.placeOrder(request, userId);

        assertThat(result).isEqualTo(expectedUrl);
        assertThat(order.getOrderItems()).containsAll(orderItems);
        assertThat(order.getTotalPrice()).isEqualByComparingTo(BigDecimal.TEN);
        verify(kafkaService, never()).sendOrderFailedEvent(any());
    }

    @Test
    void placeOrder_sendsKafkaEventAndRethrows_whenPaymentFails() {
        UUID userId = UUID.randomUUID();
        CreateOrderRequest request = new CreateOrderRequest(
                "John", "+12345", OrderDeliveryTypeEnum.COURIER, "Addr", "Comm"
        );

        CartResponse cartResponse = mock(CartResponse.class);
        Order order = new Order();
        order.setId(UUID.randomUUID());

        when(cartGrpcService.getCart(userId)).thenReturn(cartResponse);
        when(orderService.createOrder(request, userId)).thenReturn(order);
        when(productGrpcService.reserveProducts(any(), any(), any()))
                .thenReturn(mock(ReserveProductsResponse.class));
        when(paymentGrpcService.createPayment(userId, order))
                .thenThrow(new ApiException("Payment failed", HttpStatus.BAD_REQUEST));

        assertThrows(ApiException.class, () -> userOrderFacade.placeOrder(request, userId));

        verify(kafkaService).sendOrderFailedEvent(order.getId());
    }

    @Test
    void placeOrder_propagatesException_whenCartServiceFails() {
        UUID userId = UUID.randomUUID();
        CreateOrderRequest request = new CreateOrderRequest(
                "John", "+12345", OrderDeliveryTypeEnum.COURIER, "Addr", "Comm"
        );

        when(cartGrpcService.getCart(userId)).thenThrow(new RuntimeException("Cart service down"));

        assertThrows(RuntimeException.class, () -> userOrderFacade.placeOrder(request, userId));

        verify(orderService, never()).createOrder(any(), any());
        verify(paymentGrpcService, never()).createPayment(any(), any());
        verify(kafkaService, never()).sendOrderFailedEvent(any());
    }

    @Test
    void placeOrder_propagatesException_whenReservationFails() {
        UUID userId = UUID.randomUUID();
        CreateOrderRequest request = new CreateOrderRequest(
                "John", "+12345", OrderDeliveryTypeEnum.COURIER, "Addr", "Comm"
        );
        CartResponse cartResponse = mock(CartResponse.class);
        Order order = new Order();
        order.setId(UUID.randomUUID());

        when(cartGrpcService.getCart(userId)).thenReturn(cartResponse);
        when(orderService.createOrder(request, userId)).thenReturn(order);
        when(productGrpcService.reserveProducts(any(), any(), any()))
                .thenThrow(new ApiException("Out of stock", HttpStatus.CONFLICT));

        assertThrows(ApiException.class, () -> userOrderFacade.placeOrder(request, userId));

        verify(paymentGrpcService, never()).createPayment(any(), any());
        verify(kafkaService, never()).sendOrderFailedEvent(any());
    }
}