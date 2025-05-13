package com.codeservice.service;

import com.codeservice.entity.Code;
import com.codeservice.enums.CodeScopeEnum;
import com.codeservice.repository.CodeRepository;
import com.shopic.grpc.codeservice.CodeServiceGrpc;
import com.shopic.grpc.codeservice.CreateCodeRequest;
import com.shopic.grpc.codeservice.CreateCodeResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.grpc.server.service.GrpcService;

import java.time.Instant;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class GRpcCodeService extends CodeServiceGrpc.CodeServiceImplBase {
    private final CodeRepository codeRepository;
    private final CodeGeneratorService codeGeneratorService;

    @Value("${CODE_EXPIRATION:900}")
    private int expiresIn;

    @Override
    public void createCode(CreateCodeRequest request, StreamObserver<CreateCodeResponse> responseObserver) {
        log.info("CreateCode method was called: {}, {}", request.getUserId(), request.getScope());
        String generatedCode = codeGeneratorService.generateAlphanumericCode();
        CodeScopeEnum scope = CodeScopeEnum.valueOf(request.getScope().name());

        Code code = new Code(
                generatedCode,
                getExpirationTime(),
                scope,
                request.getUserId()
        );

        Code savedCode = codeRepository.save(code);

        CreateCodeResponse response = CreateCodeResponse.newBuilder()
                .setCode(savedCode.getCode())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }


    private Instant getExpirationTime() {
        return Instant.now().plusSeconds(expiresIn);
    }
}
