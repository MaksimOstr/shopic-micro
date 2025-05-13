package com.codeservice.service;

import com.codeservice.entity.Code;
import com.codeservice.enums.CodeScopeEnum;
import com.codeservice.exception.CodeValidationException;
import com.codeservice.exception.EntityDoesNotExistException;
import com.codeservice.repository.CodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CodeValidationService {
    private final CodeRepository codeRepository;

    @Transactional
    public long validate(String code, CodeScopeEnum scope) {
        return codeRepository.findByCode(code)
                .map(foundCode -> {
                    if(isCodeExpired(foundCode) || foundCode.getScope() != scope) {
                        throw new CodeValidationException("Code has expired");
                    }

                    deleteCode(code);
                    return foundCode.getId();
                })
                .orElseThrow(() -> new EntityDoesNotExistException("Code not found"));
    }

    private void deleteCode(String code) {
        codeRepository.deleteCodeByCode(code);
    }

    private boolean isCodeExpired(Code code) {
        return code.getExpiresAt().isBefore(Instant.now());
    }
}
