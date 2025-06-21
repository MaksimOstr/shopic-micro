package com.userservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.shopic.grpc.codeservice.CodeServiceGrpc;
import com.shopic.grpc.codeservice.CreateCodeResponse;
import com.shopic.grpc.codeservice.ValidateCodeResponse;
import com.userservice.dto.request.ResetPasswordRequest;
import com.userservice.entity.AuthProviderEnum;
import com.userservice.entity.User;
import com.userservice.exceptions.NotFoundException;
import com.userservice.exceptions.ResetPasswordException;
import com.userservice.projection.ResetPasswordProjection;
import com.userservice.repositories.UserRepository;
import com.userservice.services.grpc.GrpcCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserPasswordService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final GrpcCodeService grpcCodeService;
    private final KafkaEventProducer kafkaEventProducer;

    public String encode(String password) {
        return passwordEncoder.encode(password);
    }

    public void requestResetPassword(ResetPasswordRequest dto) throws JsonProcessingException {
        ResetPasswordProjection user = getUserForResetPassword(dto.email());

        if(user.getAuthProvider() != AuthProviderEnum.LOCAL) {
            throw new ResetPasswordException("User is not a local user");
        }

        CreateCodeResponse response = grpcCodeService.getResetPasswordCode(user.getId());

        kafkaEventProducer.requestResetPassword(response.getCode(), dto.email());
    }

    @Transactional
    public void resetPassword(String newPassword, String code) {
        ValidateCodeResponse response = grpcCodeService.validateResetPasswordCode(code);
        User user = userRepository.findById(response.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        if(comparePassword(user.getPassword(), newPassword)) {
            throw new ResetPasswordException("Password is the same");
        }

        user.setPassword(encode(newPassword));
    }

    private ResetPasswordProjection getUserForResetPassword(String email) {
        return userRepository.findUserForResetPassword(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private boolean comparePassword(String oldPassword, String newPassword) {
        return passwordEncoder.matches(newPassword, oldPassword);
    }
}
