package com.authservice.unit.service;

import com.authservice.dto.LocalRegisterRequest;
import com.authservice.dto.LocalRegisterResult;
import com.authservice.dto.SignInRequestDto;
import com.authservice.dto.TokenPairDto;
import com.authservice.entity.Code;
import com.authservice.entity.CodeScopeEnum;
import com.authservice.entity.RefreshToken;
import com.authservice.entity.User;
import com.authservice.entity.UserRolesEnum;
import com.authservice.exception.ApiException;
import com.authservice.security.CustomUserDetails;
import com.authservice.services.AuthService;
import com.authservice.services.CodeService;
import com.authservice.services.JwtService;
import com.authservice.services.MailService;
import com.authservice.services.RefreshTokenService;
import com.authservice.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private CodeService codeService;

    @Mock
    private MailService mailService;

    @InjectMocks
    private AuthService authService;

    private static final String EMAIL = "test@gmail.com";
    private static final String PASSWORD = "password123";
    private static final String ACCESS_TOKEN = "access";
    private static final String REFRESH_TOKEN = "refresh";

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(UUID.randomUUID())
                .email(EMAIL)
                .password("hashed")
                .isNonBlocked(true)
                .isVerified(false)
                .role(UserRolesEnum.ROLE_USER)
                .createdAt(Instant.now())
                .build();
    }

    @Test
    void localRegister_shouldCreateUserGenerateCodeAndSendMail() {
        LocalRegisterRequest request = new LocalRegisterRequest(PASSWORD, PASSWORD, EMAIL);
        Code code = Code.builder()
                .code("ABC12345")
                .user(user)
                .scope(CodeScopeEnum.EMAIL_VERIFICATION)
                .build();

        when(userService.createUser(request)).thenReturn(user);
        when(codeService.create(user, CodeScopeEnum.EMAIL_VERIFICATION)).thenReturn(code);

        LocalRegisterResult result = authService.localRegister(request);

        verify(userService).createUser(request);
        verify(codeService).create(user, CodeScopeEnum.EMAIL_VERIFICATION);
        verify(mailService).sendEmailVerificationCode(EMAIL, code.getCode());

        assertEquals(user.getId(), result.userId());
        assertEquals(user.getEmail(), result.email());
        assertEquals(user.getCreatedAt(), result.createdAt());
    }

    @Test
    void localRegister_shouldPropagateConflictWhenUserExists() {
        LocalRegisterRequest request = new LocalRegisterRequest(PASSWORD, PASSWORD, EMAIL);
        ApiException conflict = new ApiException("User with such an email already exists", HttpStatus.CONFLICT);
        when(userService.createUser(request)).thenThrow(conflict);

        ApiException thrown = assertThrows(ApiException.class, () -> authService.localRegister(request));

        assertEquals(HttpStatus.CONFLICT, thrown.getStatus());
        verify(userService).createUser(request);
        verifyNoInteractions(codeService, mailService);
    }

    @Test
    void signIn_shouldPropagateAuthException() {
        SignInRequestDto dto = new SignInRequestDto(EMAIL, PASSWORD);
        ApiException unauthorized = new ApiException("Bad credentials", HttpStatus.UNAUTHORIZED);
        when(authenticationManager.authenticate(any(Authentication.class))).thenThrow(unauthorized);

        ApiException thrown = assertThrows(ApiException.class, () -> authService.signIn(dto));

        assertEquals(HttpStatus.UNAUTHORIZED, thrown.getStatus());
        verify(authenticationManager).authenticate(any(Authentication.class));
        verifyNoInteractions(refreshTokenService, jwtService);
    }

    @Test
    void signIn_shouldAuthenticateAndReturnTokenPair() {
        SignInRequestDto dto = new SignInRequestDto(EMAIL, PASSWORD);
        CustomUserDetails principal = new CustomUserDetails(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        ArgumentCaptor<Authentication> captor = ArgumentCaptor.forClass(Authentication.class);

        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(refreshTokenService.create(user)).thenReturn(REFRESH_TOKEN);
        when(jwtService.generateToken(user.getId().toString(), user.getRole())).thenReturn(ACCESS_TOKEN);

        TokenPairDto result = authService.signIn(dto);

        verify(authenticationManager).authenticate(captor.capture());
        verify(refreshTokenService).create(user);
        verify(jwtService).generateToken(user.getId().toString(), user.getRole());

        Authentication authRequest = captor.getValue();
        assertEquals(dto.email(), authRequest.getPrincipal());
        assertEquals(dto.password(), authRequest.getCredentials());
        assertEquals(ACCESS_TOKEN, result.accessToken());
        assertEquals(REFRESH_TOKEN, result.refreshToken());
    }

    @Test
    void refreshTokens_shouldValidateRefreshTokenAndIssueNewPair() {
        RefreshToken storedToken = RefreshToken.builder()
                .token("hashed")
                .user(user)
                .expiresAt(Instant.now().plusSeconds(100))
                .build();

        when(refreshTokenService.validate(REFRESH_TOKEN)).thenReturn(storedToken);
        when(refreshTokenService.create(user)).thenReturn("new-refresh");
        when(jwtService.generateToken(user.getId().toString(), user.getRole())).thenReturn("new-access");

        TokenPairDto result = authService.refreshTokens(REFRESH_TOKEN);

        verify(refreshTokenService).validate(REFRESH_TOKEN);
        verify(refreshTokenService).create(user);
        verify(jwtService).generateToken(user.getId().toString(), user.getRole());
        assertEquals("new-access", result.accessToken());
        assertEquals("new-refresh", result.refreshToken());
    }

    @Test
    void logout_shouldDelegateDeletionToRefreshTokenService() {
        authService.logout(REFRESH_TOKEN);

        verify(refreshTokenService).deleteRefreshToken(REFRESH_TOKEN);
        verifyNoMoreInteractions(refreshTokenService);
    }
}
