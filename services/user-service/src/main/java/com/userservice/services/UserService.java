package com.userservice.services;

import com.shopic.grpc.codeservice.CodeScopeEnum;
import com.shopic.grpc.codeservice.CodeServiceGrpc;
import com.shopic.grpc.codeservice.ValidateCodeRequest;
import com.shopic.grpc.codeservice.ValidateCodeResponse;
import com.userservice.exceptions.EntityDoesNotExistException;
import com.userservice.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final CodeServiceGrpc.CodeServiceBlockingStub codeServiceBlockingStub;


    public void verifyUser(String code) {
        ValidateCodeRequest request = ValidateCodeRequest.newBuilder()
                .setCode(code)
                .setScope(CodeScopeEnum.EMAIL_VERIFICATION)
                .build();

        ValidateCodeResponse response = codeServiceBlockingStub.validateCode(request);

        markUserVerified(response.getUserId());
    }


    private void markUserVerified(long userId) {
        int updated = userRepository.markUserVerified(userId);

        if(updated == 0) {
            throw new EntityDoesNotExistException("User not found");
        }
    }
}
