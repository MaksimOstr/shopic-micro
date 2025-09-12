package com.authservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.from}")
    private String from;

    @Async
    public void sendEmailVerificationCode(String email, String code) {
        String subject = "Email varification";
        String text = "Verification code:" + code;

        send(email, subject, text);
    }

    @Async
    public void sendEmailChange(String email, String code) {
        String subject = "Email change verification";
        String text = "Code for email changing:" + code;

        send(email, subject, text);
    }

    private void send(String to, String subject, String text) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(from);
            msg.setTo(to);
            msg.setText(text);
            msg.setSubject(subject);

            javaMailSender.send(msg);

            log.info("Email sent successfully to {}", to);
        } catch (MailException e) {
            log.error("Email sending failed {}", e.getMessage());
        }
    }
}