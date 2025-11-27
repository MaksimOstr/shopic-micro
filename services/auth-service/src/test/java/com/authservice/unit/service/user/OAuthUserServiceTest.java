package com.authservice.unit.service.user;

import com.authservice.dto.CreateOAuthUserRequest;
import com.authservice.dto.response.CreateOAuthUserResponse;
import com.authservice.entity.AuthProviderEnum;
import com.authservice.entity.Role;
import com.authservice.entity.User;
import com.authservice.mapper.UserMapper;
import com.authservice.services.user.OAuthUserService;
import com.authservice.services.RoleService;
import com.authservice.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OAuthUserServiceTest {
    @Mock
    private UserService userService;

    @Mock
    private RoleService roleService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private OAuthUserService oAuthUserService;


    private static final long USER_ID = 1L;
    private static final AuthProviderEnum GOOGLE_AUTH_PROVIDER_ENUM = AuthProviderEnum.GOOGLE;
    private static final String EMAIL = "test@gmail.com";
    private static final String FIRST_NAME = "testFirstName";
    private static final String LAST_NAME = "testLastName";
    private static final CreateOAuthUserRequest CREATE_O_AUTH_USER_REQUEST = new CreateOAuthUserRequest(
            GOOGLE_AUTH_PROVIDER_ENUM,
            EMAIL,
            FIRST_NAME,
            LAST_NAME
    );

    private static final String USER_ROLE_NAME = "ROLE_USER";
    private static final CreateOAuthUserResponse CREATE_O_AUTH_USER_RESPONSE = new CreateOAuthUserResponse(
            USER_ID,
            EMAIL,
            GOOGLE_AUTH_PROVIDER_ENUM,
            List.of(USER_ROLE_NAME)
    );


    private Role role;
    private User user;

    @BeforeEach
    public void setUp() {
        role = Role.builder()
                .name(USER_ROLE_NAME)
                .build();

        user = User.builder()
                .id(USER_ID)
                .email(EMAIL)
                .authProvider(GOOGLE_AUTH_PROVIDER_ENUM)
                .roles(Set.of(role))
                .build();
    }


    @Test
    public void testCreateOrGetOAuthUser_whenCalledWithExistentUser_thenReturnResponse() {
        when(userService.findOptionalByEmail(anyString())).thenReturn(Optional.of(user));
        when(userMapper.toCreateOAuthUserResponse(any(User.class))).thenReturn(CREATE_O_AUTH_USER_RESPONSE);

        CreateOAuthUserResponse result = oAuthUserService.createOrGetOAuthUser(CREATE_O_AUTH_USER_REQUEST);

        verify(userService).findOptionalByEmail(EMAIL);
        verify(userMapper).toCreateOAuthUserResponse(user);
        verifyNoInteractions(roleService);
        verifyNoMoreInteractions(userService);

        assertEquals(USER_ID, result.userId());
        assertEquals(EMAIL, result.email());
        assertEquals(GOOGLE_AUTH_PROVIDER_ENUM, result.provider());
        assertEquals(List.of(USER_ROLE_NAME), result.roleNames());
    }

    @Test
    public void testCreateOrGetOAuthUser_whenCalledWithNonExistentUser_thenCreateNewUser() {
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        when(userService.findOptionalByEmail(anyString())).thenReturn(Optional.empty());
        when(roleService.getDefaultUserRole()).thenReturn(role);
        when(userService.save(any(User.class))).thenReturn(user);
        when(userMapper.toCreateOAuthUserResponse(any(User.class))).thenReturn(CREATE_O_AUTH_USER_RESPONSE);

        CreateOAuthUserResponse result = oAuthUserService.createOrGetOAuthUser(CREATE_O_AUTH_USER_REQUEST);

        verify(userService).findOptionalByEmail(EMAIL);
        verify(roleService).getDefaultUserRole();
        verify(userService).save(userArgumentCaptor.capture());
        verify(userMapper).toCreateOAuthUserResponse(user);

        User userForSaving = userArgumentCaptor.getValue();

        assertEquals(FIRST_NAME, userForSaving.getFirstName());
        assertEquals(LAST_NAME, userForSaving.getLastName());
        assertEquals(GOOGLE_AUTH_PROVIDER_ENUM, userForSaving.getAuthProvider());
        assertEquals(Set.of(role), userForSaving.getRoles());
        assertEquals(EMAIL, userForSaving.getEmail());
        assertEquals(CREATE_O_AUTH_USER_RESPONSE, result);
    }
}
