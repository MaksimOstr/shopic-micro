package com.authservice.services;

import com.authservice.dto.event.OAuthUserCreated;
import com.authservice.exceptions.InternalServiceException;
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

    private static final String SOMETHING_WENT_WRONG = "Something went wrong. Please try again later.";


    public void sendOAuthUserCreated(String firstName, long userId, String lastName) {
        try {
            OAuthUserCreated event = new OAuthUserCreated(
                    userId,
                    firstName,
                    lastName
            );
            kafkaTemplate.send("user.oauth.registered", objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            throw new InternalServiceException(SOMETHING_WENT_WRONG);
        }
    }

    public void sendJwkSetInvalidationEvent() {
        log.info("Sending jwk-set-invalidation event");
        kafkaTemplate.send("jwk-set-invalidation", "jwk-set-invalidation");
    }
}
