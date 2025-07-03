package com.orderservice.service;

import com.orderservice.config.statemachine.OrderEvents;
import com.orderservice.entity.Order;
import com.orderservice.entity.OrderStatusEnum;
import com.orderservice.exception.StateTransitionException;
import com.orderservice.repository.OrderRepository;
import jakarta.transaction.InvalidTransactionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderEventService {
    private final OrderRepository orderRepository;
    private final OrderQueryService orderQueryService;
    private final StateMachineFactory<OrderStatusEnum, OrderEvents> stateMachineFactory;

    @Transactional
    public Order payOrder(long orderId) {
        return processEvent(orderId, OrderEvents.PAY);
    }

    @Transactional
    public Order processOrder(long orderId) {
        return processEvent(orderId, OrderEvents.PROCESS);
    }

    @Transactional
    public Order shipOrder(long orderId) {
        return processEvent(orderId, OrderEvents.SHIP);
    }

    @Transactional
    public Order pickupReadyOrder(long orderId) {
        return processEvent(orderId, OrderEvents.PICKUP_READY);
    }

    @Transactional
    public Order completeOrder(long orderId) {
        return processEvent(orderId, OrderEvents.COMPLETE);
    }

    @Transactional
    public Order cancelOrder(long orderId) {
        return processEvent(orderId, OrderEvents.CANCEL);
    }

    @Transactional
    public Order failOrder(long orderId) {
        return processEvent(orderId, OrderEvents.FAIL);
    }


    protected Order processEvent(Long orderId, OrderEvents event) {
        log.info("Processing order event: {}", event);
        Order order = orderQueryService.getOrderById(orderId);


        StateMachine<OrderStatusEnum, OrderEvents> sm = build(order);
        Message<OrderEvents> message = MessageBuilder
                .withPayload(event)
                .setHeader("order", order)
                .build();
        OrderStatusEnum currentState = sm.getState().getId();

        if (!sm.sendEvent(message)) {
            log.error("""
                            Invalid transition attempt:
                            - Current state: {}
                            - Attempted event: {}""",
                    currentState, event);

            throw new StateTransitionException(currentState, event);
        }

        order.setStatus(sm.getState().getId());
        return orderRepository.save(order);
    }

    private StateMachine<OrderStatusEnum, OrderEvents> build(Order order) {
        StateMachine<OrderStatusEnum, OrderEvents> sm = stateMachineFactory.getStateMachine(order.getId().toString());

        sm.stopReactively().block();

        sm.getStateMachineAccessor()
                .doWithAllRegions(access -> {
                    access
                            .resetStateMachineReactively(
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
