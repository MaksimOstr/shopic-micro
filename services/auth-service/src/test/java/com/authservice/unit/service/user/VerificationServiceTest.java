package com.authservice.unit.service.user;

import com.authservice.entity.Code;
import com.authservice.entity.CodeScopeEnum;
import com.authservice.entity.User;
import com.authservice.exception.CodeValidationException;
import com.authservice.exception.EmailVerifyException;
import com.authservice.services.MailService;
import com.authservice.services.code.CodeCreationService;
import com.authservice.services.code.CodeValidationService;
import com.authservice.services.UserService;
import com.authservice.services.VerificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VerificationServiceTest {

    @Mock
    private CodeCreationService codeCreationService;

    @Mock
    private MailService mailService;

    @Mock
    private CodeValidationService codeValidationService;

    @Mock
    private UserService userService;

    @InjectMocks
    private VerificationService verificationService;


    private static final String EMAIL = "test@gmail.com";
    private static final String CODE = "testCode";
    private static final long USER_ID = 1L;


    private User user;
    private Code code;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .id(USER_ID)
                .email(EMAIL)
                .isVerified(false)
                .build();

        code = Code.builder()
                .code(CODE)
                .user(user)
                .build();
    }

    @Test
    public void testRequestVerifyEmail_whenCalledWithAlreadyVerifiedUser_thenThrowException() {
        user.setIsVerified(true);

        when(userService.findByEmail(anyString())).thenReturn(user);

        assertThrows(EmailVerifyException.class, () -> {
            verificationService.requestVerifyEmail(EMAIL);
        });

        verify(userService).findByEmail(EMAIL);
        verifyNoInteractions(codeCreationService, mailService);
    }

    @Test
    public void testRequestVerifyEmail_whenCalledWithNotVerifiedUser_thenCompletesSuccessfully() {
        when(userService.findByEmail(anyString())).thenReturn(user);
        when(codeCreationService.getCode(any(User.class), any(CodeScopeEnum.class))).thenReturn(code);

        verificationService.requestVerifyEmail(EMAIL);

        verify(userService).findByEmail(EMAIL);
        verify(codeCreationService).getCode(user, CodeScopeEnum.EMAIL_VERIFICATION);
        verify(mailService).sendEmailVerificationCode(EMAIL, CODE);
    }

    @Test
    public void testVerifyUser_whenCalledWithInvalidCode_thenNoVerifyUser() {
        when(codeValidationService.validate(anyString(), any(CodeScopeEnum.class))).thenThrow(CodeValidationException.class);

        assertThrows(CodeValidationException.class, () -> {
            verificationService.verifyUser(CODE);
        });

        verify(codeValidationService).validate(CODE, CodeScopeEnum.EMAIL_VERIFICATION);
        verifyNoInteractions(userService);
    }

    @Test
    public void testVerifyUser_whenCalledWithValidCode_thenVerifyUser() {
        when(codeValidationService.validate(anyString(), any(CodeScopeEnum.class))).thenReturn(code);

        verificationService.verifyUser(CODE);

        verify(codeValidationService).validate(CODE, CodeScopeEnum.EMAIL_VERIFICATION);
        verify(userService).updateVerificationStatus(USER_ID, true);
    }
}
