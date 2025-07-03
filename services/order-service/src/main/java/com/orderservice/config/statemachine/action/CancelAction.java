package com.orderservice.config.statemachine.action;

import com.orderservice.config.statemachine.OrderEvents;
import com.orderservice.entity.Order;
import com.orderservice.entity.OrderStatusEnum;
import com.orderservice.exception.NotFoundException;
import com.orderservice.service.KafkaService;
import lombok.RequiredArgsConstructor;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CancelAction implements Action<OrderStatusEnum, OrderEvents> {
    private final KafkaService kafkaService;

    @Override
    public void execute(StateContext<OrderStatusEnum, OrderEvents> context) {
        Order order = context.getMessage().getHeaders().get("order", Order.class);

        if(order != null) {
            kafkaService.sendOrderCanceledEvent(order.getId());
        } else {
            throw new NotFoundException("Order not found");
        }

    }
}
