package com.authservice.services.user;

import com.authservice.dto.request.ForgotPasswordRequest;
import com.authservice.entity.AuthProviderEnum;
import com.authservice.entity.User;
import com.authservice.exceptions.ResetPasswordException;
import com.authservice.projection.user.ResetPasswordProjection;
import com.authservice.services.KafkaEventProducer;
import com.authservice.services.grpc.GrpcCodeService;
import com.shopic.grpc.codeservice.CreateCodeResponse;
import com.shopic.grpc.codeservice.ValidateCodeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ForgotPasswordService {
    private final GrpcCodeService grpcCodeService;
    private final KafkaEventProducer kafkaEventProducer;
    private final PasswordService  passwordService;
    private final UserQueryService userQueryService;

    public void requestResetPassword(ForgotPasswordRequest dto) {
        ResetPasswordProjection user = userQueryService.getUserForResetPassword(dto.email());

        if(user.getAuthProvider() != AuthProviderEnum.LOCAL) {
            throw new ResetPasswordException("User is not a local user");
        }

        CreateCodeResponse response = grpcCodeService.getResetPasswordCode(user.getId());

        kafkaEventProducer.requestResetPassword(response.getCode(), dto.email());
    }

    @Transactional
    public void resetPassword(String newPassword, String code) {
        ValidateCodeResponse response = grpcCodeService.validateResetPasswordCode(code);
        User user = userQueryService.findById(response.getUserId());

        if(passwordService.comparePassword(user.getPassword(), newPassword)) {
            throw new ResetPasswordException("Password is the same");
        }

        String encodedPassword = passwordService.encode(newPassword);
        user.setPassword(encodedPassword);
    }
}
