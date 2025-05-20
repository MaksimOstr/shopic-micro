package com.authservice.services;

import com.authservice.dto.event.UserCreatedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaEventProducer {
    private final KafkaTemplate<Object, String> kafkaTemplate;
    private final ObjectMapper objectMapper;


    public void sendUserCreatedEvent(String email, long UserId) throws JsonProcessingException {
        UserCreatedEvent event = new UserCreatedEvent(email, UserId);
        kafkaTemplate.send("user-created", objectMapper.writeValueAsString(event));
    }
}
