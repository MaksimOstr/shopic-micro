package com.mailservice.services;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mailservice.dto.event.SendCodeEvent;
import com.mailservice.dto.event.UserBannedEvent;
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
    private final MailService mailService;


    @RetryableTopic(attempts = "2", backoff = @Backoff(delay = 1000))
    @KafkaListener(topics = {"user-created", "email-verification-requested"}, groupId = "mail-service")
    public void sendEmailVerificationCode(String data, Acknowledgment acknowledgment) {
        String subject = "Email verification";
        String text = "Your verification code is: ";

        sendCode(data, subject, text);

        acknowledgment.acknowledge();
    }

    @RetryableTopic(attempts = "2", backoff = @Backoff(delay = 1000))
    @KafkaListener(topics = {"password-reset-requested"}, groupId = "mail-service")
    public void sendResetPasswordCode(String data, Acknowledgment acknowledgment) {
        String subject = "Reset Password";
        String text = "Reset password code: ";

        sendCode(data, subject, text);

        acknowledgment.acknowledge();
    }

    @RetryableTopic(attempts = "2", backoff = @Backoff(delay = 1000))
    @KafkaListener(topics = {"email-change-requested"}, groupId = "mail-service")
    public void sendEmailChangeCode(String data, Acknowledgment acknowledgment) {
        String subject = "Change email";
        String text = "Code for changing email: ";

        sendCode(data, subject, text);

        acknowledgment.acknowledge();
    }

    @RetryableTopic(attempts = "2", backoff = @Backoff(delay = 1000))
    @KafkaListener(topics = {"user.banned"}, groupId = "mail-service")
    public void listenUserBanned(String data, Acknowledgment acknowledgment) {
        try {
            UserBannedEvent event = objectMapper.readValue(data, UserBannedEvent.class);

            String subject = "Your account was banned";

            String text = "Your account was banned: " + event.reason();

            mailService.send(event.email(), subject, text);

            acknowledgment.acknowledge();
        } catch (JacksonException e) {
            log.error(e.getMessage());
        }
    }



    private void sendCode(String data, String subject, String text) {
        try {
            SendCodeEvent event = objectMapper.readValue(data, SendCodeEvent.class);
            mailService.send(event.email(), subject, text + event.code());
        } catch (JacksonException e) {
            log.error(e.getMessage());
        }
    }
}
