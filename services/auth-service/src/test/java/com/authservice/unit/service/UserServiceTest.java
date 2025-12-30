package com.authservice.unit.service;

import com.authservice.dto.ChangePasswordRequest;
import com.authservice.dto.LocalRegisterRequest;
import com.authservice.dto.UserDto;
import com.authservice.entity.AuthProviderEnum;
import com.authservice.entity.User;
import com.authservice.entity.UserRolesEnum;
import com.authservice.exception.ApiException;
import com.authservice.exception.NotFoundException;
import com.authservice.mapper.UserMapper;
import com.authservice.repositories.UserRepository;
import com.authservice.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private static final String EMAIL = "test@gmail.com";
    private static final String REQUESTED_PASSWORD = "password123";
    private static final String HASHED_PASSWORD = "hashed";
    private UUID userId;
    private User user;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = User.builder()
                .id(userId)
                .email(EMAIL)
                .password(HASHED_PASSWORD)
                .isVerified(false)
                .isNonBlocked(true)
                .authProvider(AuthProviderEnum.LOCAL)
                .role(UserRolesEnum.ROLE_USER)
                .build();
    }

    @Test
    void createUser_shouldThrowWhenPasswordsDoNotMatch() {
        LocalRegisterRequest request = new LocalRegisterRequest(REQUESTED_PASSWORD, "mismatch", EMAIL);

        ApiException exception = assertThrows(ApiException.class, () -> userService.createUser(request));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        verifyNoInteractions(userRepository, passwordEncoder);
    }

    @Test
    void createUser_shouldThrowWhenEmailAlreadyExists() {
        LocalRegisterRequest request = new LocalRegisterRequest(REQUESTED_PASSWORD, REQUESTED_PASSWORD, EMAIL);
        when(userRepository.existsByEmail(EMAIL)).thenReturn(true);

        ApiException exception = assertThrows(ApiException.class, () -> userService.createUser(request));

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        verify(userRepository).existsByEmail(EMAIL);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void createUser_shouldEncodePasswordAndPersistUser() {
        LocalRegisterRequest request = new LocalRegisterRequest(REQUESTED_PASSWORD, REQUESTED_PASSWORD, EMAIL);
        when(userRepository.existsByEmail(EMAIL)).thenReturn(false);
        when(passwordEncoder.encode(REQUESTED_PASSWORD)).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        User created = userService.createUser(request);

        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertEquals("encoded", saved.getPassword());
        assertEquals(AuthProviderEnum.LOCAL, saved.getAuthProvider());
        assertEquals(UserRolesEnum.ROLE_USER, saved.getRole());
        assertEquals(created, saved);
    }

    @Test
    void createOrGetOAuthUser_shouldReturnExistingUserWhenFound() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        User result = userService.createOrGetOAuthUser(AuthProviderEnum.GOOGLE, EMAIL);

        assertEquals(user, result);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createOrGetOAuthUser_shouldCreateWhenNotExists() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        User result = userService.createOrGetOAuthUser(AuthProviderEnum.GOOGLE, EMAIL);

        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertEquals(AuthProviderEnum.GOOGLE, saved.getAuthProvider());
        assertEquals(UserRolesEnum.ROLE_USER, saved.getRole());
        assertEquals(result, saved);
    }

    @Test
    void resetPassword_shouldThrowWhenPasswordSame() {
        when(passwordEncoder.matches(REQUESTED_PASSWORD, user.getPassword())).thenReturn(true);

        ApiException exception = assertThrows(ApiException.class,
                () -> userService.resetPassword(user, REQUESTED_PASSWORD));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Password is the same", exception.getMessage());

        verify(passwordEncoder).matches(REQUESTED_PASSWORD, user.getPassword());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void resetPassword_shouldEncodeAndUpdatePassword() {
        when(passwordEncoder.matches(any(), any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("newEncoded");

        userService.resetPassword(user, REQUESTED_PASSWORD);

        assertEquals("newEncoded", user.getPassword());
        verify(passwordEncoder).matches(REQUESTED_PASSWORD, HASHED_PASSWORD);
        verify(passwordEncoder).encode(REQUESTED_PASSWORD);
    }

    @Test
    void changePassword_shouldThrowWhenOldPasswordDoesNotMatch() {
        ChangePasswordRequest request = new ChangePasswordRequest("old", "newPassword");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.oldPassword(), user.getPassword())).thenReturn(false);

        ApiException exception = assertThrows(ApiException.class,
                () -> userService.changePassword(userId, request));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());

        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void changePassword_shouldThrowWhenNewPasswordSameAsOld() {
        ChangePasswordRequest request = new ChangePasswordRequest("old", "newPassword");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.oldPassword(), user.getPassword())).thenReturn(true);
        when(passwordEncoder.matches(request.newPassword(), user.getPassword())).thenReturn(true);

        ApiException exception = assertThrows(ApiException.class,
                () -> userService.changePassword(userId, request));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("New password is the same as the old one", exception.getMessage());

        verify(passwordEncoder).matches(request.oldPassword(), user.getPassword());
        verify(passwordEncoder).matches(request.newPassword(), user.getPassword());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void changePassword_shouldEncodeAndUpdatePasswordWhenValid() {
        ChangePasswordRequest request = new ChangePasswordRequest("old", "newPassword");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.oldPassword(), user.getPassword())).thenReturn(true);
        when(passwordEncoder.matches(request.newPassword(), user.getPassword())).thenReturn(false);
        when(passwordEncoder.encode(request.newPassword())).thenReturn("encodedNew");

        userService.changePassword(userId, request);

        assertEquals("encodedNew", user.getPassword());
        verify(passwordEncoder).matches(request.oldPassword(), HASHED_PASSWORD);
        verify(passwordEncoder).matches(request.newPassword(), HASHED_PASSWORD);
        verify(passwordEncoder).encode(request.newPassword());
    }

    @Test
    void changePassword_shouldThrowWhenUserNotFound() {
        ChangePasswordRequest request = new ChangePasswordRequest("old", "newPassword");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.changePassword(userId, request));

        verify(passwordEncoder, never()).matches(any(), any());
        verify(passwordEncoder, never()).encode(any());
    }


    @Test
    void getUserDto_shouldReturnMappedDto() {
        UserDto userDto = new UserDto(userId, EMAIL, true, Instant.now(), Instant.now());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.getUserDto(userId);

        assertEquals(userDto, result);
    }

    @Test
    void findByEmail_shouldThrowWhenMissing() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.findByEmail(EMAIL));
    }

    @Test
    void updateVerificationStatus_shouldThrowWhenNothingUpdated() {
        when(userRepository.markUserVerified(userId, true)).thenReturn(0);

        assertThrows(NotFoundException.class, () -> userService.updateVerificationStatus(userId, true));
    }

    @Test
    void updateVerificationStatus_shouldSucceedWhenUpdated() {
        when(userRepository.markUserVerified(userId, true)).thenReturn(1);

        userService.updateVerificationStatus(userId, true);

        verify(userRepository).markUserVerified(userId, true);
    }
}
