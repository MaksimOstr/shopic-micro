package com.userservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.shopic.grpc.codeservice.*;
import com.userservice.exceptions.EmailVerifyException;
import com.userservice.exceptions.NotFoundException;
import com.userservice.projection.EmailVerifyProjection;
import com.userservice.repositories.UserRepository;
import com.userservice.services.grpc.GrpcCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationService {
    private final UserRepository userRepository;
    private final GrpcCodeService grpcCodeService;
    private final KafkaEventProducer kafkaEventProducer;
    private final QueryUserService queryUserService;

    @Transactional
    public void requestVerifyEmail(String email) throws JsonProcessingException {
        EmailVerifyProjection user = queryUserService.getUserForEmailVerify(email);

        if (user.getVerified()) {
            log.error("User already verified");
            throw new EmailVerifyException("Email verification request failed");
        }

        CreateCodeResponse response = grpcCodeService.getEmailVerificationCode(user.getId());

        kafkaEventProducer.requestEmailVerification(response.getCode(), email);
    }

    public void verifyUser(String code) {
        ValidateCodeResponse response = grpcCodeService.validateEmailCode(code);
        markUserVerified(response.getUserId());
    }


    private void markUserVerified(long userId) {
        int updated = userRepository.markUserVerified(userId);

        if (updated == 0) {
            throw new NotFoundException("User not found");
        }
    }
}
