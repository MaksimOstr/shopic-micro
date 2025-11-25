package com.authservice.services.impl;

import com.authservice.entity.Code;
import com.authservice.entity.CodeScopeEnum;
import com.authservice.entity.User;
import com.authservice.exceptions.CodeValidationException;
import com.authservice.exceptions.InternalServiceException;
import com.authservice.exceptions.NotFoundException;
import com.authservice.repositories.CodeRepository;
import com.authservice.services.CodeService;
import io.github.resilience4j.retry.MaxRetriesExceededException;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
public class CodeServiceImpl implements CodeService {
    private final CodeRepository codeRepository;

    @Value("${CODE_EXPIRATION:900}")
    private int expiresIn;

    private final static byte CODE_LENGTH = 8;
    private final static String chars = "0123456789" +
            "ABCDEFGHJKLMNPQRSTUVWXYZ";
    private final SecureRandom random = new SecureRandom();


    @Retry(name = "codeGenerationRetry")
    @Transactional
    public Code create(User user, CodeScopeEnum scope) {
        try {
            codeRepository.deleteByUser_IdAndScope(user.getId(), scope);

            Code newCode = createCode(user, scope);

            return codeRepository.save(newCode);
        } catch (MaxRetriesExceededException e) {
            log.error("Max retries exceeded", e);
            throw new InternalServiceException("Code generation failed.");
        }
    }

    @Transactional
    public Code validate(String code, CodeScopeEnum scope) {
        return codeRepository.findByCodeAndScope(code, scope)
                .map(foundCode -> {
                    if(isCodeExpired(foundCode)) {
                        log.error("Code validation failed: code is expired");
                        throw new CodeValidationException("Code validation failed");
                    }

                    codeRepository.delete(foundCode);

                    return foundCode;
                })
                .orElseThrow(() -> new NotFoundException("Code validation failed"));
    }



    private boolean isCodeExpired(Code code) {
        return code.getExpiresAt().isBefore(Instant.now());
    }

    private Code createCode(User user, CodeScopeEnum scope) {
        String generatedCode = generateAlphanumericCode();
        Code code = Code.builder()
                .code(generatedCode)
                .scope(scope)
                .user(user)
                .expiresAt(Instant.now().plusSeconds(expiresIn))
                .build();

        return code;
    }

    private String generateAlphanumericCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);

        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = random.nextInt(chars.length());
            code.append(chars.charAt(index));
        }

        return code.toString();
    }

    @Scheduled(fixedDelay = 900 * 1000)
    public void clearExpiredCodes() {
        log.info("Clearing expired codes");
        codeRepository.deleteExpiredCodes();
    }
}
