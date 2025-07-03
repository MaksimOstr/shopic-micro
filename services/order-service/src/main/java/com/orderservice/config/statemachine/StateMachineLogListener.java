package com.orderservice.config.statemachine;

import com.orderservice.entity.Order;
import com.orderservice.entity.OrderStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;
import org.springframework.statemachine.transition.TransitionKind;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
public class StateMachineLogListener extends StateMachineListenerAdapter<OrderStatusEnum, OrderEvents> {
    @Override
    public void stateChanged(State<OrderStatusEnum, OrderEvents> from, State<OrderStatusEnum, OrderEvents> to) {
        log.info("StateMachineLogListener.stateChanged from " + from.getId() + " to " + to.getId());
    }


    @Override
    public void eventNotAccepted(Message<OrderEvents> event) {
        log.warn("Event not accepted: {} | Current state: {}",
                event.getPayload(),
                Objects.requireNonNull(event.getHeaders().get("order", Order.class)).getStatus());
    }

    @Override
    public void stateMachineError(StateMachine<OrderStatusEnum, OrderEvents> sm, Exception ex) {
        log.error("State Machine Error [{}]: {}", sm.getId(), ex.getMessage(), ex);
        sm.getExtendedState().getVariables().put("lastError", ex.getMessage());
    }

    @Override
    public void transition(Transition<OrderStatusEnum, OrderEvents> transition) {
        if (transition.getKind() == TransitionKind.EXTERNAL) {
            log.debug("Transition: {} → {} via {}",
                    transition.getSource().getId(),
                    transition.getTarget().getId(),
                    transition.getTrigger().getEvent());
        }
    }
}
