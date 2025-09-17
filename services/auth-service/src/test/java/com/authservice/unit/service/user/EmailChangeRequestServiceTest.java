package com.authservice.unit.service.user;

import com.authservice.dto.request.ChangeEmailRequest;
import com.authservice.entity.Code;
import com.authservice.entity.CodeScopeEnum;
import com.authservice.entity.EmailChangeRequest;
import com.authservice.entity.User;
import com.authservice.exceptions.CodeValidationException;
import com.authservice.exceptions.NotFoundException;
import com.authservice.services.EmailChangeRequestService;
import com.authservice.services.MailService;
import com.authservice.services.code.CodeCreationService;
import com.authservice.services.code.CodeValidationService;
import com.authservice.services.user.EmailChangeService;
import com.authservice.services.user.PasswordService;
import com.authservice.services.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailChangeRequestServiceTest {

    @Mock
    private EmailChangeRequestService emailChangeRequestService;

    @Mock
    private CodeCreationService codeCreationService;

    @Mock
    private MailService mailService;

    @Mock
    private CodeValidationService codeValidationService;

    @Mock
    private UserService userService;

    @Mock
    private PasswordService passwordService;

    @InjectMocks
    private EmailChangeService emailChangeService;


    private static final String EMAIL = "test@gmail.com";
    private static final String NEW_EMAIL = "newTest@gmail.com";
    private static final String PASSWORD = "testPassword";
    private static final String HASHED_PASSWORD = "hashedPassword";
    private static final long USER_ID = 1L;
    private static final String CODE = "testCode";
    private static final ChangeEmailRequest CHANGE_EMAIL_REQUEST = new ChangeEmailRequest(NEW_EMAIL, PASSWORD);
    private static final String PROVIDED_CODE = "providedCode";


    private User user;
    private Code code;
    private EmailChangeRequest emailChangeRequest;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .id(USER_ID)
                .email(EMAIL)
                .password(HASHED_PASSWORD)
                .build();

        code = Code.builder()
                .user(user)
                .code(CODE)
                .build();

        emailChangeRequest = EmailChangeRequest.builder()
                .newEmail(NEW_EMAIL)
                .createdAt(Instant.now())
                .user(user)
                .build();
    }


    @Test
    public void testCreateRequest_whenCalledWithWrongPassword_thenThrowException() {
        when(userService.findById(anyLong())).thenReturn(user);
        when(passwordService.comparePassword(anyString(), anyString())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> {
            emailChangeService.createRequest(CHANGE_EMAIL_REQUEST, USER_ID);
        });

        verify(userService).findById(USER_ID);
        verify(passwordService).comparePassword(HASHED_PASSWORD, PASSWORD);
        verifyNoInteractions(emailChangeRequestService, codeCreationService, mailService);
    }

    @Test
    public void testCreateRequest_whenCalledWithValidPassword_thenCreateRequest() {
        when(userService.findById(anyLong())).thenReturn(user);
        when(passwordService.comparePassword(anyString(), anyString())).thenReturn(true);
        when(codeCreationService.getCode(any(User.class), any(CodeScopeEnum.class))).thenReturn(code);

        emailChangeService.createRequest(CHANGE_EMAIL_REQUEST, USER_ID);

        verify(userService).findById(USER_ID);
        verify(passwordService).comparePassword(HASHED_PASSWORD, PASSWORD);
        verify(emailChangeRequestService).createOrUpdateEmailChangeRequest(user, NEW_EMAIL);
        verify(codeCreationService).getCode(user, CodeScopeEnum.EMAIL_CHANGE);
        verify(mailService).sendEmailChange(EMAIL, CODE);
    }

    @Test
    public void testCreateRequest_whenCalledWithNonExistentUser_thenThrowException() {
        when(userService.findById(anyLong())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> {
            emailChangeService.createRequest(CHANGE_EMAIL_REQUEST, USER_ID);
        });

        verify(userService).findById(USER_ID);
        verifyNoInteractions(passwordService, codeCreationService, mailService, emailChangeRequestService);
    }

    @Test
    public void testChangeEmail_whenCalledWithValidCode_thenChangeEmail() {
        when(codeValidationService.validate(anyString(), any(CodeScopeEnum.class))).thenReturn(code);
        when(emailChangeRequestService.getByUserId(anyLong())).thenReturn(emailChangeRequest);

        emailChangeService.changeEmail(PROVIDED_CODE);

        verify(codeValidationService).validate(PROVIDED_CODE, CodeScopeEnum.EMAIL_CHANGE);
        verify(emailChangeRequestService).getByUserId(USER_ID);
        verify(emailChangeRequestService).deleteEmailChangeRequest(emailChangeRequest);

        assertEquals(NEW_EMAIL, user.getEmail());
    }

    @Test
    public void testChangeEmail_whenCalledWithInvalidCode_thenThrowException() {
        when(codeValidationService.validate(anyString(), any(CodeScopeEnum.class))).thenThrow(CodeValidationException.class);

        assertThrows(CodeValidationException.class, () -> {
            emailChangeService.changeEmail(PROVIDED_CODE);
        });

        verify(codeValidationService).validate(PROVIDED_CODE, CodeScopeEnum.EMAIL_CHANGE);
        verifyNoInteractions(emailChangeRequestService);

        assertEquals(EMAIL, user.getEmail());
    }

    @Test
    public void testChangeEmail_whenCalledWithNonExistentEmailChangeRequest_thenThrowException() {
        when(codeValidationService.validate(anyString(), any(CodeScopeEnum.class))).thenReturn(code);
        when(emailChangeRequestService.getByUserId(anyLong())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> {
            emailChangeService.changeEmail(PROVIDED_CODE);
        });

        verify(codeValidationService).validate(PROVIDED_CODE, CodeScopeEnum.EMAIL_CHANGE);
        verify(emailChangeRequestService).getByUserId(USER_ID);
        verifyNoMoreInteractions(emailChangeRequestService);

        assertEquals(EMAIL, user.getEmail());
    }
}
