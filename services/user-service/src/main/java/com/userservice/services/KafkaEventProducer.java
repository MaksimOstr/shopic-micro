package com.userservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.userservice.dto.event.ChangeEmailEvent;
import com.userservice.dto.event.EmailVerifyRequestDto;
import com.userservice.dto.event.PasswordResetEvent;
import com.userservice.dto.event.UserBannedEvent;
import com.userservice.exceptions.InternalServiceException;
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

    public void requestEmailVerification(String code, String email) {
        EmailVerifyRequestDto event = new EmailVerifyRequestDto(code, email);
        sendEvent("email-verification-requested", event);
    }

    public void requestResetPassword(String code, String email) {
        PasswordResetEvent event = new PasswordResetEvent(email, code);
        sendEvent("password-reset-requested", event);
    }

    public void requestEmailChange(String code, String email) {
        ChangeEmailEvent event = new ChangeEmailEvent(code, email);
        sendEvent("email-change-requested", event);
    }

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
