package com.authservice.unit.service.user;

import com.authservice.dto.request.ChangeEmailRequest;
import com.authservice.entity.Code;
import com.authservice.entity.CodeScopeEnum;
import com.authservice.entity.EmailChangeRequest;
import com.authservice.entity.User;
import com.authservice.exceptions.CodeValidationException;
import com.authservice.exceptions.NotFoundException;
import com.authservice.repositories.EmailChangeRequestRepository;
import com.authservice.services.MailService;
import com.authservice.services.code.CodeCreationService;
import com.authservice.services.code.CodeValidationService;
import com.authservice.services.user.EmailChangeRequestService;
import com.authservice.services.user.PasswordService;
import com.authservice.services.user.UserQueryService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static com.authservice.unit.service.user.EmailChangeRequestServiceTest.Resources.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailChangeRequestServiceTest {

    @Mock
    private EmailChangeRequestRepository emailChangeRequestRepository;

    @Mock
    private CodeCreationService codeCreationService;

    @Mock
    private MailService mailService;

    @Mock
    private CodeValidationService codeValidationService;

    @Mock
    private UserQueryService userQueryService;

    @Mock
    private PasswordService passwordService;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private EmailChangeRequestService emailChangeRequestService;

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
        when(passwordService.comparePassword(anyString(), anyString())).thenReturn(false);
        when(userQueryService.findById(anyLong())).thenReturn(user);

        assertThrows(IllegalArgumentException.class, () -> {
            emailChangeRequestService.createRequest(CHANGE_EMAIL_REQUEST, USER_ID);
        });

        verify(userQueryService).findById(USER_ID);
        verify(passwordService).comparePassword(HASHED_PASSWORD, PASSWORD);
        verifyNoInteractions(emailChangeRequestRepository, codeCreationService, mailService);
    }

    @Test
    public void testCreateRequest_whenCalledWithValidPassword_thenCreateRequest() {
        ArgumentCaptor<EmailChangeRequest> changeEmailRequestArgumentCaptor = ArgumentCaptor.forClass(EmailChangeRequest.class);

        when(userQueryService.findById(anyLong())).thenReturn(user);
        when(passwordService.comparePassword(anyString(), anyString())).thenReturn(true);
        when(emailChangeRequestRepository.findByUser_Id(anyLong())).thenReturn(Optional.empty());
        when(codeCreationService.getCode(any(User.class), any(CodeScopeEnum.class))).thenReturn(code);
        when(entityManager.getReference(eq(User.class), anyLong())).thenReturn(user);

        emailChangeRequestService.createRequest(CHANGE_EMAIL_REQUEST, USER_ID);

        verify(userQueryService).findById(USER_ID);
        verify(passwordService).comparePassword(HASHED_PASSWORD, PASSWORD);
        verify(emailChangeRequestRepository).findByUser_Id(USER_ID);
        verify(entityManager).getReference(User.class, USER_ID);
        verify(emailChangeRequestRepository).save(changeEmailRequestArgumentCaptor.capture());
        verify(codeCreationService).getCode(user, CodeScopeEnum.EMAIL_CHANGE);

        EmailChangeRequest emailChangeRequest = changeEmailRequestArgumentCaptor.getValue();

        assertEquals(NEW_EMAIL, emailChangeRequest.getNewEmail());
        assertEquals(user, emailChangeRequest.getUser());
    }

    @Test
    public void testCreateRequest_whenCalledWithExistingEmailChangeRequest_thenShouldUpdateEmailChangeRequest() {
        EmailChangeRequest emailChangeRequestWithOldEmail = EmailChangeRequest.builder()
                .newEmail(EMAIL)
                .createdAt(Instant.now())
                .user(user)
                .build();

        when(userQueryService.findById(anyLong())).thenReturn(user);
        when(passwordService.comparePassword(anyString(), anyString())).thenReturn(true);
        when(emailChangeRequestRepository.findByUser_Id(anyLong())).thenReturn(Optional.of(emailChangeRequestWithOldEmail));
        when(codeCreationService.getCode(any(User.class), any(CodeScopeEnum.class))).thenReturn(code);

        emailChangeRequestService.createRequest(CHANGE_EMAIL_REQUEST, USER_ID);

        verify(userQueryService).findById(USER_ID);
        verify(passwordService).comparePassword(HASHED_PASSWORD, PASSWORD);
        verify(emailChangeRequestRepository).findByUser_Id(USER_ID);
        verify(codeCreationService).getCode(user, CodeScopeEnum.EMAIL_CHANGE);

        assertEquals(NEW_EMAIL, emailChangeRequestWithOldEmail.getNewEmail());
    }

    @Test
    public void testCreateRequest_whenCalledWithNonExistentUser_thenThrowException() {
        when(userQueryService.findById(anyLong())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> {
            emailChangeRequestService.createRequest(CHANGE_EMAIL_REQUEST, USER_ID);
        });

        verify(userQueryService).findById(USER_ID);
        verifyNoInteractions(passwordService, codeCreationService, mailService, emailChangeRequestRepository);
    }

    @Test
    public void testChangeEmail_whenCalledWithValidCode_thenChangeEmail() {
        when(codeValidationService.validate(anyString(), any(CodeScopeEnum.class))).thenReturn(code);
        when(emailChangeRequestRepository.findByUser_Id(anyLong())).thenReturn(Optional.of(emailChangeRequest));

        emailChangeRequestService.changeEmail(PROVIDED_CODE);

        verify(codeValidationService).validate(PROVIDED_CODE, CodeScopeEnum.EMAIL_CHANGE);
        verify(emailChangeRequestRepository).findByUser_Id(USER_ID);
        verify(emailChangeRequestRepository).delete(emailChangeRequest);

        assertEquals(Resources.NEW_EMAIL, emailChangeRequest.getNewEmail());
    }

    @Test
    public void testChangeEmail_whenCalledWithInvalidCode_thenThrowException() {
        when(codeValidationService.validate(anyString(), any(CodeScopeEnum.class))).thenThrow(CodeValidationException.class);

        assertThrows(CodeValidationException.class, () -> {
            emailChangeRequestService.changeEmail(PROVIDED_CODE);
        });

        verify(codeValidationService).validate(PROVIDED_CODE, CodeScopeEnum.EMAIL_CHANGE);
        verifyNoInteractions(emailChangeRequestRepository);
    }

    @Test
    public void testChangeEmail_whenCalledWithNonExistentEmailChangeRequest_thenThrowException() {
        when(codeValidationService.validate(anyString(), any(CodeScopeEnum.class))).thenReturn(code);
        when(emailChangeRequestRepository.findByUser_Id(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            emailChangeRequestService.changeEmail(PROVIDED_CODE);
        });

        verify(codeValidationService).validate(PROVIDED_CODE, CodeScopeEnum.EMAIL_CHANGE);
        verifyNoMoreInteractions(emailChangeRequestRepository);

        assertEquals(EMAIL, user.getEmail());
    }

    @Test
    public void testCleanupOldRequests() {
        when(emailChangeRequestRepository.deleteAllByCreatedAtBefore(any(Instant.class))).thenReturn(1);

        emailChangeRequestService.cleanupOldRequests();

        verify(emailChangeRequestRepository).deleteAllByCreatedAtBefore(any(Instant.class));
    }


    static class Resources {
        public static final String EMAIL = "test@gmail.com";
        public static final String NEW_EMAIL = "newTest@gmail.com";
        public static final String PASSWORD = "testPassword";
        public static final String HASHED_PASSWORD = "hashedPassword";
        public static final long USER_ID = 1L;
        public static final String CODE = "testCode";
        public static final ChangeEmailRequest CHANGE_EMAIL_REQUEST = new ChangeEmailRequest(NEW_EMAIL, PASSWORD);
        public static final String PROVIDED_CODE = "providedCode";
    }
}
