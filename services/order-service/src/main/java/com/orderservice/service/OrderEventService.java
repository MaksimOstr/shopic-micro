package com.orderservice.service;

import com.orderservice.config.statemachine.OrderEvents;
import com.orderservice.entity.Order;
import com.orderservice.entity.OrderStatusEnum;
import com.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderEventService {
    private final OrderRepository orderRepository;
    private final OrderQueryService orderQueryService;
    private final StateMachineFactory<OrderStatusEnum, OrderEvents> stateMachineFactory;


    public Order payOrder(long orderId) {
        return processEvent(orderId, OrderEvents.PAY);
    }

    public Order processOrder(long orderId) {
        return processEvent(orderId, OrderEvents.PROCESS);
    }

    public Order shipOrder(long orderId) {
        return processEvent(orderId, OrderEvents.SHIP);
    }

    public Order deliverOrder(long orderId) {
        return processEvent(orderId, OrderEvents.DELIVER);
    }

    public Order completeOrder(long orderId) {
        return processEvent(orderId, OrderEvents.COMPLETE);
    }

    public Order cancelOrder(long orderId) {
        return processEvent(orderId, OrderEvents.CANCEL);
    }

    public Order failOrder(long orderId) {
        return processEvent(orderId, OrderEvents.FAIL);
    }

    private Order processEvent(Long orderId, OrderEvents event) {
        log.info("Processing order event: {}", event);
        Order order = orderQueryService.getOrderById(orderId);


        StateMachine<OrderStatusEnum, OrderEvents> sm = build(order);

        Message<OrderEvents> message = MessageBuilder
                .withPayload(event)
                .setHeader("order", order)
                .build();

        sm.sendEvent(Mono.just(message))
                .doOnError(e -> log.error("Event processing failed", e))
                .blockFirst();

        order.setStatus(sm.getState().getId());
        return orderRepository.save(order);
    }

    private StateMachine<OrderStatusEnum, OrderEvents> build(Order order) {
        StateMachine<OrderStatusEnum, OrderEvents> sm = stateMachineFactory.getStateMachine(order.getId().toString());

        sm.stopReactively().block();

        sm.getStateMachineAccessor()
                .doWithAllRegions(access -> {
                    access.resetStateMachineReactively(
                            new DefaultStateMachineContext<>(
                                    order.getStatus(),
                                    null,
                                    null,
                                    null,
                                    null,
                                    order.getId().toString()
                            )
                    ).block();
                });

        sm.startReactively().block();
        return sm;
    }
}
