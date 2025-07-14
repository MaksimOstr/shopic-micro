package com.userservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.shopic.grpc.codeservice.CreateCodeResponse;
import com.shopic.grpc.codeservice.ValidateCodeResponse;
import com.userservice.dto.request.ForgotPasswordRequest;
import com.userservice.entity.AuthProviderEnum;
import com.userservice.entity.User;
import com.userservice.exceptions.ResetPasswordException;
import com.userservice.projection.ResetPasswordProjection;
import com.userservice.services.grpc.GrpcCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class ForgotPasswordService {
    private final GrpcCodeService grpcCodeService;
    private final KafkaEventProducer kafkaEventProducer;
    private final PasswordService  passwordService;
    private final QueryUserService queryUserService;

    public void requestResetPassword(ForgotPasswordRequest dto) {
        ResetPasswordProjection user = queryUserService.getUserForResetPassword(dto.email());

        if(user.getAuthProvider() != AuthProviderEnum.LOCAL) {
            throw new ResetPasswordException("User is not a local user");
        }

        CreateCodeResponse response = grpcCodeService.getResetPasswordCode(user.getId());

        kafkaEventProducer.requestResetPassword(response.getCode(), dto.email());
    }

    @Transactional
    public void resetPassword(String newPassword, String code) {
        ValidateCodeResponse response = grpcCodeService.validateResetPasswordCode(code);
        User user = queryUserService.findById(response.getUserId());

        if(passwordService.comparePassword(user.getPassword(), newPassword)) {
            throw new ResetPasswordException("Password is the same");
        }

        String encodedPassword = passwordService.encode(newPassword);
        user.setPassword(encodedPassword);
    }
}
