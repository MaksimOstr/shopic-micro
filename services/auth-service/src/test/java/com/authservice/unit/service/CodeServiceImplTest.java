package com.authservice.unit.service;

import com.authservice.entity.Code;
import com.authservice.entity.CodeScopeEnum;
import com.authservice.entity.User;
import com.authservice.exception.ApiException;
import com.authservice.repositories.CodeRepository;
import com.authservice.services.impl.CodeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CodeServiceImplTest {

    @Mock
    private CodeRepository codeRepository;

    @InjectMocks
    private CodeServiceImpl codeService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(UUID.randomUUID())
                .build();
        ReflectionTestUtils.setField(codeService, "expiresAt", 120);
    }

    @Test
    void create_shouldUpdateExistingCodeIfPresent() {
        Code existing = Code.builder()
                .code("OLD1234")
                .scope(CodeScopeEnum.EMAIL_VERIFICATION)
                .user(user)
                .expiresAt(Instant.now().plusSeconds(60))
                .build();

        when(codeRepository.findByUserAndScope(user, CodeScopeEnum.EMAIL_VERIFICATION))
                .thenReturn(Optional.of(existing));
        when(codeRepository.save(any(Code.class))).thenReturn(existing);

        Instant before = Instant.now();
        Code result = codeService.create(user, CodeScopeEnum.EMAIL_VERIFICATION);


        assertEquals(user, result.getUser());
        assertEquals(CodeScopeEnum.EMAIL_VERIFICATION, result.getScope());
        assertEquals(8, result.getCode().length());
        assertTrue(result.getExpiresAt().isAfter(before));
    }

    @Test
    void create_shouldCreateNewCodeIfNoneExists() {
        when(codeRepository.findByUserAndScope(user, CodeScopeEnum.EMAIL_VERIFICATION))
                .thenReturn(Optional.empty());
        when(codeRepository.save(any(Code.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Instant before = Instant.now();
        Code result = codeService.create(user, CodeScopeEnum.EMAIL_VERIFICATION);

        assertEquals(user, result.getUser());
        assertEquals(CodeScopeEnum.EMAIL_VERIFICATION, result.getScope());
        assertEquals(8, result.getCode().length());
        assertTrue(result.getExpiresAt().isAfter(before));
    }


    @Test
    void validate_shouldDeleteCodeAndReturnWhenValid() {
        Code stored = Code.builder()
                .code("ABCDEFGH")
                .scope(CodeScopeEnum.EMAIL_VERIFICATION)
                .expiresAt(Instant.now().plusSeconds(30))
                .build();
        when(codeRepository.findByCodeAndScope(stored.getCode(), CodeScopeEnum.EMAIL_VERIFICATION)).thenReturn(Optional.of(stored));

        Code result = codeService.validate(stored.getCode(), CodeScopeEnum.EMAIL_VERIFICATION);

        verify(codeRepository).findByCodeAndScope(stored.getCode(), CodeScopeEnum.EMAIL_VERIFICATION);
        verify(codeRepository).delete(stored);
        assertEquals(stored, result);
    }

    @Test
    void validate_shouldThrowWhenCodeExpired() {
        Code stored = Code.builder()
                .code("EXPIRED")
                .scope(CodeScopeEnum.EMAIL_VERIFICATION)
                .expiresAt(Instant.now().minusSeconds(5))
                .build();
        when(codeRepository.findByCodeAndScope(stored.getCode(), CodeScopeEnum.EMAIL_VERIFICATION)).thenReturn(Optional.of(stored));

        ApiException exception = assertThrows(ApiException.class, () -> codeService.validate(stored.getCode(), CodeScopeEnum.EMAIL_VERIFICATION));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        verify(codeRepository, never()).delete(any(Code.class));
    }

    @Test
    void validate_shouldThrowWhenCodeNotFound() {
        when(codeRepository.findByCodeAndScope("missing", CodeScopeEnum.EMAIL_VERIFICATION)).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> codeService.validate("missing", CodeScopeEnum.EMAIL_VERIFICATION));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        verify(codeRepository, never()).delete(any(Code.class));
    }
}
