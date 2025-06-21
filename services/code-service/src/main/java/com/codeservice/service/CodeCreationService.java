package com.codeservice.service;

import com.codeservice.entity.Code;
import com.codeservice.enums.CodeScopeEnum;
import com.codeservice.repository.CodeRepository;
import com.shopic.grpc.codeservice.CreateCodeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CodeCreationService {
    private final CodeRepository codeRepository;
    private final CodeGeneratorService codeGeneratorService;


    @Value("${CODE_EXPIRATION:900}")
    private int expiresIn;

    @Transactional
    public Code getCode(long userId, CodeScopeEnum scope) {
        return codeRepository.findByScopeAndUserId(scope, userId)
                .map(code -> {
                    String generatedCode = codeGeneratorService.generateAlphanumericCode();
                    code.setCode(generatedCode);
                    code.setExpiresAt(getExpirationTime());
                    return codeRepository.save(code);
                })
                .orElseGet(() -> createCode(userId, scope));
    }


    private Code createCode(long userId, CodeScopeEnum scope) {
        String generatedCode = codeGeneratorService.generateAlphanumericCode();
        Code code = Code.builder()
                .code(generatedCode)
                .expiresAt(getExpirationTime())
                .scope(scope)
                .userId(userId)
                .build();

        return codeRepository.save(code);
    }



    private Instant getExpirationTime() {
        return Instant.now().plusSeconds(expiresIn);
    }
}
