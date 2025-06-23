package com.userservice.services.grpc;

import com.shopic.grpc.codeservice.*;
import com.userservice.exceptions.CodeVerificationException;
import com.userservice.exceptions.InternalServiceException;
import com.userservice.exceptions.NotFoundException;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;


@Slf4j
@GrpcService
@RequiredArgsConstructor
public class GrpcCodeService {
    private final CodeServiceGrpc.CodeServiceBlockingStub codeGrpcService;

    private static final String INTERNAL_SERVICE_ERROR = "Internal code service error";


    public CreateCodeResponse getEmailVerificationCode(long userId) {
        try {
            return codeGrpcService.getEmailVerificationCode(getCreateCodeRequest(userId));
        } catch (StatusRuntimeException e) {
            getCodeExceptionHandler(e);
            throw e;
        }
    }

    public CreateCodeResponse getResetPasswordCode(long userId) {
        try {
            return codeGrpcService.getResetPasswordCode(getCreateCodeRequest(userId));
        } catch (StatusRuntimeException e) {
            getCodeExceptionHandler(e);
            throw e;
        }
    }

    public ValidateCodeResponse validateEmailCode(String code) {
        try {
            return codeGrpcService.validateEmailCode(getValidateCodeRequest(code));
        } catch (StatusRuntimeException e) {
            codeVerifyExceptionHandler(e);
            throw e;
        }
    }

    public ValidateCodeResponse validateResetPasswordCode(String code) {
        try {
            return codeGrpcService.validateResetPasswordCode(getValidateCodeRequest(code));
        } catch (StatusRuntimeException e) {
            codeVerifyExceptionHandler(e);
            throw e;
        }
    }


    private void getCodeExceptionHandler(StatusRuntimeException e) {
        switch (e.getStatus().getCode()) {
            case ALREADY_EXISTS -> throw new InternalServiceException(INTERNAL_SERVICE_ERROR);
            default -> throw e;
        }
    }

    private void codeVerifyExceptionHandler(StatusRuntimeException e) {
        switch (e.getStatus().getCode()) {
            case NOT_FOUND -> throw new NotFoundException(e.getStatus().getDescription());
            case INVALID_ARGUMENT -> throw new CodeVerificationException(e.getStatus().getDescription());
            case INTERNAL -> throw new InternalServiceException(INTERNAL_SERVICE_ERROR);
            default -> throw new CodeVerificationException("Unexpected error from code service" + e.getMessage());
        }
    }


    private CreateCodeRequest getCreateCodeRequest(long userId) {
        return CreateCodeRequest.newBuilder()
                .setUserId(userId)
                .build();
    }

    private ValidateCodeRequest getValidateCodeRequest(String code) {
        return ValidateCodeRequest.newBuilder()
                .setCode(code)
                .build();
    }
}
