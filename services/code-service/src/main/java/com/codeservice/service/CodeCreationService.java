package com.codeservice.service;

import com.codeservice.entity.Code;
import com.codeservice.entity.CodeScopeEnum;
import com.codeservice.exception.InternalException;
import com.codeservice.repository.CodeRepository;
import io.github.resilience4j.retry.MaxRetriesExceededException;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class CodeCreationService {
    private final CodeRepository codeRepository;
    private final CodeGeneratorService codeGeneratorService;


    @Value("${CODE_EXPIRATION:900}")
    private int expiresIn;

    @Retry(name = "codeGenerationRetry")
    @Transactional
    public Code getCode(long userId, CodeScopeEnum scope) {
        try {
            return codeRepository.findByScopeAndUserId(scope, userId)
                    .map(this::prepareCode)
                    .orElseGet(() -> createCode(userId, scope));
        } catch (MaxRetriesExceededException e) {
            log.error("Max retries exceeded", e);
            throw new InternalException("Code generation failed.");
        }
    }

    private Code prepareCode(Code code) {
        String generatedCode = codeGeneratorService.generateAlphanumericCode();
        code.setCode(generatedCode);
        code.setExpiresAt(getExpirationTime());
        return codeRepository.save(code);
    }

    private Code createCode(long userId, CodeScopeEnum scope) {
        Code code = Code.builder()
                .scope(scope)
                .userId(userId)
                .build();

        return prepareCode(code);
    }

    private Instant getExpirationTime() {
        return Instant.now().plusSeconds(expiresIn);
    }
}
