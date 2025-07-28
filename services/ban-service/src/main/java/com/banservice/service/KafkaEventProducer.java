package com.banservice.service;

import com.banservice.dto.event.UserBannedEvent;
import com.banservice.exception.InternalServiceException;
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
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendUserBanned(String email, String reason) {
        UserBannedEvent event = new UserBannedEvent(email, reason);
        sendEvent("user.banned", event);
    }

    private void sendEvent(String topic, Object event) {
        try {
            kafkaTemplate.send(topic, objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new InternalServiceException("Something went wrong");
        }
    }
}
