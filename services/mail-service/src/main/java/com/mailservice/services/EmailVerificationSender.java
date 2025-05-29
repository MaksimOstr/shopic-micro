package com.mailservice.services;

import com.mailservice.services.grpc.CodeGrpcService;
import com.shopic.grpc.codeservice.CodeScopeEnum;
import com.shopic.grpc.codeservice.CreateCodeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailVerificationSender {

    private final MailService mailService;
    private final CodeGrpcService codeGrpcService;

    public void sendEmailVerificationEmail(String email, long userId) {
        CreateCodeResponse verificationCode = codeGrpcService.getCode(CodeScopeEnum.EMAIL_VERIFICATION,  userId);
        String subject = "Email verification";
        String text = "Your verification code is: " + verificationCode.getCode();

        mailService.send(email, subject, text);
    }
}
