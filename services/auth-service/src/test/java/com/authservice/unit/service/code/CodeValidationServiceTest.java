package com.authservice.unit.service.code;

import com.authservice.entity.Code;
import com.authservice.entity.CodeScopeEnum;
import com.authservice.entity.User;
import com.authservice.exceptions.CodeValidationException;
import com.authservice.exceptions.NotFoundException;
import com.authservice.repositories.CodeRepository;
import com.authservice.services.code.CodeValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CodeValidationServiceTest {

    @Mock
    private CodeRepository codeRepository;

    @InjectMocks
    private CodeValidationService codeValidationService;


    private static final String CODE = "testCode";
    private static final CodeScopeEnum EMAIL_CHANGE_CODE_SCOPE_ENUM = CodeScopeEnum.EMAIL_CHANGE;
    private static final String EMAIL = "test@gmail.com";


    private Code code;
    private User user;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .email(EMAIL)
                .build();

        code = Code.builder()
                .user(user)
                .used(false)
                .code(CODE)
                .build();
    }

    @Test
    public void testValidate_whenCalledWithValidCode_thenReturnCode() {
        code.setExpiresAt(Instant.now().plusSeconds(60));
        code.setScope(EMAIL_CHANGE_CODE_SCOPE_ENUM);

        when(codeRepository.findUnusedByCode(anyString(), any(CodeScopeEnum.class))).thenReturn(Optional.of(code));

        Code result = codeValidationService.validate(CODE, EMAIL_CHANGE_CODE_SCOPE_ENUM);

        verify(codeRepository).findUnusedByCode(CODE, EMAIL_CHANGE_CODE_SCOPE_ENUM);

        assertTrue(result.isUsed());
        assertEquals(CODE, result.getCode());
        assertEquals(user, result.getUser());
    }

    @Test
    public void testValidate_whenCalledWithInvalidCode_thenThrowException() {
        when(codeRepository.findUnusedByCode(anyString(), any(CodeScopeEnum.class))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            codeValidationService.validate(CODE, EMAIL_CHANGE_CODE_SCOPE_ENUM);
        });

        verify(codeRepository).findUnusedByCode(CODE, EMAIL_CHANGE_CODE_SCOPE_ENUM);

        assertFalse(code.isUsed());
    }

    @Test
    public void testValidate_whenCalledWithExpired_thenThrowException() {
        code.setExpiresAt(Instant.now().minusSeconds(60));

        when(codeRepository.findUnusedByCode(anyString(), any(CodeScopeEnum.class))).thenReturn(Optional.of(code));

        assertThrows(CodeValidationException.class, () -> {
            codeValidationService.validate(CODE, EMAIL_CHANGE_CODE_SCOPE_ENUM);
        });

        verify(codeRepository).findUnusedByCode(CODE, EMAIL_CHANGE_CODE_SCOPE_ENUM);
        assertFalse(code.isUsed());
    }
}
