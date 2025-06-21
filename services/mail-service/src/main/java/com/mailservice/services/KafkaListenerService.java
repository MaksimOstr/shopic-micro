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
import org.springframework.mail.MailSender;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaListenerService {

    private final ObjectMapper objectMapper;
    private final MailService mailService;


    @RetryableTopic(attempts = "2", backoff = @Backoff(delay = 1000))
    @KafkaListener(topics = "user-created", groupId = "mail-service")
    public void sendEmailVerificationCode(String data, Acknowledgment acknowledgment) {
        try {
            log.info("Received data: {}", data);

            UserCreatedEvent event = objectMapper.readValue(data,  UserCreatedEvent.class);

            sendEmailVerificationEmail(event.email(), event.code());

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

            sendEmailVerificationEmail(event.email(), event.code());

            acknowledgment.acknowledge();
        } catch (JacksonException e) {
            log.error(e.getMessage());
        }
    }

    private void sendEmailVerificationEmail(String email, String code) {
        String subject = "Email verification";
        String text = "Your verification code is: " + code;

        mailService.send(email, subject, text);
    }
}
