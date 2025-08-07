package com.authservice.services;

import com.authservice.dto.event.BasicSendCodeEvent;
import com.authservice.dto.event.LocalUserCreatedEvent;
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


    public void sendLocalUserCreated(String email, String code, String firstName, long userId, String lastName) {
        try {
            LocalUserCreatedEvent event = new LocalUserCreatedEvent(
                    email,
                    code,
                    userId,
                    firstName,
                    lastName
            );
            kafkaTemplate.send("user.local.registered", objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            throw new InternalServiceException(SOMETHING_WENT_WRONG);
        }
    }

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

    public void requestEmailVerification(String code, String email) {
        sendCodeEvent("email-verification-requested", code, email);
    }

    public void requestResetPassword(String code, String email) {
        sendCodeEvent("password-reset-requested", code, email);
    }

    public void requestEmailChange(String code, String email) {
        sendCodeEvent("email-change-requested", code, email);
    }

    public void sendJwkSetInvalidationEvent() {
        log.info("Sending jwk-set-invalidation event");
        kafkaTemplate.send("jwk-set-invalidation", "jwk-set-invalidation");
    }

    private void sendCodeEvent(String topic, String code, String email) {
        try {
            BasicSendCodeEvent event = new BasicSendCodeEvent(code, email);
            kafkaTemplate.send(topic, objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new InternalServiceException(SOMETHING_WENT_WRONG);
        }
    }
}
