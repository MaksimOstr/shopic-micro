package com.codeservice.service;

import com.codeservice.entity.Code;
import com.codeservice.enums.CodeScopeEnum;
import com.shopic.grpc.codeservice.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class GrpcCodeService extends CodeServiceGrpc.CodeServiceImplBase {
    private final CodeCreationService codeCreationServiceService;
    private final CodeValidationService codeValidationService;

    @Override
    public void getEmailVerificationCode(CreateCodeRequest request, StreamObserver<CreateCodeResponse> responseObserver) {
        log.info("CreateCode method was called: {}", request.getUserId());

        Code createdCode = codeCreationServiceService.getCode(request.getUserId(), CodeScopeEnum.EMAIL_VERIFICATION);

        CreateCodeResponse response = CreateCodeResponse.newBuilder()
                .setCode(createdCode.getCode())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }

    @Override
    public void validateEmailCode(ValidateCodeRequest request, StreamObserver<ValidateCodeResponse> responseObserver) {
        long userId = codeValidationService.validate(request.getCode(), CodeScopeEnum.EMAIL_VERIFICATION);

        ValidateCodeResponse response = ValidateCodeResponse.newBuilder()
                .setUserId(userId)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
