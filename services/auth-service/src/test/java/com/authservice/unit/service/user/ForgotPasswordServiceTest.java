package com.authservice.unit.service.user;

import com.authservice.dto.request.ForgotPasswordRequest;
import com.authservice.entity.AuthProviderEnum;
import com.authservice.entity.Code;
import com.authservice.entity.CodeScopeEnum;
import com.authservice.entity.User;
import com.authservice.exceptions.CodeValidationException;
import com.authservice.exceptions.ResetPasswordException;
import com.authservice.services.MailService;
import com.authservice.services.code.CodeCreationService;
import com.authservice.services.code.CodeValidationService;
import com.authservice.services.user.ForgotPasswordService;
import com.authservice.services.user.PasswordService;
import com.authservice.services.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.authservice.unit.service.user.ForgotPasswordServiceTest.Resources.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ForgotPasswordServiceTest {
    @Mock
    private CodeCreationService codeCreationService;

    @Mock
    private MailService mailService;

    @Mock
    private CodeValidationService codeValidationService;

    @Mock
    private PasswordService passwordService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ForgotPasswordService forgotPasswordService;

    private User user;
    private Code code;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email(EMAIL)
                .password(PASSWORD)
                .authProvider(LOCAL_AUTH_PROVIDER_ENUM)
                .build();
        code = Code.builder()
                .user(user)
                .code(CODE)
                .build();
    }

    @Test
    public void testRequestResetPassword_whenCalledForLocalUser_thenCreateRequest() {
        when(userService.findByEmail(anyString())).thenReturn(user);
        when(codeCreationService.getCode(any(User.class), any(CodeScopeEnum.class))).thenReturn(code);

        forgotPasswordService.requestResetPassword(FORGOT_PASSWORD_REQUEST);

        verify(userService).findByEmail(EMAIL);
        verify(codeCreationService).getCode(user, CodeScopeEnum.RESET_PASSWORD);
        verify(mailService).sendForgotPasswordChange(EMAIL, CODE);
    }

    @Test
    public void testRequestResetPassword_whenForNonLocalUser_thenThrowException() {
        user.setAuthProvider(OAUTH_AUTH_PROVIDER_ENUM);

        when(userService.findByEmail(anyString())).thenReturn(user);

        assertThrows(ResetPasswordException.class, () -> {
            forgotPasswordService.requestResetPassword(FORGOT_PASSWORD_REQUEST);
        });

        verify(userService).findByEmail(EMAIL);
        verifyNoInteractions(codeCreationService, mailService);
    }

    @Test
    public void testResetPassword_whenCalledWithValidCode_thenResetPassword() {
        when(codeValidationService.validate(anyString(), any(CodeScopeEnum.class))).thenReturn(code);
        when(passwordService.comparePassword(anyString(), anyString())).thenReturn(false);
        when(passwordService.encode(anyString())).thenReturn(NEW_PASSWORD);

        forgotPasswordService.resetPassword(NEW_PASSWORD, CODE);

        verify(codeValidationService).validate(CODE, CodeScopeEnum.RESET_PASSWORD);
        verify(passwordService).comparePassword(PASSWORD, NEW_PASSWORD);
        verify(passwordService).encode(NEW_PASSWORD);

        assertEquals(NEW_PASSWORD, user.getPassword());
    }

    @Test
    public void testResetPassword_whenCalledWithInvalidCode_thenThrowException() {
        when(codeValidationService.validate(anyString(), any(CodeScopeEnum.class))).thenThrow(CodeValidationException.class);

        assertThrows(CodeValidationException.class, () -> {
            forgotPasswordService.resetPassword(NEW_PASSWORD, CODE);
        });

        verifyNoInteractions(passwordService);

        assertEquals(PASSWORD, user.getPassword());
    }

    @Test
    public void testResetPassword_whenCalledWithTheSameCode_thenResetPasswordException() {
        when(codeValidationService.validate(anyString(), any(CodeScopeEnum.class))).thenReturn(code);
        when(passwordService.comparePassword(anyString(), anyString())).thenReturn(true);

        assertThrows(ResetPasswordException.class, () -> {
            forgotPasswordService.resetPassword(PASSWORD, CODE);
        });

        verify(codeValidationService).validate(CODE, CodeScopeEnum.RESET_PASSWORD);
        verify(passwordService).comparePassword(PASSWORD, PASSWORD);
        verifyNoMoreInteractions(passwordService);

        assertEquals(PASSWORD, user.getPassword());
    }

    static class Resources {
        public static final AuthProviderEnum LOCAL_AUTH_PROVIDER_ENUM = AuthProviderEnum.LOCAL;
        public static final AuthProviderEnum OAUTH_AUTH_PROVIDER_ENUM = AuthProviderEnum.GOOGLE;
        public static final String EMAIL = "test@gmail.com";
        public static final ForgotPasswordRequest FORGOT_PASSWORD_REQUEST = new ForgotPasswordRequest(EMAIL);
        public static final String CODE = "testCode";
        public static final String PASSWORD = "testPassword";
        public static final String NEW_PASSWORD = "newTestPassword";
    }
}
