package com.codeservice.service;

import com.codeservice.entity.Code;
import com.codeservice.enums.CodeScopeEnum;
import com.codeservice.exception.CodeValidationException;
import com.codeservice.exception.NotFoundException;
import com.codeservice.repository.CodeRepository;
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
    public long validate(String code, CodeScopeEnum scope) {
        return codeRepository.findByCode(code)
                .map(foundCode -> {
                    if(isCodeExpired(foundCode) || foundCode.getScope() != scope) {
                        log.error("Code validation failed: code is expired or scope is not valid");
                        throw new CodeValidationException(CODE_VALIDATION_FAILED);
                    }

                    deleteCode(code);
                    return foundCode.getUserId();
                })
                .orElseThrow(() -> new NotFoundException(CODE_VALIDATION_FAILED));
    }

    private void deleteCode(String code) {
        codeRepository.deleteCodeByCode(code);
    }

    private boolean isCodeExpired(Code code) {
        return code.getExpiresAt().isBefore(Instant.now());
    }
}
