package com.productservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.productservice.dto.event.BaseReservationEvent;
import com.productservice.exceptions.InternalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaService {
    private final KafkaTemplate<String, String> atLeastOnceBatchTemplate;
    private final ObjectMapper objectMapper;

    private static final String SOMETHING_WENT_WRONG = "Something went wrong. Please try again later";

    public void sendReservationConfirmed(long orderId) {
        try {
            BaseReservationEvent event = new BaseReservationEvent(orderId);

            atLeastOnceBatchTemplate.send("reservation.confirmed", objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new InternalException(SOMETHING_WENT_WRONG);
        }
    }
}
