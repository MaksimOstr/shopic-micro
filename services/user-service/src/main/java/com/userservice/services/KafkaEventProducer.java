package com.userservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.userservice.dto.event.EmailVerifyRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaEventProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void requestEmailVerification(long userId, String email) throws JsonProcessingException {
        EmailVerifyRequestDto event = new EmailVerifyRequestDto(userId, email);

        kafkaTemplate.send("email-verification-requested", objectMapper.writeValueAsString(event));
    }
}
