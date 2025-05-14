package com.mailservice.listeners;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mailservice.dto.event.EmailVerifyRequestDto;
import com.mailservice.dto.event.UserCreatedEvent;
import com.mailservice.services.MailService;
import com.mailservice.services.VerificationCodeSender;
import com.shopic.grpc.codeservice.CodeScopeEnum;
import com.shopic.grpc.codeservice.CodeServiceGrpc;
import com.shopic.grpc.codeservice.CreateCodeRequest;
import com.shopic.grpc.codeservice.CreateCodeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.mail.MailException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserCreatedEventListener {

    private final VerificationCodeSender verificationCodeSender;
    private final ObjectMapper objectMapper;



    @RetryableTopic(attempts = "2", backoff = @Backoff(delay = 1000))
    @KafkaListener(topics = "user-created", groupId = "mail-service")
    public void sendEmailVerificationCode(String data, Acknowledgment acknowledgment) {
        try {
            log.info("Received data: {}", data);
            UserCreatedEvent event = objectMapper.readValue(data,  UserCreatedEvent.class);
            verificationCodeSender.sendVerificationCode(event.email(), event.userId(), CodeScopeEnum.EMAIL_VERIFICATION);
            acknowledgment.acknowledge();
        } catch (JacksonException | MailException e) {
            log.error(e.getMessage());
        }
    }


    @RetryableTopic(attempts = "2", backoff = @Backoff(delay = 1000))
    @KafkaListener(topics = "email-verification-requested", groupId = "mail-service")
    public void sendRetryEmailVerificationCode(String data, Acknowledgment acknowledgment) {
        try {
            log.info("Received data: {}", data);
            EmailVerifyRequestDto event = objectMapper.readValue(data,  EmailVerifyRequestDto.class);
            verificationCodeSender.sendVerificationCode(event.email(), event.userId(), CodeScopeEnum.EMAIL_VERIFICATION);
            acknowledgment.acknowledge();
        } catch (JacksonException | MailException e) {
            log.error(e.getMessage());
        }
    }

}
