package com.authservice.services.code;

import com.authservice.entity.Code;
import com.authservice.entity.CodeScopeEnum;
import com.authservice.entity.User;
import com.authservice.exceptions.InternalServiceException;
import com.authservice.repositories.CodeRepository;
import io.github.resilience4j.retry.MaxRetriesExceededException;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class CodeCreationService {
    private final CodeRepository codeRepository;

    @Value("${CODE_EXPIRATION:900}")
    private int expiresIn;

    private final static byte CODE_LENGTH = 8;
    private final static String chars = "0123456789" +
            "ABCDEFGHJKLMNPQRSTUVWXYZ";
    private final SecureRandom random = new SecureRandom();


    @Retry(name = "codeGenerationRetry")
    @Transactional
    public Code getCode(User user, CodeScopeEnum scope) {
        try {
            return codeRepository.findByScopeAndUserId(scope, user.getId())
                    .map(this::prepareCode)
                    .orElseGet(() -> createCode(user, scope));
        } catch (MaxRetriesExceededException e) {
            log.error("Max retries exceeded", e);
            throw new InternalServiceException("Code generation failed.");
        }
    }


    private Code prepareCode(Code code) {
        String generatedCode = generateAlphanumericCode();
        code.setCode(generatedCode);
        code.setExpiresAt(getExpirationTime());
        return codeRepository.save(code);
    }

    private Code createCode(User user, CodeScopeEnum scope) {
        Code code = Code.builder()
                .scope(scope)
                .used(false)
                .user(user)
                .build();

        return prepareCode(code);
    }

    private Instant getExpirationTime() {
        return Instant.now().plusSeconds(expiresIn);
    }

    private String generateAlphanumericCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);

        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = random.nextInt(chars.length());
            code.append(chars.charAt(index));
        }

        return code.toString();
    }
}
