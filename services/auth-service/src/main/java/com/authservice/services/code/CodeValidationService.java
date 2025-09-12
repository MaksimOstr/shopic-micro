package com.authservice.services.code;

import com.authservice.entity.Code;
import com.authservice.entity.CodeScopeEnum;
import com.authservice.exceptions.CodeValidationException;
import com.authservice.exceptions.NotFoundException;
import com.authservice.repositories.CodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class CodeValidationService {
    private final CodeRepository codeRepository;

    private static final String CODE_VALIDATION_FAILED = "Code validation failed";


    @Transactional
    public Code validate(String code, CodeScopeEnum scope) {
        return codeRepository.findUnusedByCode(code)
                .map(foundCode -> {
                    if(isCodeExpired(foundCode) || foundCode.getScope() != scope) {
                        log.error("Code validation failed: code is expired or scope is not valid");
                        throw new CodeValidationException(CODE_VALIDATION_FAILED);
                    }

                    foundCode.setUsed(true);

                    return foundCode;
                })
                .orElseThrow(() -> new NotFoundException(CODE_VALIDATION_FAILED));
    }


    private boolean isCodeExpired(Code code) {
        return code.getExpiresAt().isBefore(Instant.now());
    }
}
