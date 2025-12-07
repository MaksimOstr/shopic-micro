package com.authservice.unit.service;

import com.authservice.entity.Code;
import com.authservice.entity.CodeScopeEnum;
import com.authservice.entity.User;
import com.authservice.services.CodeService;
import com.authservice.services.MailService;
import com.authservice.services.UserService;
import com.authservice.services.VerificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerificationServiceTest {

    @Mock
    private MailService mailService;

    @Mock
    private UserService userService;

    @Mock
    private CodeService codeService;

    @InjectMocks
    private VerificationService verificationService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(UUID.randomUUID())
                .email("test@gmail.com")
                .isVerified(false)
                .build();
    }

    @Test
    void requestVerifyEmail_shouldSendCodeWhenUserNotVerified() {
        Code code = Code.builder()
                .code("CODE123")
                .scope(CodeScopeEnum.EMAIL_VERIFICATION)
                .user(user)
                .build();
        when(userService.findOptionalByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(codeService.create(user, CodeScopeEnum.EMAIL_VERIFICATION)).thenReturn(code);

        verificationService.requestVerifyEmail(user.getEmail());

        verify(userService).findOptionalByEmail(user.getEmail());
        verify(codeService).create(user, CodeScopeEnum.EMAIL_VERIFICATION);
        verify(mailService).sendEmailVerificationCode(user.getEmail(), code.getCode());
    }

    @Test
    void requestVerifyEmail_shouldSkipWhenUserNotFound() {
        when(userService.findOptionalByEmail(user.getEmail())).thenReturn(Optional.empty());

        verificationService.requestVerifyEmail(user.getEmail());

        verify(userService).findOptionalByEmail(user.getEmail());
        verifyNoInteractions(codeService, mailService);
    }

    @Test
    void requestVerifyEmail_shouldSkipWhenAlreadyVerified() {
        user.setIsVerified(true);
        when(userService.findOptionalByEmail(user.getEmail())).thenReturn(Optional.of(user));

        verificationService.requestVerifyEmail(user.getEmail());

        verify(userService).findOptionalByEmail(user.getEmail());
        verifyNoInteractions(codeService, mailService);
    }

    @Test
    void verifyUser_shouldValidateCodeAndUpdateStatus() {
        Code code = Code.builder()
                .code("CODE123")
                .user(user)
                .scope(CodeScopeEnum.EMAIL_VERIFICATION)
                .build();
        when(codeService.validate(code.getCode(), CodeScopeEnum.EMAIL_VERIFICATION)).thenReturn(code);

        verificationService.verifyUser(code.getCode());

        verify(codeService).validate(code.getCode(), CodeScopeEnum.EMAIL_VERIFICATION);
        verify(userService).updateVerificationStatus(user.getId(), true);
    }
}
