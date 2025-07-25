package com.orderservice.config.statemachine;

import com.orderservice.config.statemachine.action.CancelAction;
import com.orderservice.config.statemachine.action.CompleteAction;
import com.orderservice.entity.OrderStatusEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@Slf4j
@Configuration
@EnableStateMachineFactory
@RequiredArgsConstructor
public class StateMachineConfig extends EnumStateMachineConfigurerAdapter<OrderStatusEnum, OrderEvents> {
    private final StateMachineLogListener listener;
    private final CompleteAction completeAction;
    private final CancelAction cancelAction;


    @Override
    public void configure(StateMachineConfigurationConfigurer<OrderStatusEnum, OrderEvents> config) throws Exception {
        config
                .withConfiguration()
                .listener(listener);
    }

    @Override
    public void configure(StateMachineStateConfigurer<OrderStatusEnum, OrderEvents> states) throws Exception {
        states
                .withStates()
                .initial(OrderStatusEnum.CREATED)
                .states(EnumSet.allOf(OrderStatusEnum.class))
                .end(OrderStatusEnum.CANCELLED)
                .end(OrderStatusEnum.COMPLETED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<OrderStatusEnum, OrderEvents> transitions) throws Exception {
        transitions
                .withExternal()
                    .source(OrderStatusEnum.CREATED)
                    .target(OrderStatusEnum.PAID)
                    .event(OrderEvents.PAY)
                    .and()
                .withExternal()
                    .source(OrderStatusEnum.CREATED)
                    .target(OrderStatusEnum.CANCELLED)
                    .event(OrderEvents.CANCEL)
                    .action(cancelAction)
                    .and()
                .withExternal()
                    .source(OrderStatusEnum.CREATED)
                    .target(OrderStatusEnum.FAILED)
                    .event(OrderEvents.FAIL)
                    .and()

                .withExternal()
                    .source(OrderStatusEnum.PAID)
                    .target(OrderStatusEnum.PROCESSING)
                    .event(OrderEvents.PROCESS)
                    .and()
                .withExternal()
                    .source(OrderStatusEnum.PAID)
                    .target(OrderStatusEnum.CANCELLED_PAID)
                    .action(cancelAction)
                    .event(OrderEvents.CANCEL)
                    .and()

                .withExternal()
                    .source(OrderStatusEnum.PROCESSING)
                    .target(OrderStatusEnum.SHIPPED)
                    .event(OrderEvents.SHIP)
                    .and()
                .withExternal()
                    .source(OrderStatusEnum.PROCESSING)
                    .target(OrderStatusEnum.CANCELLED_PAID)
                    .event(OrderEvents.CANCEL)
                    .action(cancelAction)
                    .and()

                .withExternal()
                    .source(OrderStatusEnum.SHIPPED)
                    .target(OrderStatusEnum.READY_FOR_PICKUP)
                    .event(OrderEvents.PICKUP_READY)
                    .and()

                .withExternal()
                    .source(OrderStatusEnum.READY_FOR_PICKUP)
                    .target(OrderStatusEnum.COMPLETED)
                    .action(completeAction)
                    .event(OrderEvents.COMPLETE)
                    .and()
                .withExternal()
                    .source(OrderStatusEnum.READY_FOR_PICKUP)
                    .target(OrderStatusEnum.RETURNED)
                    .event(OrderEvents.RETURN)
                    .and();

    }
}
