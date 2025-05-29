package com.mailservice.services;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mailservice.dto.event.EmailVerifyRequestDto;
import com.mailservice.dto.event.UserCreatedEvent;
import com.mailservice.services.grpc.CodeGrpcService;
import com.shopic.grpc.codeservice.CodeScopeEnum;
import com.shopic.grpc.codeservice.CreateCodeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.mail.MailException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaListenerService {

    private final ObjectMapper objectMapper;
    private final CodeGrpcService codeGrpcService;
    private final MailService mailService;

    @RetryableTopic(attempts = "2", backoff = @Backoff(delay = 1000))
    @KafkaListener(topics = "user-created", groupId = "mail-service")
    public void sendEmailVerificationCode(String data, Acknowledgment acknowledgment) {
        try {
            log.info("Received data: {}", data);

            UserCreatedEvent event = objectMapper.readValue(data,  UserCreatedEvent.class);

            sendEmailVerificationCode(event.email(), event.userId());
            acknowledgment.acknowledge();
        } catch (JacksonException | MailException e) {
            log.error(e.getMessage());
        }
    }


    @RetryableTopic(attempts = "2", backoff = @Backoff(delay = 1000))
    @org.springframework.kafka.annotation.KafkaListener(topics = "email-verification-requested", groupId = "mail-service")
    public void sendRetryEmailVerificationCode(String data, Acknowledgment acknowledgment) {
        try {
            log.info("Received data: {}", data);

            EmailVerifyRequestDto event = objectMapper.readValue(data,  EmailVerifyRequestDto.class);

            sendEmailVerificationCode(event.email(), event.userId());
            acknowledgment.acknowledge();
        } catch (JacksonException | MailException e) {
            log.error(e.getMessage());
        }
    }

    private void sendEmailVerificationCode(String email, long userId) {
        CreateCodeResponse verificationCode = codeGrpcService.getCode(CodeScopeEnum.EMAIL_VERIFICATION,  userId);
        String subject = "Email verification";
        String text = "Your verification code is: " + verificationCode.getCode();

        mailService.send(email, subject, text);
    }
}
