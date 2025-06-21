package com.userservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopic.grpc.codeservice.*;
import com.userservice.exceptions.CodeVerificationException;
import com.userservice.exceptions.EmailVerifyException;
import com.userservice.exceptions.NotFoundException;
import com.userservice.projection.EmailVerifyProjection;
import com.userservice.repositories.UserRepository;
import com.userservice.services.grpc.GrpcCodeService;
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
    private final GrpcCodeService grpcCodeService;
    private final KafkaEventProducer kafkaEventProducer;

    @Transactional
    public void requestVerifyEmail(String email) throws JsonProcessingException {
        EmailVerifyProjection user = getUserForEmailVerify(email);

        if (user.getVerified()) {
            log.error("User already verified");
            throw new EmailVerifyException("Email verification request failed");
        }

        CreateCodeResponse response = grpcCodeService.getCode(CodeScopeEnum.EMAIL_VERIFICATION, user.getId());

        kafkaEventProducer.requestEmailVerification(response.getCode(), email);
    }

    public void verifyUser(String code) {
        ValidateCodeResponse response = grpcCodeService.validateCode(code);
        markUserVerified(response.getUserId());
    }


    private void markUserVerified(long userId) {
        int updated = userRepository.markUserVerified(userId);

        if (updated == 0) {
            throw new NotFoundException("User not found");
        }
    }


    private EmailVerifyProjection getUserForEmailVerify(String email) {
        return userRepository.findUserForEmailVerify(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }
}
