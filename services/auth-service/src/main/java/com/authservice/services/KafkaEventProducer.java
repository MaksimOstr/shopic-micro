package com.authservice.services;

import com.authservice.dto.event.UserCreatedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaEventProducer {
    private final KafkaTemplate<Object, String> kafkaTemplate;
    private final ObjectMapper objectMapper;


    public void sendUserCreatedEvent(String email, long userId) throws JsonProcessingException {
        UserCreatedEvent event = new UserCreatedEvent(email, userId);
        kafkaTemplate.send("user-created", objectMapper.writeValueAsString(event));
    }

    public void sendJwkSetInvalidationEvent() {
        log.info("Sending jwk-set-invalidation event");
        kafkaTemplate.send("jwk-set-invalidation", "jwk-set-invalidation");
    }
}
