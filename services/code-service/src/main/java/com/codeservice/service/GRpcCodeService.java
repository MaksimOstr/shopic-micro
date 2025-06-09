package com.codeservice.service;

import com.codeservice.entity.Code;
import com.codeservice.enums.CodeScopeEnum;
import com.shopic.grpc.codeservice.*;
import io.grpc.stub.StreamObserver;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class GRpcCodeService extends CodeServiceGrpc.CodeServiceImplBase {
    private final CodeCreationService codeCreationServiceService;
    private final CodeValidationService codeValidationService;

    @Override
    public void getCode(CreateCodeRequest request, StreamObserver<CreateCodeResponse> responseObserver) {
        log.info("CreateCode method was called: {}, {}", request.getUserId(), request.getScope());

        CodeScopeEnum scope = CodeScopeEnum.valueOf(request.getScope().name());
        Code createdCode = codeCreationServiceService.getCode(request.getUserId(), scope);

        CreateCodeResponse response = CreateCodeResponse.newBuilder()
                .setCode(createdCode.getCode())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void validateCode(ValidateCodeRequest request, StreamObserver<ValidateCodeResponse> responseObserver) {
        CodeScopeEnum scope = CodeScopeEnum.valueOf(request.getScope().name());
        long userId = codeValidationService.validate(request.getCode(), scope);

        ValidateCodeResponse response = ValidateCodeResponse.newBuilder()
                .setUserId(userId)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
