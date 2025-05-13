package com.mailservice.services;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mailservice.dto.event.UserCreatedEvent;
import com.shopic.grpc.codeservice.CodeScopeEnum;
import com.shopic.grpc.codeservice.CodeServiceGrpc;
import com.shopic.grpc.codeservice.CreateCodeRequest;
import com.shopic.grpc.codeservice.CreateCodeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {
    private final MailSender javaMailSender;
    private final CodeServiceGrpc.CodeServiceBlockingStub codeServiceGrpc;
    private final ObjectMapper objectMapper;

    @Value("${spring.mail.from}")
    private String from;

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

            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(from);
            msg.setTo(event.email());
            msg.setText("Your verification code is: " + verificationCode.getCode());
            msg.setSubject("Email verification code");

            javaMailSender.send(msg);
            acknowledgment.acknowledge();

            log.info("Email sent successfully to {}", event.email());
        } catch (JacksonException | MailException e) {
            log.error(e.getMessage());
        }
    }
}
