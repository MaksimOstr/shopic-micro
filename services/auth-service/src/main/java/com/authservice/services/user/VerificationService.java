package com.authservice.services.user;

import com.authservice.exceptions.EmailVerifyException;
import com.authservice.exceptions.NotFoundException;
import com.authservice.projection.user.EmailVerifyProjection;
import com.authservice.repositories.UserRepository;
import com.authservice.services.KafkaEventProducer;
import com.authservice.services.grpc.GrpcCodeService;
import com.shopic.grpc.codeservice.CreateCodeResponse;
import com.shopic.grpc.codeservice.ValidateCodeResponse;
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
    private final UserQueryService userQueryService;

    @Transactional
    public void requestVerifyEmail(String email) {
        EmailVerifyProjection user = userQueryService.getUserForEmailVerify(email);

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
