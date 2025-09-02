package com.authservice.services.grpc;

import com.authservice.exceptions.CodeVerificationException;
import com.authservice.exceptions.ExternalServiceUnavailableException;
import com.authservice.exceptions.InternalServiceException;
import com.authservice.exceptions.NotFoundException;
import com.shopic.grpc.codeservice.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;

import static io.grpc.Status.Code.*;


@Slf4j
@GrpcService
@RequiredArgsConstructor
public class GrpcCodeService {
    private final CodeServiceGrpc.CodeServiceBlockingStub codeGrpcService;

    private static final String INTERNAL_SERVICE_ERROR = "Internal code service error";


    @CircuitBreaker(name = "code-service", fallbackMethod = "getCodeFallbackMethod")
    public CreateCodeResponse getEmailVerificationCode(long userId) {
        return codeGrpcService.getEmailVerificationCode(getCreateCodeRequest(userId));
    }

    @CircuitBreaker(name = "code-service", fallbackMethod = "getCodeFallbackMethod")
    public CreateCodeResponse getResetPasswordCode(long userId) {
        return codeGrpcService.getResetPasswordCode(getCreateCodeRequest(userId));
    }

    @CircuitBreaker(name = "code-service", fallbackMethod = "getCodeFallbackMethod")
    public CreateCodeResponse getEmailChangeCode(long userId) {
        return codeGrpcService.getEmailChangeCode(getCreateCodeRequest(userId));
    }

    @CircuitBreaker(name = "code-service", fallbackMethod = "validateCodeFallbackMethod")
    public ValidateCodeResponse validateEmailCode(String code) {
        return codeGrpcService.validateEmailCode(getValidateCodeRequest(code));
    }

    @CircuitBreaker(name = "code-service", fallbackMethod = "validateCodeFallbackMethod")
    public ValidateCodeResponse validateResetPasswordCode(String code) {
        return codeGrpcService.validateResetPasswordCode(getValidateCodeRequest(code));
    }

    @CircuitBreaker(name = "code-service", fallbackMethod = "validateCodeFallbackMethod")
    public ValidateCodeResponse validateEmailChangeCode(String code) {
        return codeGrpcService.validateEmailChangeCode(getValidateCodeRequest(code));
    }

    public CreateCodeResponse getCodeFallbackMethod(long userId, Throwable e) {
        log.error("getCodeFallbackMethod error = {}", e.getClass());

        if(e instanceof StatusRuntimeException statusRuntimeException){
            String description = statusRuntimeException.getStatus().getDescription();

            switch (statusRuntimeException.getStatus().getCode()) {
                case INTERNAL -> throw new InternalServiceException(description);
                default -> throw statusRuntimeException;
            }
        } else {
            throw new ExternalServiceUnavailableException("Something went wrong. Try again later");
        }
    }

    public ValidateCodeResponse validateCodeFallbackMethod(String code, Throwable e) {
        log.error("validateCodeFallbackMethod error = {}", e.getClass());

        if(e instanceof StatusRuntimeException statusRuntimeException) {
            String description = statusRuntimeException.getStatus().getDescription();

            switch (statusRuntimeException.getStatus().getCode()) {
                case NOT_FOUND -> throw new NotFoundException(description);
                case INVALID_ARGUMENT -> throw new CodeVerificationException(description);
                case INTERNAL -> throw new InternalServiceException(INTERNAL_SERVICE_ERROR);
                default -> throw statusRuntimeException;
            }
        } else {
            throw new ExternalServiceUnavailableException("Something went wrong. Try again later");
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
