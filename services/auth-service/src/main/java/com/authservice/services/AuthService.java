package com.authservice.services;

import com.authservice.config.security.model.CustomUserDetails;
import com.authservice.dto.TokenPairDto;
import com.authservice.dto.request.LocalRegisterRequest;
import com.authservice.dto.request.SignInRequestDto;
import com.authservice.dto.response.CreateLocalUserResponse;
import com.authservice.entity.Code;
import com.authservice.entity.CodeScopeEnum;
import com.authservice.entity.User;
import com.authservice.mapper.RoleMapper;
import com.authservice.services.code.CodeCreationService;
import com.authservice.services.user.LocalUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final LocalUserService localUserService;
    private final RoleMapper roleMapper;
    private final CodeCreationService codeCreationService;
    private final MailService mailService;


    @Transactional
    public CreateLocalUserResponse localRegister(LocalRegisterRequest dto){
        User user = localUserService.createLocalUser(dto);
        Code code = codeCreationService.getCode(user, CodeScopeEnum.EMAIL_VERIFICATION);

        mailService.sendEmailVerificationCode(user.getEmail(), code.getCode());

        return new CreateLocalUserResponse(
                user.getId(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }

    @Transactional
    public TokenPairDto signIn(SignInRequestDto dto) {
        Authentication authReq = new UsernamePasswordAuthenticationToken(dto.email(), dto.password());
        Authentication authenticatedUser = authenticationManager.authenticate(authReq);
        CustomUserDetails customUserDetails = (CustomUserDetails) authenticatedUser.getPrincipal();

        return getTokenPair(customUserDetails, dto.deviceId());
    }

    public TokenPairDto refreshTokens(String refreshToken, String deviceId) {
        return tokenService.refreshTokens(refreshToken, deviceId);
    }

    public void logout(String refreshToken, String deviceId) {
        tokenService.logout(refreshToken, deviceId);
    }


    private TokenPairDto getTokenPair(CustomUserDetails customUserDetails, String deviceId) {
        long userId = customUserDetails.getUserId();
        List<String> roles = roleMapper.toRoleNames(customUserDetails.getAuthorities());

        return tokenService.getTokenPair(userId, roles, deviceId);
    }
}
