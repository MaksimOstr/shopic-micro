package com.authservice.services.grpc;

import com.authservice.exceptions.InternalServiceException;
import com.shopic.grpc.codeservice.CodeServiceGrpc;
import com.shopic.grpc.codeservice.CreateCodeRequest;
import com.shopic.grpc.codeservice.CreateCodeResponse;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import org.springframework.grpc.server.service.GrpcService;


@GrpcService
@RequiredArgsConstructor
public class CodeGrpcService {
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
}
