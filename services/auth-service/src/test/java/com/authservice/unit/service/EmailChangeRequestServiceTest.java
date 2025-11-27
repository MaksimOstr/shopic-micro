package com.authservice.unit.service;

import com.authservice.entity.EmailChangeRequest;
import com.authservice.entity.User;
import com.authservice.exception.NotFoundException;
import com.authservice.repositories.EmailChangeRequestRepository;
import com.authservice.services.EmailChangeRequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailChangeRequestServiceTest {
    @Mock
    private EmailChangeRequestRepository emailChangeRequestRepository;

    @InjectMocks
    private EmailChangeRequestService emailChangeRequestService;



    private static final long USER_ID = 1L;
    private static final String EMAIL = "test@gmail.com";
    private static final String NEW_EMAIL = "new@gmail.com";



    private User user;
    private EmailChangeRequest emailChangeRequest;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .id(USER_ID)
                .build();

        emailChangeRequest = EmailChangeRequest.builder()
                .user(user)
                .newEmail(EMAIL)
                .build();
    }


    @Test
    public void testGetByUserId_whenCalledWithExistingRequest_thenReturn() {
        when(emailChangeRequestRepository.findByUser_Id(anyLong())).thenReturn(Optional.of(emailChangeRequest));

        EmailChangeRequest result = emailChangeRequestService.getByUserId(USER_ID);

        verify(emailChangeRequestRepository).findByUser_Id(USER_ID);

        assertEquals(emailChangeRequest, result);
    }

    @Test
    public void testGetByUserId_whenCalledWithNonExistingRequest_thenThrowException() {
        when(emailChangeRequestRepository.findByUser_Id(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            emailChangeRequestService.getByUserId(USER_ID);
        });

        verify(emailChangeRequestRepository).findByUser_Id(USER_ID);
    }

    @Test
    public void testCreateOrUpdateEmailChangeRequest_whenCalledWithExistingRequest_thenUpdate() {
        when(emailChangeRequestRepository.findByUser_Id(anyLong())).thenReturn(Optional.of(emailChangeRequest));

        emailChangeRequestService.createOrUpdateEmailChangeRequest(user, NEW_EMAIL);

        verify(emailChangeRequestRepository).findByUser_Id(USER_ID);
        verifyNoMoreInteractions(emailChangeRequestRepository);

        assertNotEquals(EMAIL, emailChangeRequest.getNewEmail());
    }

    @Test
    public void testCreateOrUpdateEmailChangeRequest_whenCalledWithNonExistingRequest_thenCreateNewRequest() {
        ArgumentCaptor<EmailChangeRequest> emailChangeRequestArgumentCaptor = ArgumentCaptor.forClass(EmailChangeRequest.class);

        when(emailChangeRequestRepository.findByUser_Id(anyLong())).thenReturn(Optional.empty());

        emailChangeRequestService.createOrUpdateEmailChangeRequest(user, NEW_EMAIL);

        verify(emailChangeRequestRepository).findByUser_Id(USER_ID);
        verify(emailChangeRequestRepository).save(emailChangeRequestArgumentCaptor.capture());

        EmailChangeRequest savedEmailChangeRequest = emailChangeRequestArgumentCaptor.getValue();

        assertEquals(NEW_EMAIL, savedEmailChangeRequest.getNewEmail());
        assertEquals(user, savedEmailChangeRequest.getUser());
    }

    @Test
    public void testDeleteEmailChangeRequest_whenCalledWithArgs_thenDelete() {
        emailChangeRequestService.deleteEmailChangeRequest(emailChangeRequest);

        verify(emailChangeRequestRepository).delete(emailChangeRequest);
    }
}
