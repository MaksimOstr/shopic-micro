package com.orderservice.unit;

import com.orderservice.dto.CreateOrderRequest;
import com.orderservice.dto.UpdateContactInfoRequest;
import com.orderservice.entity.Order;
import com.orderservice.entity.OrderDeliveryTypeEnum;
import com.orderservice.entity.OrderStatusEnum;
import com.orderservice.exception.NotFoundException;
import com.orderservice.repository.OrderRepository;
import com.orderservice.service.OrderService;
import com.orderservice.service.calculator.DeliveryPriceCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private DeliveryPriceCalculator deliveryPriceCalculator;

    @Mock
    private DeliveryPriceCalculator secondDeliveryPriceCalculator;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, List.of(deliveryPriceCalculator));
    }

    @Test
    void createOrder_createsOrderWithDeliveryPrice_whenValidRequestAndCalculatorExists() {
        UUID userId = UUID.randomUUID();
        CreateOrderRequest request = new CreateOrderRequest(
                "John", "+123", OrderDeliveryTypeEnum.COURIER, "Address", "Comment"
        );
        BigDecimal expectedPrice = BigDecimal.TEN;

        when(deliveryPriceCalculator.getDeliveryType()).thenReturn(OrderDeliveryTypeEnum.COURIER);
        when(deliveryPriceCalculator.calculateDeliveryPrice(any(), any())).thenReturn(expectedPrice);
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);

        Order result = orderService.createOrder(request, userId);

        assertThat(result.getDeliveryPrice()).isEqualTo(expectedPrice);
        assertThat(result.getStatus()).isEqualTo(OrderStatusEnum.PENDING);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void createOrder_selectsCorrectCalculator_whenMultipleCalculatorsExist() {
        orderService = new OrderService(orderRepository, List.of(deliveryPriceCalculator, secondDeliveryPriceCalculator));
        UUID userId = UUID.randomUUID();
        CreateOrderRequest request = new CreateOrderRequest(
                "John", "+123", OrderDeliveryTypeEnum.EXPRESS_DELIVERY, "Address", "Comment"
        );

        when(deliveryPriceCalculator.getDeliveryType()).thenReturn(OrderDeliveryTypeEnum.COURIER);
        when(secondDeliveryPriceCalculator.getDeliveryType()).thenReturn(OrderDeliveryTypeEnum.EXPRESS_DELIVERY);
        when(secondDeliveryPriceCalculator.calculateDeliveryPrice(any(), any())).thenReturn(BigDecimal.ONE);
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);

        Order result = orderService.createOrder(request, userId);

        assertThat(result.getDeliveryType()).isEqualTo(OrderDeliveryTypeEnum.EXPRESS_DELIVERY);
        verify(secondDeliveryPriceCalculator).calculateDeliveryPrice(any(), any());
        verify(deliveryPriceCalculator, never()).calculateDeliveryPrice(any(), any());
    }

    @Test
    void createOrder_throwsNotFoundException_whenCalculatorListIsEmpty() {
        OrderService emptyService = new OrderService(orderRepository, Collections.emptyList());
        UUID userId = UUID.randomUUID();
        CreateOrderRequest request = new CreateOrderRequest(
                "John", "+123", OrderDeliveryTypeEnum.COURIER, "Address", "Comment"
        );

        assertThrows(NotFoundException.class, () -> emptyService.createOrder(request, userId));
    }

    @Test
    void createOrder_throwsNotFoundException_whenNoMatchingCalculatorFound() {
        UUID userId = UUID.randomUUID();
        CreateOrderRequest request = new CreateOrderRequest(
                "John", "+123", OrderDeliveryTypeEnum.SELF_PICKUP, "Address", "Comment"
        );

        when(deliveryPriceCalculator.getDeliveryType()).thenReturn(OrderDeliveryTypeEnum.COURIER);

        assertThrows(NotFoundException.class, () -> orderService.createOrder(request, userId));
    }

    @Test
    void updateOrderContactInfo_updatesAllFields_whenAllFieldsProvided() {
        UUID orderId = UUID.randomUUID();
        Order existingOrder = new Order();
        existingOrder.setCustomerName("Old Name");
        existingOrder.setAddress("Old Address");
        UpdateContactInfoRequest request = new UpdateContactInfoRequest("New Name", "New Address");

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));

        Order result = orderService.updateOrderContactInfo(orderId, request);

        assertThat(result.getCustomerName()).isEqualTo("New Name");
        assertThat(result.getAddress()).isEqualTo("New Address");
    }

    @Test
    void updateOrderContactInfo_updatesOnlyCustomerName_whenAddressIsNull() {
        UUID orderId = UUID.randomUUID();
        Order existingOrder = new Order();
        existingOrder.setCustomerName("Old Name");
        existingOrder.setAddress("Old Address");
        UpdateContactInfoRequest request = new UpdateContactInfoRequest("New Name", null);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));

        Order result = orderService.updateOrderContactInfo(orderId, request);

        assertThat(result.getCustomerName()).isEqualTo("New Name");
        assertThat(result.getAddress()).isEqualTo("Old Address");
    }

    @Test
    void updateOrderContactInfo_updatesOnlyAddress_whenCustomerNameIsNull() {
        UUID orderId = UUID.randomUUID();
        Order existingOrder = new Order();
        existingOrder.setCustomerName("Old Name");
        existingOrder.setAddress("Old Address");
        UpdateContactInfoRequest request = new UpdateContactInfoRequest(null, "New Address");

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));

        Order result = orderService.updateOrderContactInfo(orderId, request);

        assertThat(result.getCustomerName()).isEqualTo("Old Name");
        assertThat(result.getAddress()).isEqualTo("New Address");
    }

    @Test
    void updateOrderContactInfo_makesNoChanges_whenRequestFieldsAreNull() {
        UUID orderId = UUID.randomUUID();
        Order existingOrder = new Order();
        existingOrder.setCustomerName("Old Name");
        existingOrder.setAddress("Old Address");
        UpdateContactInfoRequest request = new UpdateContactInfoRequest(null, null);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));

        Order result = orderService.updateOrderContactInfo(orderId, request);

        assertThat(result.getCustomerName()).isEqualTo("Old Name");
        assertThat(result.getAddress()).isEqualTo("Old Address");
    }

    @Test
    void updateOrderContactInfo_throwsNotFoundException_whenOrderNotFound() {
        UUID orderId = UUID.randomUUID();
        UpdateContactInfoRequest request = new UpdateContactInfoRequest("Name", "Address");

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.updateOrderContactInfo(orderId, request));
    }

    @Test
    void updateOrderStatus_changesStatusAndSaves_whenOrderExists() {
        UUID orderId = UUID.randomUUID();
        Order existingOrder = Order.builder()
                .status(OrderStatusEnum.SHIPPED)
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(orderRepository.save(existingOrder)).thenReturn(existingOrder);

        Order result = orderService.updateOrderStatus(orderId, OrderStatusEnum.COMPLETED);

        verify(orderRepository).save(existingOrder);
        assertThat(result.getStatus()).isEqualTo(OrderStatusEnum.COMPLETED);
    }

    @Test
    void updateOrderStatus_throwsApiException_whenTransitionIsInvalid() {
        UUID orderId = UUID.randomUUID();
        Order existingOrder = Order.builder()
                .status(OrderStatusEnum.PENDING)
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));

        assertThrows(com.orderservice.exception.ApiException.class,
                () -> orderService.updateOrderStatus(orderId, OrderStatusEnum.COMPLETED));

        verify(orderRepository, never()).save(any());
    }

    @Test
    void updateOrderStatus_cancelsOrder_whenStatusIsPending() {
        UUID orderId = UUID.randomUUID();
        Order existingOrder = Order.builder()
                .status(OrderStatusEnum.PENDING)
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(orderRepository.save(existingOrder)).thenReturn(existingOrder);

        Order result = orderService.updateOrderStatus(orderId, OrderStatusEnum.CANCELLED);

        assertThat(result.getStatus()).isEqualTo(OrderStatusEnum.CANCELLED);
        verify(orderRepository).save(existingOrder);
    }

    @Test
    void getOrderWithItems_returnsOrder_whenFound() {
        UUID orderId = UUID.randomUUID();
        Order order = new Order();
        when(orderRepository.findByIdWithItems(orderId)).thenReturn(Optional.of(order));

        Order result = orderService.getOrderWithItems(orderId);

        assertThat(result).isSameAs(order);
    }

    @Test
    void getOrderWithItems_throwsNotFoundException_whenNotFound() {
        UUID orderId = UUID.randomUUID();
        when(orderRepository.findByIdWithItems(orderId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.getOrderWithItems(orderId));
    }

    @Test
    void getOrderById_returnsOrder_whenFound() {
        UUID orderId = UUID.randomUUID();
        Order order = new Order();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        Order result = orderService.getOrderById(orderId);

        assertThat(result).isSameAs(order);
    }

    @Test
    void getOrderById_throwsNotFoundException_whenNotFound() {
        UUID orderId = UUID.randomUUID();
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.getOrderById(orderId));
    }

    @Test
    void getOrdersBySpec_returnsPageFromRepository_always() {
        Specification<Order> spec = mock(Specification.class);
        Pageable pageable = mock(Pageable.class);
        Page<Order> expectedPage = new PageImpl<>(List.of(new Order()));

        when(orderRepository.findAll(spec, pageable)).thenReturn(expectedPage);

        Page<Order> result = orderService.getOrdersBySpec(spec, pageable);

        assertThat(result).isSameAs(expectedPage);
    }

    @Test
    void getOrdersBySpec_returnsEmptyPage_whenRepositoryReturnsEmpty() {
        Specification<Order> spec = mock(Specification.class);
        Pageable pageable = mock(Pageable.class);

        when(orderRepository.findAll(spec, pageable)).thenReturn(Page.empty());

        Page<Order> result = orderService.getOrdersBySpec(spec, pageable);

        assertThat(result).isEmpty();
    }
}