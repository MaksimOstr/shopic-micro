package com.orderservice.unit;

import com.orderservice.dto.*;
import com.orderservice.entity.Order;
import com.orderservice.entity.OrderStatusEnum;
import com.orderservice.exception.ApiException;
import com.orderservice.exception.NotFoundException;
import com.orderservice.mapper.OrderMapper;
import com.orderservice.service.AdminOrderFacade;
import com.orderservice.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminOrderFacadeTest {

    @Mock
    private OrderService orderService;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private AdminOrderFacade adminOrderFacade;

    @Test
    void getOrder_returnsDto_whenOrderExists() {
        UUID orderId = UUID.randomUUID();
        Order order = new Order();
        AdminOrderDto expectedDto = mock(AdminOrderDto.class);

        when(orderService.getOrderWithItems(orderId)).thenReturn(order);
        when(orderMapper.toAdminOrderDto(order)).thenReturn(expectedDto);

        AdminOrderDto result = adminOrderFacade.getOrder(orderId);

        assertThat(result).isEqualTo(expectedDto);
    }

    @Test
    void getOrder_throwsNotFoundException_whenOrderServiceThrows() {
        UUID orderId = UUID.randomUUID();

        when(orderService.getOrderWithItems(orderId)).thenThrow(new NotFoundException("Order not found"));

        assertThrows(NotFoundException.class, () -> adminOrderFacade.getOrder(orderId));
        verify(orderMapper, never()).toAdminOrderDto(any());
    }

    @Test
    void getOrders_returnsPageOfDtos_whenOrdersExist() {
        AdminOrderParams params = new AdminOrderParams(UUID.randomUUID(), "John", "Doe", OrderStatusEnum.PENDING);
        Pageable pageable = Pageable.unpaged();
        Order order = new Order();
        List<Order> orders = List.of(order);
        Page<Order> orderPage = new PageImpl<>(orders, pageable, 1);
        AdminOrderPreviewDto previewDto = mock(AdminOrderPreviewDto.class);
        List<AdminOrderPreviewDto> dtos = List.of(previewDto);

        when(orderService.getOrdersBySpec(any(Specification.class), eq(pageable))).thenReturn(orderPage);
        when(orderMapper.toAdminOrderSummaryDto(orders)).thenReturn(dtos);

        Page<AdminOrderPreviewDto> result = adminOrderFacade.getOrders(params, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(previewDto);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void getOrders_returnsEmptyPage_whenNoOrdersFound() {
        AdminOrderParams params = new AdminOrderParams(null, null, null, null);
        Pageable pageable = Pageable.unpaged();
        Page<Order> emptyPage = Page.empty(pageable);

        when(orderService.getOrdersBySpec(any(Specification.class), eq(pageable))).thenReturn(emptyPage);
        when(orderMapper.toAdminOrderSummaryDto(Collections.emptyList())).thenReturn(Collections.emptyList());

        Page<AdminOrderPreviewDto> result = adminOrderFacade.getOrders(params, pageable);

        assertThat(result).isEmpty();
    }

    @Test
    void updateOrderContactInfo_returnsUpdatedDto_whenOrderExists() {
        UUID orderId = UUID.randomUUID();
        UpdateContactInfoRequest request = new UpdateContactInfoRequest("New Name", "New Address");
        Order updatedOrder = new Order();
        AdminOrderDto expectedDto = mock(AdminOrderDto.class);

        when(orderService.updateOrderContactInfo(orderId, request)).thenReturn(updatedOrder);
        when(orderMapper.toAdminOrderDto(updatedOrder)).thenReturn(expectedDto);

        AdminOrderDto result = adminOrderFacade.updateOrderContactInfo(orderId, request);

        assertThat(result).isEqualTo(expectedDto);
    }

    @Test
    void updateOrderContactInfo_propagatesException_whenServiceFails() {
        UUID orderId = UUID.randomUUID();
        UpdateContactInfoRequest request = new UpdateContactInfoRequest("Name", "Address");

        when(orderService.updateOrderContactInfo(orderId, request)).thenThrow(new NotFoundException("Not found"));

        assertThrows(NotFoundException.class, () -> adminOrderFacade.updateOrderContactInfo(orderId, request));
        verify(orderMapper, never()).toAdminOrderDto(any());
    }

    @Test
    void updateOrderStatus_returnsUpdatedDto_whenTransitionIsValid() {
        UUID orderId = UUID.randomUUID();
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest(OrderStatusEnum.SHIPPED);
        Order updatedOrder = new Order();
        AdminOrderDto expectedDto = mock(AdminOrderDto.class);

        when(orderService.updateOrderStatus(orderId, request.targetStatus())).thenReturn(updatedOrder);
        when(orderMapper.toAdminOrderDto(updatedOrder)).thenReturn(expectedDto);

        AdminOrderDto result = adminOrderFacade.updateOrderStatus(orderId, request);

        assertThat(result).isEqualTo(expectedDto);
    }

    @Test
    void updateOrderStatus_propagatesException_whenTransitionIsInvalid() {
        UUID orderId = UUID.randomUUID();
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest(OrderStatusEnum.COMPLETED);

        when(orderService.updateOrderStatus(orderId, request.targetStatus()))
                .thenThrow(new ApiException("Invalid transition", HttpStatus.BAD_REQUEST));

        assertThrows(ApiException.class, () -> adminOrderFacade.updateOrderStatus(orderId, request));
        verify(orderMapper, never()).toAdminOrderDto(any());
    }
}
