package com.orderservice.config.statemachine;

import com.orderservice.entity.OrderStatusEnum;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@Configuration
@EnableStateMachineFactory
public class StateMachineConfig extends EnumStateMachineConfigurerAdapter<OrderStatusEnum, OrderEvents> {
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
                    .source(OrderStatusEnum.PAID)
                    .target(OrderStatusEnum.PROCESSING)
                    .event(OrderEvents.PROCESS)
                    .and()
                .withExternal()
                    .source(OrderStatusEnum.PROCESSING)
                    .target(OrderStatusEnum.SHIPPED)
                    .event(OrderEvents.SHIP)
                    .and()
                .withExternal()
                    .source(OrderStatusEnum.SHIPPED)
                    .target(OrderStatusEnum.DELIVERED)
                    .event(OrderEvents.DELIVER)
                    .and()
                .withExternal()
                    .source(OrderStatusEnum.DELIVERED)
                    .target(OrderStatusEnum.CANCELLED)
                    .event(OrderEvents.COMPLETE)
                    .and()

                .withExternal()
                    .source(OrderStatusEnum.CREATED)
                    .target(OrderStatusEnum.FAILED)
                    .event(OrderEvents.FAIL)
                    .and()
                .withExternal()
                    .source(OrderStatusEnum.FAILED)
                    .target(OrderStatusEnum.PAID)
                    .event(OrderEvents.PAY);

    }
}
