package com.paymentservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymentservice.dto.event.CheckoutSuccessEvent;
import com.paymentservice.dto.event.UnpaidPaymentEvent;
import com.paymentservice.exception.InternalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaService {

    private final KafkaTemplate<String, String> atLeastOnceBatchTemplate;
    private final ObjectMapper objectMapper;

    private static final String SOMETHING_WENT_WRONG = "Something went wrong. Please try again later";

    public KafkaService (
            @Qualifier("atLeastOnceBatchTemplate") KafkaTemplate<String, String> atLeastOnceBatchTemplate,
            ObjectMapper objectMapper
    ) {
        this.atLeastOnceBatchTemplate = atLeastOnceBatchTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendCheckoutSessionSuccess(long orderId) {
        try {
            CheckoutSuccessEvent event = new CheckoutSuccessEvent(orderId);

            atLeastOnceBatchTemplate.send("payment.paid", objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new InternalException(SOMETHING_WENT_WRONG);
        }
    }

    public void sendUnpaidPaymentEvent(long orderId) {
        try {
            UnpaidPaymentEvent event = new UnpaidPaymentEvent(orderId);

            atLeastOnceBatchTemplate.send("payment.unpaid", objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new InternalException(SOMETHING_WENT_WRONG);
        }
    }
}
