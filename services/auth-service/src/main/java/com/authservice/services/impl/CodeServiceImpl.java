package com.authservice.services.impl;

import com.authservice.entity.Code;
import com.authservice.entity.CodeScopeEnum;
import com.authservice.entity.User;
import com.authservice.exception.ApiException;
import com.authservice.repositories.CodeRepository;
import com.authservice.services.CodeService;
import io.github.resilience4j.retry.MaxRetriesExceededException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;

import static com.authservice.utils.CodeUtils.generateAlphanumericCode;

@Service
@Slf4j
@RequiredArgsConstructor
public class CodeServiceImpl implements CodeService {
    private final CodeRepository codeRepository;

    @Value("${verification-code.expires-at}")
    private int expiresAt;

    @Retryable(retryFor = DataIntegrityViolationException.class, backoff = @Backoff(delay = 100))
    public Code create(User user, CodeScopeEnum scope) {
        try {
            log.info("Creating code for user {} with scope {}", user, scope);
            return codeRepository.findByUserAndScope(user, scope)
                    .map((this::update))
                    .orElseGet(() -> createAndSave(user, scope));
        } catch (MaxRetriesExceededException e) {
            log.error("Max retries exceeded", e);
            throw new ApiException("Code generation failed.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public Code validate(String code, CodeScopeEnum scope) {
        return codeRepository.findByCodeAndScope(code, scope)
                .map(foundCode -> {
                    if(isCodeExpired(foundCode)) {
                        log.error("Code validation failed: code is expired");
                        throw new ApiException("Code validation failed", HttpStatus.BAD_REQUEST);
                    }

                    codeRepository.delete(foundCode);

                    return foundCode;
                })
                .orElseThrow(() -> new ApiException("Code validation failed", HttpStatus.BAD_REQUEST));
    }



    private boolean isCodeExpired(Code code) {
        return code.getExpiresAt().isBefore(Instant.now());
    }

    private Code update(Code code) {
        String generatedCode = generateAlphanumericCode();
        code.setExpiresAt(Instant.now().plusSeconds(expiresAt));
        code.setCode(generatedCode);

        return codeRepository.save(code);
    }

    private Code createAndSave(User user, CodeScopeEnum scope) {
        String generatedCode = generateAlphanumericCode();
        Code code = Code.builder()
                .code(generatedCode)
                .scope(scope)
                .user(user)
                .expiresAt(Instant.now().plusSeconds(expiresAt))
                .build();

        return codeRepository.save(code);
    }

    @Scheduled(fixedDelay = 1000 * 60 * 5)
    public void clearExpiredCodes() {
        log.info("Clearing expired codes");
        codeRepository.deleteExpiredCodes();
    }
}
