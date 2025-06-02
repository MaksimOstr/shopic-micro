package com.mailservice.services;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mailservice.dto.event.EmailVerifyRequestDto;
import com.mailservice.dto.event.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaListenerService {

    private final ObjectMapper objectMapper;
    private final EmailVerificationSender emailVerificationSender;


    @RetryableTopic(attempts = "2", backoff = @Backoff(delay = 1000))
    @KafkaListener(topics = "user-created", groupId = "mail-service")
    public void sendEmailVerificationCode(String data, Acknowledgment acknowledgment) {
        try {
            log.info("Received data: {}", data);

            UserCreatedEvent event = objectMapper.readValue(data,  UserCreatedEvent.class);

            emailVerificationSender.sendEmailVerificationEmail(event.email(), event.userId());

            acknowledgment.acknowledge();
        } catch (JacksonException e) {
            log.error(e.getMessage());
        }
    }


    @RetryableTopic(attempts = "2", backoff = @Backoff(delay = 1000))
    @KafkaListener(topics = "email-verification-requested", groupId = "mail-service")
    public void sendRetryEmailVerificationCode(String data, Acknowledgment acknowledgment) {
        try {
            log.info("Received data: {}", data);

            EmailVerifyRequestDto event = objectMapper.readValue(data,  EmailVerifyRequestDto.class);

            emailVerificationSender.sendEmailVerificationEmail(event.email(), event.userId());

            acknowledgment.acknowledge();
        } catch (JacksonException e) {
            log.error(e.getMessage());
        }
    }
}
