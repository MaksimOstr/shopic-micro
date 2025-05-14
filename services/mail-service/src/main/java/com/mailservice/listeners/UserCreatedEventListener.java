package com.mailservice.listeners;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mailservice.dto.event.UserCreatedEvent;
import com.mailservice.services.MailService;
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

    private final MailService mailService;
    private final ObjectMapper objectMapper;
    private final CodeServiceGrpc.CodeServiceBlockingStub codeServiceGrpc;

    @RetryableTopic(attempts = "2", backoff = @Backoff(delay = 1000))
    @KafkaListener(topics = "user-created", groupId = "mail-service")
    public void sendEmailVerificationCode(String data, Acknowledgment acknowledgment) {
        try {
            log.info("Received data: {}", data);
            UserCreatedEvent event = objectMapper.readValue(data,  UserCreatedEvent.class);
            CreateCodeRequest request = CreateCodeRequest.newBuilder()
                    .setScope(CodeScopeEnum.EMAIL_VERIFICATION)
                    .setUserId(event.userId())
                    .build();
            CreateCodeResponse verificationCode = codeServiceGrpc.getCode(request);

            String text = "Your verification code is: " + verificationCode.getCode();
            String subject = "Email verification";

            mailService.send(event.email(), subject, text);
            acknowledgment.acknowledge();
        } catch (JacksonException | MailException e) {
            log.error(e.getMessage());
        }
    }
}
