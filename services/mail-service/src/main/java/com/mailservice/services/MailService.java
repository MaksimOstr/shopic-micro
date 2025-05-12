package com.mailservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {
    private final MailSender javaMailSender;


    @KafkaListener(topics = "user-created", groupId = "mail-service")
    public void sendEmailVerificationCode(String email, Acknowledgment acknowledgment) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(email);
        msg.setSubject("Email verification code");

        javaMailSender.send(msg);
    }
}
