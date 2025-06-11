package com.userservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopic.grpc.codeservice.CodeScopeEnum;
import com.shopic.grpc.codeservice.CodeServiceGrpc;
import com.shopic.grpc.codeservice.ValidateCodeRequest;
import com.shopic.grpc.codeservice.ValidateCodeResponse;
import com.userservice.exceptions.CodeVerificationException;
import com.userservice.exceptions.EmailVerifyException;
import com.userservice.exceptions.NotFoundException;
import com.userservice.projection.EmailVerifyProjection;
import com.userservice.repositories.UserRepository;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserVerificationService {
    private final UserRepository userRepository;
    private final CodeServiceGrpc.CodeServiceBlockingStub codeServiceBlockingStub;
    private final KafkaEventProducer kafkaEventProducer;

    @Transactional
    public void requestVerifyEmail(String email) throws JsonProcessingException {
        EmailVerifyProjection user = getUserForEmailVerify(email);

        if(user.getVerified()) {
            log.error("User already verified");
            throw new EmailVerifyException("Email verification request failed");
        }

        kafkaEventProducer.requestEmailVerification(user.getId(), email);
    }

    public void verifyUser(String code) {
        try {
            ValidateCodeRequest request = ValidateCodeRequest.newBuilder()
                    .setCode(code)
                    .setScope(CodeScopeEnum.EMAIL_VERIFICATION)
                    .build();
            ValidateCodeResponse response = codeServiceBlockingStub.validateCode(request);
            log.info("Code verified successfully: {}", response.toString());
            markUserVerified(response.getUserId());
        } catch (StatusRuntimeException e) {
            log.error("Code verification failed: {}", e.getStatus().getDescription());
            throw new CodeVerificationException("Code verification failed");
        }
    }


    private void markUserVerified(long userId) {
        int updated = userRepository.markUserVerified(userId);

        if(updated == 0) {
            throw new NotFoundException("User not found");
        }
    }


    private EmailVerifyProjection getUserForEmailVerify(String email) {
        return userRepository.findUserForEmailVerify(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }
}
