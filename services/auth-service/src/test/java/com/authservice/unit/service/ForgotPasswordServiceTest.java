package com.authservice.unit.service;

import com.authservice.dto.ForgotPasswordRequest;
import com.authservice.dto.ResetPasswordRequest;
import com.authservice.entity.AuthProviderEnum;
import com.authservice.entity.Code;
import com.authservice.entity.CodeScopeEnum;
import com.authservice.entity.User;
import com.authservice.services.CodeService;
import com.authservice.services.ForgotPasswordService;
import com.authservice.services.MailService;
import com.authservice.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ForgotPasswordServiceTest {

    @Mock
    private MailService mailService;

    @Mock
    private UserService userService;

    @Mock
    private CodeService codeService;

    @InjectMocks
    private ForgotPasswordService forgotPasswordService;

    private static final String EMAIL = "test@gmail.com";

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email(EMAIL)
                .authProvider(AuthProviderEnum.LOCAL)
                .build();
    }

    @Test
    void requestResetPassword_shouldCreateCodeAndSendMailForLocalUser() {
        Code code = Code.builder()
                .code("RESET123")
                .scope(CodeScopeEnum.RESET_PASSWORD)
                .user(user)
                .build();

        when(userService.findOptionalByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(codeService.create(user, CodeScopeEnum.RESET_PASSWORD)).thenReturn(code);

        forgotPasswordService.requestResetPassword(new ForgotPasswordRequest(EMAIL));

        verify(userService).findOptionalByEmail(EMAIL);
        verify(codeService).create(user, CodeScopeEnum.RESET_PASSWORD);
        verify(mailService).sendForgotPasswordChange(EMAIL, code.getCode());
    }

    @Test
    void requestResetPassword_shouldSkipWhenUserNotFound() {
        when(userService.findOptionalByEmail(EMAIL)).thenReturn(Optional.empty());

        forgotPasswordService.requestResetPassword(new ForgotPasswordRequest(EMAIL));

        verify(userService).findOptionalByEmail(EMAIL);
        verifyNoInteractions(codeService, mailService);
    }

    @Test
    void requestResetPassword_shouldSkipWhenUserIsOAuth() {
        user.setAuthProvider(AuthProviderEnum.GOOGLE);
        when(userService.findOptionalByEmail(EMAIL)).thenReturn(Optional.of(user));

        forgotPasswordService.requestResetPassword(new ForgotPasswordRequest(EMAIL));

        verify(userService).findOptionalByEmail(EMAIL);
        verifyNoInteractions(codeService, mailService);
    }

    @Test
    void resetPassword_shouldValidateCodeAndChangePassword() {
        ResetPasswordRequest request = new ResetPasswordRequest("newPassword123", "providedCode");
        Code code = Code.builder()
                .code(request.code())
                .scope(CodeScopeEnum.RESET_PASSWORD)
                .user(user)
                .build();
        when(codeService.validate(request.code(), CodeScopeEnum.RESET_PASSWORD)).thenReturn(code);

        forgotPasswordService.resetPassword(request);

        verify(codeService).validate(request.code(), CodeScopeEnum.RESET_PASSWORD);
        verify(userService).resetPassword(user, request.newPassword());
    }
}
