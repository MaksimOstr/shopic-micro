package com.authservice.unit.service.code;

import com.authservice.entity.Code;
import com.authservice.entity.CodeScopeEnum;
import com.authservice.entity.User;
import com.authservice.exceptions.InternalServiceException;
import com.authservice.repositories.CodeRepository;
import com.authservice.services.code.CodeCreationService;
import io.github.resilience4j.retry.MaxRetriesExceededException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CodeCreationServiceTest {
    @Mock
    private CodeRepository codeRepository;

    @InjectMocks
    private CodeCreationService codeCreationService;


    private static final long USER_ID = 1L;
    private static final String CODE = "testCode";
    private static final CodeScopeEnum EMAIL_CHANGE_CODE_SCOPE_ENUM = CodeScopeEnum.EMAIL_CHANGE;


    private User user;
    private Code code;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .id(USER_ID)
                .build();

        code = Code.builder()
                .code(CODE)
                .scope(EMAIL_CHANGE_CODE_SCOPE_ENUM)
                .user(user)
                .build();
    }


    @Test
    public void testGetCode_whenCalledWithExistingCode_thenPrepareAndReturn() {
        ArgumentCaptor<Code> codeArgumentCaptor = ArgumentCaptor.forClass(Code.class);

        when(codeRepository.findByScopeAndUserId(any(CodeScopeEnum.class), anyLong())).thenReturn(Optional.of(code));
        when(codeRepository.save(any(Code.class))).thenReturn(code);

        Code result = codeCreationService.getCode(user, EMAIL_CHANGE_CODE_SCOPE_ENUM);

        verify(codeRepository).findByScopeAndUserId(EMAIL_CHANGE_CODE_SCOPE_ENUM, USER_ID);
        verify(codeRepository).save(codeArgumentCaptor.capture());

        Code savedCode = codeArgumentCaptor.getValue();

        assertNotEquals(CODE, savedCode.getCode());
        assertNotNull(savedCode.getExpiresAt());
        assertEquals(user, savedCode.getUser());
        assertEquals(EMAIL_CHANGE_CODE_SCOPE_ENUM, savedCode.getScope());
        assertFalse(savedCode.isUsed());
        assertEquals(code, result);
    }

    @Test
    public void testGetCode_whenCalledWithNonExistingCode_thenCreateNewCode() {
        ArgumentCaptor<Code> codeArgumentCaptor = ArgumentCaptor.forClass(Code.class);

        when(codeRepository.findByScopeAndUserId(any(CodeScopeEnum.class), anyLong())).thenReturn(Optional.empty());
        when(codeRepository.save(any(Code.class))).thenReturn(code);

        Code result = codeCreationService.getCode(user, EMAIL_CHANGE_CODE_SCOPE_ENUM);

        verify(codeRepository).findByScopeAndUserId(EMAIL_CHANGE_CODE_SCOPE_ENUM, USER_ID);
        verify(codeRepository).save(codeArgumentCaptor.capture());

        Code savedCode = codeArgumentCaptor.getValue();

        assertNotEquals(CODE, savedCode.getCode());
        assertNotNull(savedCode.getExpiresAt());
        assertEquals(user, savedCode.getUser());
        assertEquals(EMAIL_CHANGE_CODE_SCOPE_ENUM, savedCode.getScope());
        assertFalse(savedCode.isUsed());
        assertEquals(code, result);
    }

    @Test
    public void testGetCode_whenCouldNotGenerateUniqueCode_thenThrowException() {
        when(codeRepository.findByScopeAndUserId(any(CodeScopeEnum.class), anyLong())).thenThrow(MaxRetriesExceededException.class);

        assertThrows(InternalServiceException.class, () -> {
            codeCreationService.getCode(user, EMAIL_CHANGE_CODE_SCOPE_ENUM);
        });

        verify(codeRepository).findByScopeAndUserId(EMAIL_CHANGE_CODE_SCOPE_ENUM, USER_ID);
        verifyNoMoreInteractions(codeRepository);
    }
}
