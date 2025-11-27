package com.authservice.unit.service.user;

import com.authservice.dto.LocalRegisterRequest;
import com.authservice.entity.AuthProviderEnum;
import com.authservice.entity.Role;
import com.authservice.entity.User;
import com.authservice.exception.AlreadyExistsException;
import com.authservice.services.user.LocalUserService;
import com.authservice.services.user.PasswordService;
import com.authservice.services.RoleService;
import com.authservice.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LocalUserServiceTest {
    @Mock
    private RoleService roleService;

    @Mock
    private PasswordService passwordService;

    @Mock
    private UserService userService;

    @InjectMocks
    private LocalUserService localUserService;


    private static final String EMAIL = "test@gmail.com";
    private static final String PASSWORD = "testPassword";
    private static final String FIRST_NAME = "testFirstName";
    private static final String LAST_NAME = "testLastName";
    private static final String HASHED_PASSWORD = "testHashedPassword";
    private static final long USER_ID = 1L;
    private static final AuthProviderEnum LOCAL_AUTH_PROVIDER = AuthProviderEnum.LOCAL;
    private static final LocalRegisterRequest LOCAL_REGISTER_REQUEST = new LocalRegisterRequest(PASSWORD, EMAIL, FIRST_NAME, LAST_NAME);


    private User user;
    private Role userRole;

    @BeforeEach
    public void setUp() {
        userRole = Role.builder()
                .name("ROLE_USER")
                .build();

        user = User.builder()
                .id(USER_ID)
                .password(HASHED_PASSWORD)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .roles(Set.of(userRole))
                .email(EMAIL)
                .build();
    }

    @Test
    public void testCreateLocalUser_whenCalledWithNonExistingUser_thenCreateNewUser() {
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        when(userService.isUserExist(anyString())).thenReturn(false);
        when(roleService.getDefaultUserRole()).thenReturn(userRole);
        when(passwordService.encode(anyString())).thenReturn(HASHED_PASSWORD);
        when(userService.save(any(User.class))).thenReturn(user);

        localUserService.createLocalUser(LOCAL_REGISTER_REQUEST);

        verify(userService).isUserExist(EMAIL);
        verify(roleService).getDefaultUserRole();
        verify(passwordService).encode(PASSWORD);
        verify(userService).save(userArgumentCaptor.capture());

        User userForSaving = userArgumentCaptor.getValue();

        assertEquals(HASHED_PASSWORD, userForSaving.getPassword());
        assertEquals(FIRST_NAME, userForSaving.getFirstName());
        assertEquals(LAST_NAME, userForSaving.getLastName());
        assertEquals(EMAIL, userForSaving.getEmail());
        assertEquals(Set.of(userRole), userForSaving.getRoles());
        assertEquals(LOCAL_AUTH_PROVIDER, userForSaving.getAuthProvider());
    }

    @Test
    public void testCreateLocalUser_whenCalledWithExistingUser_thenThrowException() {
        when(userService.isUserExist(anyString())).thenReturn(true);

        assertThrows(AlreadyExistsException.class, () -> {
            localUserService.createLocalUser(LOCAL_REGISTER_REQUEST);
        });

        verify(userService).isUserExist(EMAIL);
        verifyNoInteractions(roleService, passwordService);
        verifyNoMoreInteractions(userService);
    }
}
