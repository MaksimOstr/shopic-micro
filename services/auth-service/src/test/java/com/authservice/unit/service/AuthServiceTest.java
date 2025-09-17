package com.authservice.unit.service;

import com.authservice.config.security.model.CustomUserDetails;
import com.authservice.dto.LocalRegisterResult;
import com.authservice.dto.TokenPairDto;
import com.authservice.dto.request.LocalRegisterRequest;
import com.authservice.dto.request.SignInRequestDto;
import com.authservice.entity.Code;
import com.authservice.entity.CodeScopeEnum;
import com.authservice.entity.Role;
import com.authservice.entity.User;
import com.authservice.exceptions.AlreadyExistsException;
import com.authservice.mapper.RoleMapper;
import com.authservice.services.AuthService;
import com.authservice.services.MailService;
import com.authservice.services.TokenService;
import com.authservice.services.code.CodeCreationService;
import com.authservice.services.user.LocalUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenService tokenService;

    @Mock
    private LocalUserService localUserService;

    @Spy
    private RoleMapper roleMapper;

    @Mock
    private CodeCreationService codeCreationService;

    @Mock
    private MailService mailService;

    @InjectMocks
    private AuthService authService;


    private static final String PASSWORD = "masterkey";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String CODE_VALUE = "testCode";
    private static final String DEVICE_ID = "deviceId";
    private static final String EMAIL = "test@gmail.com";
    private static final long USER_ID = 1;
    private static final Set<GrantedAuthority> AUTHORITIES = Set.of(
            new SimpleGrantedAuthority("ROLE_USER")
    );
    private static final List<String> ROLE_NAMES = List.of("ROLE_USER");
    private static final String ACCESS_TOKEN = "access_token";
    private static final String REFRESH_TOKEN = "refresh_token";

    private static final TokenPairDto TOKEN_PAIR_DTO = new TokenPairDto(ACCESS_TOKEN, REFRESH_TOKEN);
    private static final LocalRegisterRequest LOCAL_REGISTER_REQUEST = new LocalRegisterRequest(
            PASSWORD,
            EMAIL,
            LAST_NAME,
            LAST_NAME
    );
    private static final SignInRequestDto SIGN_IN_REQUEST_DTO = new SignInRequestDto(EMAIL, PASSWORD, DEVICE_ID);


    private User user;

    @BeforeEach
    public void setUp() {
        Role role = Role.builder()
                .name("ROLE_USER")
                .build();

        Set<Role> roles = Set.of(role);

        user = User.builder()
                .id(USER_ID)
                .password(PASSWORD)
                .email(EMAIL)
                .firstName(FIRST_NAME)
                .isNonBlocked(true)
                .isVerified(false)
                .lastName(LAST_NAME)
                .createdAt(Instant.now())
                .roles(roles)
                .build();
    }


    @Test
    public void testLocalRegister_whenCalled_thenReturnResult() {
        Code code = Code.builder()
                .code(CODE_VALUE)
                .user(user)
                .build();

        when(localUserService.createLocalUser(any(LocalRegisterRequest.class))).thenReturn(user);
        when(codeCreationService.getCode(any(User.class), any(CodeScopeEnum.class))).thenReturn(code);

        LocalRegisterResult result = authService.localRegister(LOCAL_REGISTER_REQUEST);

        verify(localUserService).createLocalUser(LOCAL_REGISTER_REQUEST);
        verify(codeCreationService).getCode(user, CodeScopeEnum.EMAIL_VERIFICATION);
        verify(mailService).sendEmailVerificationCode(user.getEmail(), code.getCode());

        assertEquals(result.userId(), user.getId());
        assertEquals(result.email(), user.getEmail());
        assertEquals(result.createdAt(), user.getCreatedAt());
    }

    @Test
    public void testLocalRegister_whenUserAlreadyExists_thenThrowsException() {
        when(localUserService.createLocalUser(any(LocalRegisterRequest.class))).thenThrow(new AlreadyExistsException(""));

        assertThrows(AlreadyExistsException.class, () -> authService.localRegister(LOCAL_REGISTER_REQUEST));

        verify(localUserService).createLocalUser(LOCAL_REGISTER_REQUEST);
        verifyNoInteractions(codeCreationService, mailService);
    }

    @Test
    public void testSignIn_whenCorrectCredentials_thenReturnResult() {
        CustomUserDetails userDetails = new CustomUserDetails(user);
        Authentication authRequest = new UsernamePasswordAuthenticationToken(userDetails, null, AUTHORITIES);
        ArgumentCaptor<Authentication> authenticationArgumentCaptor = ArgumentCaptor.forClass(Authentication.class);

        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authRequest);
        when(tokenService.getTokenPair(anyLong(), anyList(), anyString())).thenReturn(TOKEN_PAIR_DTO);

        TokenPairDto result = authService.signIn(SIGN_IN_REQUEST_DTO);

        verify(authenticationManager).authenticate(authenticationArgumentCaptor.capture());
        verify(roleMapper).toRoleNames(authRequest.getAuthorities());
        verify(tokenService).getTokenPair(USER_ID, ROLE_NAMES, DEVICE_ID);

        Authentication authentication = authenticationArgumentCaptor.getValue();

        assertEquals(SIGN_IN_REQUEST_DTO.email(), authentication.getPrincipal());
        assertEquals(SIGN_IN_REQUEST_DTO.password(), authentication.getCredentials());
        assertEquals(ACCESS_TOKEN, result.accessToken());
        assertEquals(REFRESH_TOKEN, result.refreshToken());
    }

    @Test
    public void testSignIn_whenInvalidCredentials_thenThrowsException() {
        when(authenticationManager.authenticate(any(Authentication.class))).thenThrow(UsernameNotFoundException.class);

        assertThrows(UsernameNotFoundException.class, () -> authService.signIn(SIGN_IN_REQUEST_DTO));

        verify(authenticationManager).authenticate(any(Authentication.class));
        verifyNoInteractions(roleMapper, tokenService);
    }


    @Test
    public void testRefreshToken_whenCalled_thenReturnResult() {
        when(tokenService.refreshTokens(anyString(), anyString())).thenReturn(TOKEN_PAIR_DTO);

        TokenPairDto result = authService.refreshTokens(REFRESH_TOKEN, DEVICE_ID);

        verify(tokenService).refreshTokens(REFRESH_TOKEN, DEVICE_ID);

        assertEquals(ACCESS_TOKEN, result.accessToken());
        assertEquals(REFRESH_TOKEN, result.refreshToken());
    }

    @Test
    public void testLogout_whenCalled_thenReturnResult() {
        authService.logout(REFRESH_TOKEN, DEVICE_ID);

        verify(tokenService).logout(REFRESH_TOKEN, DEVICE_ID);
    }
}
