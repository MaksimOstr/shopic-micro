package com.userservice.services.grpc;

import com.shopic.grpc.codeservice.*;
import com.userservice.exceptions.CodeVerificationException;
import com.userservice.exceptions.InternalServiceException;
import io.grpc.StatusRuntimeException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GrpcCodeService {
    private final CodeServiceGrpc.CodeServiceBlockingStub codeGrpcService;


    public CreateCodeResponse getCode(CodeScopeEnum scope, long userId) {
        CreateCodeRequest request = CreateCodeRequest.newBuilder()
                .setScope(scope)
                .setUserId(userId)
                .build();

        return codeGrpcService.getCode(request);
    }

    public ValidateCodeResponse validateCode(String code) {
        ValidateCodeRequest request = ValidateCodeRequest.newBuilder()
                .setCode(code)
                .setScope(CodeScopeEnum.EMAIL_VERIFICATION)
                .build();
        try {
            return codeGrpcService.validateCode(request);
        } catch (StatusRuntimeException e) {
            log.error("gRpc code service{}", e.getMessage());
            switch (e.getStatus().getCode()) {
                case NOT_FOUND -> throw new NotFoundException(e.getStatus().getDescription());
                case INVALID_ARGUMENT -> throw new CodeVerificationException(e.getStatus().getDescription());
                case INTERNAL -> throw new InternalServiceException("Internal code service error");
                default -> throw new CodeVerificationException("Unexpected error from code service" + e.getMessage());
            }
        }
    }
}
