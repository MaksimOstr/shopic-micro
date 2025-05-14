package com.mailservice.services;

import com.shopic.grpc.codeservice.CodeScopeEnum;
import com.shopic.grpc.codeservice.CodeServiceGrpc;
import com.shopic.grpc.codeservice.CreateCodeRequest;
import com.shopic.grpc.codeservice.CreateCodeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VerificationCodeSender {
    private final CodeServiceGrpc.CodeServiceBlockingStub codeServiceGrpc;
    private final MailService mailService;


    public void sendVerificationCode(String email, long userId, CodeScopeEnum scope) {
        CreateCodeRequest request = CreateCodeRequest.newBuilder()
                .setScope(scope)
                .setUserId(userId)
                .build();

        CreateCodeResponse verificationCode = codeServiceGrpc.getCode(request);

        String text = "Your verification code is: " + verificationCode.getCode();
        String subject = "Email verification";

        mailService.send(email, subject, text);
    }
}
