package com.mailservice.services.grpc;


import com.shopic.grpc.codeservice.CodeScopeEnum;
import com.shopic.grpc.codeservice.CodeServiceGrpc;
import com.shopic.grpc.codeservice.CreateCodeRequest;
import com.shopic.grpc.codeservice.CreateCodeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CodeGrpcService {

    private final CodeServiceGrpc.CodeServiceBlockingStub codeServiceGrpc;

    public CreateCodeResponse getCode(CodeScopeEnum scope, long userId) {
        CreateCodeRequest request = CreateCodeRequest.newBuilder()
                .setScope(scope)
                .setUserId(userId)
                .build();

        return codeServiceGrpc.getCode(request);
    }
}
