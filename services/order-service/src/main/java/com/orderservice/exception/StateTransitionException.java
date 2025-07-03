package com.orderservice.exception;

import com.orderservice.config.statemachine.OrderEvents;
import com.orderservice.entity.OrderStatusEnum;

import java.util.Collections;
import java.util.List;

public class StateTransitionException extends RuntimeException {
    private final OrderStatusEnum currentState;
    private final OrderEvents attemptedEvent;
    private final List<OrderEvents> allowedEvents;

    public StateTransitionException(OrderStatusEnum currentState,
                                    OrderEvents attemptedEvent) {
        super(String.format("Invalid transition from %s via %s",
                currentState, attemptedEvent));
        this.currentState = currentState;
        this.attemptedEvent = attemptedEvent;
        this.allowedEvents = Collections.emptyList();
    }


}
