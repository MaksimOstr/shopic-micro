package com.userservice.services.grpc;

import com.shopic.grpc.codeservice.*;
import com.userservice.exceptions.CodeVerificationException;
import com.userservice.exceptions.InternalServiceException;
import com.userservice.exceptions.NotFoundException;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;


@GrpcService
@Slf4j
@RequiredArgsConstructor
public class GrpcCodeService {
    private final CodeServiceGrpc.CodeServiceBlockingStub codeGrpcService;

    private static final String INTERNAL_SERVICE_ERROR = "Internal code service error";

    public CreateCodeResponse getEmailVerificationCode(long userId) {
        CreateCodeRequest request = CreateCodeRequest.newBuilder()
                .setUserId(userId)
                .build();
        try {
            return codeGrpcService.getEmailVerificationCode(request);
        } catch (StatusRuntimeException e) {
            switch (e.getStatus().getCode()) {
                case ALREADY_EXISTS -> throw new InternalServiceException(INTERNAL_SERVICE_ERROR);
                default -> throw e;
            }
        }
    }

    public ValidateCodeResponse validateEmailCode(String code) {
        ValidateCodeRequest request = ValidateCodeRequest.newBuilder()
                .setCode(code)
                .build();
        try {
            return codeGrpcService.validateEmailCode(request);
        } catch (StatusRuntimeException e) {
            log.error("gRpc code service{}", e.getMessage());
            switch (e.getStatus().getCode()) {
                case NOT_FOUND -> throw new NotFoundException(e.getStatus().getDescription());
                case INVALID_ARGUMENT -> throw new CodeVerificationException(e.getStatus().getDescription());
                case INTERNAL -> throw new InternalServiceException(INTERNAL_SERVICE_ERROR);
                default -> throw new CodeVerificationException("Unexpected error from code service" + e.getMessage());
            }
        }
    }
}
