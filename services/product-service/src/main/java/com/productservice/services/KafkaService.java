package com.productservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.productservice.dto.event.BaseReservationEvent;
import com.productservice.exceptions.ApiException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaService {
    private final KafkaTemplate<String, String> atLeastOnceBatchTemplate;
    private final ObjectMapper objectMapper;

    public void sendReservationConfirmed(UUID orderId) {
        try {
            BaseReservationEvent event = new BaseReservationEvent(orderId);

            atLeastOnceBatchTemplate.send("reservation.confirmed", objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new ApiException("Something went wrong. Please try again later", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
